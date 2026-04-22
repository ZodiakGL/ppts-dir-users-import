package ru.sfera.users.repository;


import java.util.Collection;
import java.util.List;

import ru.sfera.users.model.User;


public interface UserRepository {

    List<User> findAll();

    void saveAll(Collection<User> users);

    void updateAll(Collection<User> users);

    void markAsInactive(Collection<String> userLogins);

    void deleteAll();

    void deleteAllByLogins(Collection<String> logins);

}
