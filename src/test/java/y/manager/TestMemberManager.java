package y.manager;

import y.model.Member;
import y.model.TestDao;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kent Yeh
 */
@Repository("testMemberManager")
@Log4j2
public class TestMemberManager extends MemberManager {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public int countUsers() throws Exception {
        return getContext().getBean(TestDao.class).countUsers();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void raiseRollback(Member member) throws Exception {
        TestDao dao = getContext().getBean(TestDao.class);
        dao.changePasswd(member.getAccount(), member.getPassword(), "guesspass");
        member.setName(null);
        dao.updateMember(member);
    }
}
