package ru.sfera.users.service;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Iterables;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.sfera.users.dir.config.properties.DirProperties;
import ru.sfera.users.dir.config.properties.RoleProperty;
import ru.sfera.users.dir.config.properties.UsersImportProperties;
import ru.sfera.users.dir.dto.DirUserResponseDto;
import ru.sfera.users.dir.service.DirUserService;
import ru.sfera.users.mapper.UserMapper;
import ru.sfera.users.model.User;
import ru.sfera.users.repository.UserRepository;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;


@Slf4j
@RequiredArgsConstructor
public class ImportService {

    private final DirUserService dirUserService;
    private final ImportRoleService importRoleService;
    private final ColorService colorService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final UsersImportProperties properties;
    private final List<ImportLifecycleProcessor> lifecycleProcessors;


    @Transactional
    public void importUsersFromDir() {
        var ignoreUsers = properties.getIgnoreUsers();
        var existingUsers = userRepository.findAll()
            .stream()
            .filter(user -> !ignoreUsers.contains(user.getLogin()))
            .collect(toMap(user -> user.getLogin().toLowerCase(), identity()));

        var continueOnError = properties.isContinueOnError();
        var markUsersAsInactive = properties.isMarkUsersAsInactive();
        var usersToAddAcc = new LinkedHashMap<String, User>();
        var usersToUpdateAcc = new LinkedHashMap<String, User>();
        var processedDirUserLoginsAcc = new LinkedHashSet<String>();
        var userRolesMapAcc = new LinkedHashMap<String, Set<String>>();

        properties.getDir()
            .stream()
            .filter(DirProperties::isEnabled)
            .forEach(dirProperties ->
                dirProperties.getImports()
                    .getRoles().stream()
                    .sorted(Comparator.comparing(RoleProperty::getPriority))
                    .forEachOrdered(role -> {
                        try {
                            processRole(role.getName(),
                                role.getDirGroups(),
                                role.getExclDirGroups(),
                                existingUsers,
                                usersToAddAcc,
                                usersToUpdateAcc,
                                processedDirUserLoginsAcc,
                                userRolesMapAcc,
                                dirProperties);
                        } catch (Exception e) {
                            log.error("Error processing role {}", role.getName(), e);
                            if (!continueOnError) {
                                throw e;
                            }
                        }
                    })
            );

        if (markUsersAsInactive) {
            markUserAsInactive(existingUsers, processedDirUserLoginsAcc);
        }

        log.info("Updating {} users", usersToUpdateAcc.size());
        userRepository.updateAll(usersToUpdateAcc.values());

        log.info("Inserting {} new users", usersToAddAcc.size());
        userRepository.saveAll(usersToAddAcc.values());

        if (!CollectionUtils.isEmpty(userRolesMapAcc)) {
            var userIterationSize = properties.getUserIterationPart();
            var usersPartition = Iterables.partition(userRolesMapAcc.entrySet(), userIterationSize);
            usersPartition.forEach(partition -> {
                var loginToRoles = partition.stream()
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                    ));
                importRoleService.updateRolesForUsers(loginToRoles);
            });
        }
        lifecycleProcessors.forEach(ImportLifecycleProcessor::after);
    }

    private void markUserAsInactive(Map<String, User> existingUsers, HashSet<String> receivedDirUserLogins) {
        var userLoginsToDeactivate = existingUsers.entrySet()
            .stream()
            .filter(entry -> entry.getValue().getActivityStatus().isActive())
            .filter(entry -> !receivedDirUserLogins.contains(entry.getKey()))
            .map(Map.Entry::getKey)
            .toList();

        log.info("Marking users {} as inactive", userLoginsToDeactivate.size());
        if (!userLoginsToDeactivate.isEmpty()) {
            userRepository.markAsInactive(userLoginsToDeactivate);
        }
    }

    private void processRole(
        String roleName,
        Set<String> groups,
        Set<String> excludeGroups,
        Map<String, User> existingUsers,
        Map<String, User> usersToAddAcc,
        Map<String, User> usersToUpdateAcc,
        Set<String> processedDirUserLoginsAcc,
        HashMap<String, Set<String>> userRolesMapAcc,
        DirProperties dirProperties
    ) {
        if (groups.isEmpty()) {
            return;
        }
        log.info("Processing role {} using groups {}", roleName, groups);

        var excludeDirUsers = excludeGroups.stream()
            .map(excludeGroup -> dirUserService.getAllUsers(excludeGroup, dirProperties))
            .flatMap(Collection::stream)
            .map(DirUserResponseDto.DirUserDto::getUpn)
            .filter(Objects::nonNull)
            .map(String::toLowerCase)
            .collect(toSet());

        var ignoreUsers = properties.getIgnoreUsers();
        for (var group : groups) {
            log.info("Processing DIR group: {}", group);
            var dirUsers = dirUserService.getAllUsers(group, dirProperties).stream()
                .filter(Objects::nonNull)
                .filter(user -> Objects.nonNull(user.getUpn()))
                .filter(user ->
                    (!processedDirUserLoginsAcc.contains(user.getUpn().toLowerCase())
                        || !excludeDirUsers.contains(user.getUpn().toLowerCase()))
                        && !ignoreUsers.contains(user.getUpn().toLowerCase()))
                .toList();
            log.info("Found {} users for DIR group: {}", dirUsers.size(), group);

            for (var dirUser : dirUsers) {
                log.debug("Processing user - {}", dirUser);

                if (!existingUsers.containsKey(dirUser.getUpn())) {
                    if (!usersToAddAcc.containsKey(dirUser.getUpn())) {
                        log.debug("Should insert new user - {}", dirUser);
                        usersToAddAcc.put(dirUser.getUpn(), userMapper.mapToActiveUser(dirUser, colorService.nextColorForUser()));
                    }
                } else {
                    var currentUser = existingUsers.get(dirUser.getUpn());
                    if (shouldUpdate(currentUser, dirUser) && !usersToUpdateAcc.containsKey(currentUser.getLogin())) {
                        log.debug("Should update attributes for user {}", dirUser);
                        usersToUpdateAcc.put(currentUser.getLogin(), userMapper.mapToActiveUser(dirUser, currentUser.getColor()));
                    }
                }
                log.debug("Preparing to add role {} to user {}", roleName, dirUser);
                addUserToUserRolesMap(userRolesMapAcc, dirUser, roleName);
                processedDirUserLoginsAcc.add(dirUser.getUpn().toLowerCase());
            }
        }
    }

    private void addUserToUserRolesMap(HashMap<String, Set<String>> userRolesMap, DirUserResponseDto.DirUserDto dirUser, String roleName) {
        String loginKey = dirUser.getUpn().toLowerCase();

        if (userRolesMap.containsKey(loginKey)) {
            userRolesMap.get(loginKey).add(roleName);
        } else {
            var roles = new HashSet<String>();
            roles.add(roleName);
            userRolesMap.put(loginKey, roles);
        }
    }

    private boolean shouldUpdate(User current, DirUserResponseDto.DirUserDto fromDir) {
        var equalContent = Objects.equals(current.getEmail(), fromDir.getEmail())
            && Objects.equals(current.getFirstName(), fromDir.getFirstName())
            && Objects.equals(current.getLastName(), fromDir.getLastName())
            && Objects.equals(current.getPatronymic(), fromDir.getMiddleName());
        return !equalContent || !current.getActivityStatus().isActive();
    }

}
