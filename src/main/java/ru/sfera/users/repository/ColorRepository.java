package ru.sfera.users.repository;

import java.util.List;

public interface ColorRepository {

    List<String> findAllNamesByEntity(String entity);
}
