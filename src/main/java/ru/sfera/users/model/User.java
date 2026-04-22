package ru.sfera.users.model;

import lombok.Builder;
import lombok.Data;

import java.util.Objects;

@Data
@Builder
public class User {
    private Long id;
    private String login;
    private String firstName;
    private String patronymic;
    private String lastName;
    private String email;
    private String color;
    private ActivityStatus activityStatus;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return login.equalsIgnoreCase(user.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login.toLowerCase());
    }
}
