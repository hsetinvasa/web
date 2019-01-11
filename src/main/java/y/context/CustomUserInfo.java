package y.context;

import y.model.Member;
import java.util.ArrayList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

/**
 *
 * @author Kent Yeh
 */
@EqualsAndHashCode(of = "member", callSuper = false)
public class CustomUserInfo extends User {

    private static final long serialVersionUID = -2209416924912982094L;

    @Getter
    private final Member member;

    public CustomUserInfo(Member member, String roles) {
        super(member.getAccount(), member.getPassword(), true, true, true, true, roles == null || roles.isEmpty()
                ? new ArrayList<GrantedAuthority>(0) : AuthorityUtils.commaSeparatedStringToAuthorityList(roles));
        this.member = member;
    }

    @Override
    public boolean isEnabled() {
        return "Y".equals(member.getEnabled());
    }

    @Override
    public String getUsername() {
        return member == null ? super.getUsername() : member.getAccount();
    }

    @Override
    public String getPassword() {
        return member == null ? super.getPassword() : member.getPassword();
    }
    @Override
    public String toString() {
        return member.toString();
    }

}
