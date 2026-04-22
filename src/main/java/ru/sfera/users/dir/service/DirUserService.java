package ru.sfera.users.dir.service;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import ru.sfera.users.dir.clients.DirClient;
import ru.sfera.users.dir.config.properties.DirProperties;
import ru.sfera.users.dir.config.properties.UsersImportProperties;
import ru.sfera.users.dir.dto.DirUserResponseDto;
import ru.sfera.users.dir.dto.DirUsersRequestDto;


@Slf4j
@RequiredArgsConstructor
public class DirUserService {

    private final DirClient dirClient;
    private final UsersImportProperties importProperties;

    public List<DirUserResponseDto.DirUserDto> getAllUsers(String groupName, DirProperties properties) {
        var result = new ArrayList<DirUserResponseDto.DirUserDto>();
        int pageNumber = 0;
        var request = new DirUsersRequestDto()
            .setGroupCode(groupName)
            .setSystemCode(importProperties.getSystemCode())
            .setPageSize(properties.getImports().getPageSize());

        DirUserResponseDto users;
        do {
            request.setPageNumber(pageNumber);
            users = dirClient.getUsers(request, properties);
            if (users != null && !CollectionUtils.isEmpty(users.getContent())) {
                result.addAll(users.getContent());
                log.info("Received page {} for group {} with {} results", pageNumber, groupName, users.getContent().size());
            }
            pageNumber++;
        } while (users != null && !users.getLast());

        return result;
    }

}
