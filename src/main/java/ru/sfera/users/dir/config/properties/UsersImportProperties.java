package ru.sfera.users.dir.config.properties;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties(prefix = "sfera.import.users")
public class UsersImportProperties {

    private Set<String> ignoreUsers = new HashSet<>();
    private boolean markUsersAsInactive = true;
    private boolean deleteUserRoles = true;
    @NotNull
    @Min(1)
    private Integer userIterationPart = 1000;
    @NotEmpty
    private String systemCode;
    private Map<String, String> tableMapping;
    private List<DirProperties> dir = new ArrayList<>();
    private boolean continueOnError = false;

}
