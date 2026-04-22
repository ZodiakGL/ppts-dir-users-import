package ru.sfera.users.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.sfera.users.model.ActivityStatus;
import ru.sfera.users.model.User;

import java.util.Collection;
import java.util.List;


@RequiredArgsConstructor
public class JdbcUserRepository implements UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final String usersTable;

    @Override
    public void saveAll(Collection<User> users) {
        if (users == null || users.isEmpty()) {
            return;
        }
        var args = users.stream()
            .map(user -> new Object[]{
                user.getLogin(), user.getFirstName(), user.getPatronymic(),
                user.getLastName(), user.getEmail(), user.getColor(), user.getActivityStatus().toString()
            })
            .toList();
        jdbcTemplate.batchUpdate(("INSERT INTO %s (login, first_name, patronymic, last_name, email, color, activity_status)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?)").formatted(usersTable), args);
    }

    @Override
    public void updateAll(Collection<User> users) {
        if (users == null || users.isEmpty()) {
            return;
        }
        var args = users.stream()
            .map(user -> new Object[]{
                user.getFirstName(), user.getPatronymic(), user.getLastName(),
                user.getEmail(), user.getColor(), user.getActivityStatus().toString(), user.getLogin()
            })
            .toList();
        jdbcTemplate.batchUpdate("UPDATE %s SET ".formatted(usersTable) +
            "first_name = ?, patronymic = ?, last_name = ?, email = ?, color = ?, activity_status = ? WHERE lower(login) = lower(?)", args);
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM %s".formatted(usersTable), (rs, rowNum) -> User.builder()
            .id(rs.getLong("id"))
            .login(rs.getString("login"))
            .firstName(rs.getString("first_name"))
            .patronymic(rs.getString("patronymic"))
            .lastName(rs.getString("last_name"))
            .email(rs.getString("email"))
            .color(rs.getString("color"))
            .activityStatus(ActivityStatus.valueOf(rs.getString("activity_status")))
            .build()
        );
    }


    @Override
    public void markAsInactive(Collection<String> userLogins) {
        var args = userLogins.stream()
            .map(login -> new Object[]{ActivityStatus.I.toString(), login})
            .toList();
        jdbcTemplate.batchUpdate("UPDATE %s SET activity_status = ? WHERE lower(login) = lower(?)".formatted(usersTable), args);
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.execute("DELETE FROM %s".formatted(usersTable));
    }

    @Override
    public void deleteAllByLogins(Collection<String> logins) {
        var args = logins.stream()
            .map(login -> new Object[] { login })
            .toList();
        jdbcTemplate.batchUpdate("DELETE FROM %s WHERE lower(login) = lower(?)".formatted(usersTable), args);
    }

}
