package ru.sfera.users.service;

import java.util.Map;
import java.util.Set;


public interface ImportRoleService {

    void updateRolesForUsers(Map<String, Set<String>> userLoginToRoles);

}
