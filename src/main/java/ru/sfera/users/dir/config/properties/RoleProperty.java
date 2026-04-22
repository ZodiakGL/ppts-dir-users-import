package ru.sfera.users.dir.config.properties;

import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class RoleProperty {

    @NotNull
    private Integer priority = Integer.MAX_VALUE;
    @NotBlank
    private String name;
    @NotEmpty
    private Set<String> dirGroups = new HashSet<>();
    private Set<String> exclDirGroups = new HashSet<>();

}
