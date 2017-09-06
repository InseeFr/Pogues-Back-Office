package fr.insee.pogues.metadata.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupRepositoryImpl implements GroupRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<String> getRootIds() throws Exception {
        return jdbcTemplate.queryForList("SELECT id FROM ddi_group", String.class);
    }

}
