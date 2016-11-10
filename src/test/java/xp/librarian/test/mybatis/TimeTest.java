package xp.librarian.test.mybatis;

import java.sql.*;
import java.time.*;
import java.time.temporal.*;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import xp.librarian.config.RepositoryConfig;
import xp.librarian.model.dto.User;
import xp.librarian.repository.mapper.UserMapper;

/**
 * @author xp
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RepositoryConfig.class})
@Transactional
@ActiveProfiles("test")
public class TimeTest {

    private static final Logger LOG = LoggerFactory.getLogger(TimeTest.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SqlSession sqlSession;

    @Test
    public void now() throws SQLException {
        Instant javaNow = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        LOG.info(String.format("Java: %s %d", javaNow.toString(), javaNow.toEpochMilli()));

        Connection connection = sqlSession.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT now();");
        ResultSet resultSet = statement.executeQuery();
        while (!resultSet.isAfterLast()) {
            if (!resultSet.next()) break;
            Timestamp timestamp = resultSet.getTimestamp(1);
            Instant sqlNow = timestamp.toInstant();
            LOG.info(String.format("MySQL: %s %d", sqlNow.toString(), sqlNow.toEpochMilli()));
        }
    }

    @Test
    public void instant() {
        User where = new User().setId(1L);
        User user = userMapper.select(where, 0L, 1).stream().findFirst().orElse(null);
        if (user != null) {
            Instant instant = user.getCreateTime();
            LOG.info(String.format("%s %d", instant.toString(), instant.toEpochMilli()));
        }
    }

}
