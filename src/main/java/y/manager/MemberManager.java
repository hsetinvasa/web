package y.manager;

import y.context.ValidationUtils;
import y.model.Authority;
import y.model.Dao;
import y.model.Member;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kent Yeh
 */
@Repository("memberManager")
@Log4j2
public class MemberManager extends AbstractDaoManager<String, Member> {

    @Autowired(required = false)
    @Qualifier("messageAccessor")
    @Getter
    MessageSourceAccessor messageAccessor;

    @Autowired
    ValidationUtils vu;

    @Override
    public String text2Key(String text) {
        return text;
    }

    protected Exception extractSQLException(Exception ex) {
        Throwable result = ex;
        boolean found = false;
        while (result != null) {
            if (result instanceof java.sql.SQLException) {
                found = true;
                break;
            } else if (result.getCause() == null) {
                break;
            } else {
                result = result.getCause();
            }
        }

        return found ? (java.sql.SQLException) result : ex;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public Member findByPrimaryKey(String account) {
        Dao dao = getContext().getBean(Dao.class);
        Member member = dao.findMemberByPrimaryKey(account);
        if (member != null) {
            member.setAuthorities(dao.findAuthorityByAccount(account));
        }
        return member;
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public List<Member> findAvailableUsers() throws Exception {
        return getContext().getBean(Dao.class).findAvailableUsers();
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public List<Member> findAllUsers() throws Exception {
        return getContext().getBean(Dao.class).findAllUsers();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void newMember(Member member) throws Exception {
        try {
            Dao dao = getContext().getBean(Dao.class);
            vu.validateMessage(member, RuntimeException.class);
            dao.newMember(member);
            List<Authority> authories = member.getAuthorities();
            if (authories != null) {
                for (Authority authority : authories) {
                    vu.validateMessage(authority, RuntimeException.class);
                    dao.newAuthority(authority);
                }
            }
        } catch (Exception ex) {
            log.debug("{}{}", messageAccessor.getMessage("exception.newMember"), ex.getMessage());
            throw new RuntimeException(ex.getMessage(), extractSQLException(ex));
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean updateMember(Member member) throws Exception {
        Dao dao = getContext().getBean(Dao.class);
        vu.validateMessage(member, RuntimeException.class);
        if (dao.updateMember(member) == 1) {
            List<Authority> authories = member.getAuthorities();
            if (authories != null && !authories.isEmpty()) {
                List<String> auths = new ArrayList<>();
                for (Authority authority : authories) {
                    auths.add(authority.getAuthority());
                    vu.validateMessage(authority, RuntimeException.class);
                    if (dao.findAuthorityByBean(authority) == null) {
                        log.debug("insert authority id = {}", dao.newAuthority(authority));
                    }
                }
                log.debug("Authorities remove count = {}", dao.removeAuthories(member.getAccount(), auths));
            } else {
                dao.removeAuthories(member.getAccount());
            }
            return true;
        } else {
            return false;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int updatePass(String account, String oldPass, String newPass) throws Exception {
        return getContext().getBean(Dao.class).changePasswd(account, oldPass, newPass);
    }

}
