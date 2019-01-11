package y.context;

import y.manager.MemberManager;
import y.model.Authority;
import y.model.Member;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.stereotype.Service;

/**
 * Change
 * {@link UsernamePasswordAuthenticationToken UsernamePasswordAuthenticationToken}
 * to {@link OpenIDAuthenticationToken OpenIDAuthenticationToken} if intend to
 * use OpenId.
 * 如果要用OpenId，請將{@link UsernamePasswordAuthenticationToken UsernamePasswordAuthenticationToken}
 * 換成{@link OpenIDAuthenticationToken OpenIDAuthenticationToken}
 *
 * @author Kent Yeh
 */
@Service("customUserService")
@Log4j2
public class CustomUserService implements UserDetailsService, AuthenticationUserDetailsService<UsernamePasswordAuthenticationToken> {

    @Autowired
    private MemberManager memberManager;

    /**
     * {@link DaoAuthenticationProvider DaoAuthenticationProvider} load
     * userDetails to compare user's password.<br>
     * {@link DaoAuthenticationProvider DaoAuthenticationProvider}叫用本函式取得用戶資料以比對密碼
     *
     * @param account
     * @return
     */
    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        //Find user data,找到用戶資料
        try {
            Member member = memberManager.findByPrimaryKey(account);
            //Decide user's roles,自行決定如何給角色
            if (member == null) {
                throw new UsernameNotFoundException("");
            }
            StringBuilder roles = null;
            for (Authority authority : member.getAuthorities()) {
                if (roles == null) {
                    roles = new StringBuilder(authority.getAuthority());
                } else {
                    roles.append(",").append(authority.getAuthority());
                }
            }
            if (roles == null) {
                return new CustomUserInfo(member, "");
            } else {
                return new CustomUserInfo(member, roles.toString());
            }
        } catch (UsernameNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new UsernameNotFoundException(ex.getMessage(), ex);
        }
    }

    /**
     * Not used by
     * {@link DaoAuthenticationProvider DaoAuthenticationProvider}.<br>
     * {@link DaoAuthenticationProvider DaoAuthenticationProvider}目前用不到這個函式.
     * 3.1以後OpenId真正叫用這個函式
     *
     * @param token
     * @return
     */
    @Override
    public UserDetails loadUserDetails(UsernamePasswordAuthenticationToken token) throws UsernameNotFoundException {
        return loadUserByUsername(token.getName());
    }
}
