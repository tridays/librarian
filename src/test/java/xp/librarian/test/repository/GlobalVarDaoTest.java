package xp.librarian.test.repository;

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
import xp.librarian.repository.GlobalVarDao;

/**
 * @author xp
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RepositoryConfig.class})
@Transactional
@ActiveProfiles("test")
public class GlobalVarDaoTest {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalVarDaoTest.class);

    @Autowired
    private GlobalVarDao globalVarDao;

    @Test
    public void reloadTest() {
        globalVarDao.reload();
        LOG.info(globalVarDao.getAll().toString());
    }

}
