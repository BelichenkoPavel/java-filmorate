package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.MPAStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component("dbMPAStorage")
public class DBMPAStorage implements MPAStorage {
    private final JdbcTemplate jdbcTemplate;

    String SELECT_BY_ID= "SELECT * FROM \"mpa\" WHERE ID = ?";

    String SELECT_LIST = "SELECT * FROM \"mpa\"";

    @Autowired
    public DBMPAStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public MPA getById(Long id) {
        List<MPA> result = jdbcTemplate.query(SELECT_BY_ID, this::mapToMPA, id);

        if (result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }

    @Override
    public List<MPA> getList() {
        List<MPA> result = jdbcTemplate.query(SELECT_LIST, this::mapToMPA);

        if (result.isEmpty()) {
            return null;
        }

        return result;
    }

    private MPA mapToMPA(ResultSet resultSet, int rowNum) throws SQLException {
        return MPA.builder()
                .id(resultSet.getLong("ID"))
                .name(resultSet.getString("NAME"))
                .build();
    }
}
