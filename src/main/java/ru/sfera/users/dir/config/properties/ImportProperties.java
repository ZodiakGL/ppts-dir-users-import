package ru.sfera.users.dir.config.properties;


import java.util.ArrayList;
import java.util.List;

import lombok.Data;


@Data
public class ImportProperties {

    private int pageSize = 1000;
    private int retryMinBackOffMillis = 500;
    private int retryMaxAttempts = 3;
    private List<RoleProperty> roles = new ArrayList<>();

}
