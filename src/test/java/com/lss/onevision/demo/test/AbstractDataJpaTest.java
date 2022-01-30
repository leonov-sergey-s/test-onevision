package com.lss.onevision.demo.test;

import com.lss.onevision.demo.annotation.JdbcTemplateRowMapper;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Tag("DataJpaTest")
@ExtendWith(SpringExtension.class)
@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION,
        classes = {Repository.class, JdbcTemplateRowMapper.class}))
public abstract class AbstractDataJpaTest implements WithAssertions {
    @Autowired
    protected JdbcTemplate jdbcTemplate;
}
