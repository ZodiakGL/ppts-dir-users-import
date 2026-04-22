package ru.sfera.users.dir.config.properties;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class DirProperties {

    private boolean enabled = true;
    @NotNull
    private ConnectionProperties connection;
    @NotNull
    private ImportProperties imports;

}
