package ru.sfera.users.dir.config.properties;

import lombok.Data;


@Data
public class ConnectionProperties {

    private String username;
    private String password;
    private String url;
    private String authProfile;

}
