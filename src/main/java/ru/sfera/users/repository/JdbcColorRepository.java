package ru.sfera.users.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;


@RequiredArgsConstructor
public class JdbcColorRepository implements ColorRepository {

    private final JdbcTemplate jdbcTemplate;
    private final String colorsTable;

    @Override
    public List<String> findAllNamesByEntity(String entity) {
        return jdbcTemplate.query("SELECT c.name FROM %s c WHERE c.entity = ?".formatted(colorsTable),
            (rs, rowNum) -> rs.getString("name"), entity);
    }

}
