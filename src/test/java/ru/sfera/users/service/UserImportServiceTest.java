package ru.sfera.users.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.sfera.users.config.TestcontainersDbTest;
import ru.sfera.users.config.WireMockInitializer;
import ru.sfera.users.dir.config.properties.UsersImportProperties;
import ru.sfera.users.dir.dto.DirUsersRequestDto;
import ru.sfera.users.model.ActivityStatus;
import ru.sfera.users.model.User;
import ru.sfera.users.repository.UserRepository;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;

import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathTemplate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


public class UserImportServiceTest extends TestcontainersDbTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImportService importService;
    @Autowired
    private UsersImportProperties importProperties;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    ImportRoleService roleService;
    @Captor
    private ArgumentCaptor<Map<String, Set<String>>> rolesCaptor;

    @BeforeEach
    void init() throws IOException {
        var role1Batch1 = new ClassPathResource("ldap1/role1_batch1.json");
        var role1Batch2 = new ClassPathResource("ldap1/role1_batch2.json");
        var role2 = new ClassPathResource("ldap1/role2.json");
        try (var is1 = role1Batch1.getInputStream();
             var is2 = role1Batch2.getInputStream();
             var is3 = role2.getInputStream()
        ) {
            var request = new DirUsersRequestDto()
                .setSystemCode(importProperties.getSystemCode())
                .setGroupCode("dir1Sys1Role1")
                .setPageNumber(0)
                .setPageSize(3);
            WireMockInitializer.WIRE_MOCK_SERVER1.stubFor(
                put(urlPathTemplate("/api/v1/public/groups/users/search"))
                    .withRequestBody(equalToJson(objectMapper.writeValueAsString(request)))
                    .willReturn(aResponse().withBody(is1.readAllBytes()).withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE))
            );
            request = new DirUsersRequestDto()
                .setSystemCode(importProperties.getSystemCode())
                .setGroupCode("dir1Sys1Role1")
                .setPageNumber(1)
                .setPageSize(3);
            WireMockInitializer.WIRE_MOCK_SERVER1.stubFor(
                put(urlPathTemplate("/api/v1/public/groups/users/search"))
                    .withRequestBody(equalToJson(objectMapper.writeValueAsString(request)))
                    .willReturn(aResponse().withBody(is2.readAllBytes()).withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE))
            );
            request = new DirUsersRequestDto()
                .setSystemCode(importProperties.getSystemCode())
                .setGroupCode("dir1Sys1Role2")
                .setPageNumber(0)
                .setPageSize(3);
            WireMockInitializer.WIRE_MOCK_SERVER1.stubFor(
                put(urlPathTemplate("/api/v1/public/groups/users/search"))
                    .withRequestBody(equalToJson(objectMapper.writeValueAsString(request)))
                    .willReturn(aResponse().withBody(is3.readAllBytes()).withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE))
            );
        }

        var role1 = new ClassPathResource("ldap2/role1.json");
        role2 = new ClassPathResource("ldap2/role3.json");
        try (var is1 = role1.getInputStream();
             var is2 = role2.getInputStream()
        ) {
            var request = new DirUsersRequestDto()
                .setSystemCode(importProperties.getSystemCode())
                .setGroupCode("dir2Sys1Role1")
                .setPageNumber(0)
                .setPageSize(5);
            WireMockInitializer.WIRE_MOCK_SERVER2.stubFor(
                put(urlPathTemplate("/api/v1/public/groups/users/search"))
                    .withRequestBody(equalToJson(objectMapper.writeValueAsString(request)))
                    .willReturn(aResponse().withBody(is1.readAllBytes()).withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE))
            );
            request = new DirUsersRequestDto()
                .setSystemCode(importProperties.getSystemCode())
                .setGroupCode("dir2Sys1Role2")
                .setPageNumber(0)
                .setPageSize(5);
            WireMockInitializer.WIRE_MOCK_SERVER2.stubFor(
                put(urlPathTemplate("/api/v1/public/groups/users/search"))
                    .withRequestBody(equalToJson(objectMapper.writeValueAsString(request)))
                    .willReturn(aResponse().withBody(is2.readAllBytes()).withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE))
            );
        }
        WireMockInitializer.WIRE_MOCK_SERVER1.stubFor(
            post("/api/auth/openid-connect/token")
                .withRequestBody(containing("grant_type=password"))
                .withRequestBody(containing("username=tech_tk"))
                .willReturn(
                    okJson("""
                           {"access_token":"mock-token","refresh_token":"mock-token","token_type":"bearer","expires_in":3600}
                           """))
        );
    }

    @AfterEach
    void afterEach() {
        var users = userRepository.findAll().stream()
            .map(User::getLogin)
            .filter(login -> !"sfera_tech_user".equals(login))
            .toList();
        userRepository.deleteAllByLogins(users);
    }

    @Test
    void importNewUsers() {
        var usersBeforeImport = userRepository.findAll();
        assertThat(usersBeforeImport).hasSize(1);

        importService.importUsersFromDir();

        var usersAfterImport = userRepository.findAll();

        assertThat(usersAfterImport).hasSize(12);
        assertThat(usersAfterImport).extracting(User::getLogin)
            .containsExactlyInAnyOrder(
                "pppl_test",
                "ikuzovlev_tk",
                "rbeskrovnyy_tk",
                "aslebedev_tk",
                "vmorozov_tk",
                "m.malovitsin",
                "dzhalnin",
                "pplt_autotest",
                "adembitskaya",
                "vdranitsyna",
                "i.matasov",
                "sfera_tech_user");
        assertThat(usersAfterImport).extracting(User::getActivityStatus).containsOnly(ActivityStatus.A);
        verify(roleService, times(2)).updateRolesForUsers(rolesCaptor.capture());
        var captured = rolesCaptor.getAllValues();
        var rolesMapping = captured.stream().flatMap(map -> map.entrySet().stream())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue
            ));
        assertThat(rolesMapping).hasSize(11);

        assertThat(rolesMapping.entrySet()
            .stream()
            .filter(entry -> entry.getValue().equals(Set.of("ROLE_1")))
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet())).containsExactlyInAnyOrder(
            "pppl_test",
            "aslebedev_tk",
            "vmorozov_tk",
            "vdranitsyna",
            "i.matasov"
        );
        assertThat(rolesMapping.entrySet()
            .stream()
            .filter(entry -> entry.getValue().equals(Set.of("ROLE_1", "ROLE_2")))
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet())).containsExactlyInAnyOrder(
            "ikuzovlev_tk",
            "rbeskrovnyy_tk"
        );
        assertThat(rolesMapping.entrySet()
            .stream()
            .filter(entry -> entry.getValue().equals(Set.of("ROLE_2")))
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet())).containsExactlyInAnyOrder(
            "m.malovitsin"
        );
        assertThat(rolesMapping.entrySet()
            .stream()
            .filter(entry -> entry.getValue().equals(Set.of("ROLE_1", "ROLE_3")))
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet())).containsExactlyInAnyOrder(
            "dzhalnin",
            "adembitskaya"
        );
        assertThat(rolesMapping.entrySet()
            .stream()
            .filter(entry -> entry.getValue().equals(Set.of("ROLE_3")))
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet())).containsExactlyInAnyOrder("pplt_autotest");
    }

    @Test
    void importNewUsersTwoLaunchesWithDeletionAfterFirstImport() {
        var usersBeforeImport = userRepository.findAll();
        assertThat(usersBeforeImport).hasSize(1);
        importService.importUsersFromDir();

        var usersAfterImport = userRepository.findAll();
        assertThat(usersAfterImport).hasSize(12);
        assertThat(usersAfterImport).extracting(User::getLogin)
            .containsExactlyInAnyOrder(
                "pppl_test",
                "ikuzovlev_tk",
                "rbeskrovnyy_tk",
                "aslebedev_tk",
                "vmorozov_tk",
                "m.malovitsin",
                "dzhalnin",
                "pplt_autotest",
                "adembitskaya",
                "vdranitsyna",
                "i.matasov",
                "sfera_tech_user");

        var logins = new HashSet<String>();
        logins.add("dzhalnin");
        logins.add("vdranitsyna");
        logins.add("vmorozov_tk");
        userRepository.deleteAllByLogins(logins);

        usersBeforeImport = userRepository.findAll();
        assertThat(usersBeforeImport).hasSize(9);

        importService.importUsersFromDir();

        assertThat(usersAfterImport).hasSize(12);
        assertThat(usersAfterImport).extracting(User::getLogin)
            .containsExactlyInAnyOrder(
                "pppl_test",
                "ikuzovlev_tk",
                "rbeskrovnyy_tk",
                "aslebedev_tk",
                "vmorozov_tk",
                "m.malovitsin",
                "dzhalnin",
                "pplt_autotest",
                "adembitskaya",
                "vdranitsyna",
                "i.matasov",
                "sfera_tech_user");
    }

    @Test
    void importWithOneUserAlreadyExists() {
        userRepository.saveAll(List.of(User.builder()
            .login("pppl_test")
            .firstName("Не заполнено")
            .lastName("Не заполнено")
            .patronymic("Не заполнено")
            .activityStatus(ActivityStatus.A)
            .build()
        ));

        var usersBeforeImport = userRepository.findAll();

        importService.importUsersFromDir();

        var usersAfterImport = userRepository.findAll();

        assertThat(usersBeforeImport).hasSize(2);
        assertThat(usersAfterImport).hasSize(12);

        assertThat(usersAfterImport).extracting(User::getLogin)
            .containsExactlyInAnyOrder(
                "pppl_test",
                "ikuzovlev_tk",
                "rbeskrovnyy_tk",
                "aslebedev_tk",
                "vmorozov_tk",
                "m.malovitsin",
                "dzhalnin",
                "pplt_autotest",
                "adembitskaya",
                "vdranitsyna",
                "i.matasov",
                "sfera_tech_user");
        assertThat(usersAfterImport).extracting(User::getFirstName)
            .containsExactlyInAnyOrder(
                "pppl_test",
                "Иван",
                "Roman",
                "Александр",
                "Vyacheslav",
                "Максим",
                "Дмитрий",
                "Анастасия",
                "Вероника",
                "Иван",
                "Autotester",
                "Пользователь");
        var updatedUser = userRepository.findAll()
            .stream()
            .filter(user -> user.getLogin().equals("pppl_test"))
            .findFirst()
            .orElseThrow();

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getFirstName()).isEqualTo("pppl_test");
        assertThat(updatedUser.getLastName()).isEqualTo("pppl_test");
    }



    @Test
    void markExtraUserAsInactive() {
        userRepository.saveAll(List.of(User.builder()
            .login("sfr0000005")
            .firstName("Фома")
            .lastName("Киняев")
            .patronymic("Иванович")
            .activityStatus(ActivityStatus.A)
            .build()
        ));
        userRepository.saveAll(List.of(User.builder()
            .login("aslebedev_tk")
            .firstName("Александр")
            .lastName("Лебедев")
            .activityStatus(ActivityStatus.A)
            .build()
        ));

        importService.importUsersFromDir();

        var extraUser = userRepository.findAll()
            .stream()
            .filter(user -> "sfr0000005".equals(user.getLogin()))
            .findFirst()
            .orElseThrow();
        var existingNormalUser = userRepository.findAll()
            .stream()
            .filter(user -> "aslebedev_tk".equals(user.getLogin()))
            .findFirst()
            .orElseThrow();

        assertThat(extraUser.getActivityStatus()).isEqualTo(ActivityStatus.I);
        assertThat(existingNormalUser.getActivityStatus()).isEqualTo(ActivityStatus.A);
    }

    @Test
    void skipInactiveMarkIfUserActivityStatusIsTest() {
        userRepository.saveAll(List.of(User.builder()
            .login("sfr0000005")
            .firstName("Фома")
            .lastName("Киняев")
            .patronymic("Иванович")
            .activityStatus(ActivityStatus.T)
            .build()
        ));

        importService.importUsersFromDir();

        var extraUser = userRepository.findAll()
            .stream()
            .filter(user -> "sfr0000005".equals(user.getLogin()))
            .findFirst()
            .orElseThrow();

        assertThat(extraUser.getActivityStatus()).isEqualTo(ActivityStatus.T);
    }

}
