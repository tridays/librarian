package xp.librarian.test.repository;

import java.util.*;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import xp.librarian.config.RepositoryConfig;
import xp.librarian.model.dto.User;
import xp.librarian.repository.UserDao;

/**
 * @author xp
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RepositoryConfig.class})
@Transactional
@ActiveProfiles("test")
public class UserDaoTest {

    @Autowired
    private UserDao userDao;

    @Test
    public void test() {
        User insertUser = new User()
                .setUsername("mock")
                .setPassword("mock")
                .setStatus(User.Status.NORMAL);
        Assert.assertEquals(1, userDao.add(insertUser));
        Assert.assertNotEquals(0, insertUser.getId().intValue());

        User where = new User().setId(insertUser.getId());
        Assert.assertEquals(insertUser, userDao.get(insertUser.getId()));
        Assert.assertEquals(insertUser, userDao.get(where));
        Assert.assertEquals(Collections.singletonList(insertUser), userDao.gets(where, 0L, 1));

        User set = new User()
                .setStatus(User.Status.DELETED);
        Assert.assertEquals(1, userDao.update(where, set));

        Assert.assertNull(userDao.get(where));
        Assert.assertEquals(insertUser, userDao.get(insertUser.getId(), true));
        Assert.assertEquals(insertUser, userDao.get(where, true));
        Assert.assertEquals(Collections.singletonList(insertUser), userDao.gets(where, 0L, 1, true));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void addFailedTest() {
        User insertUser = new User();
        userDao.add(insertUser);
    }

}
