package y.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Kent Yeh
 */
@Entity
@NoArgsConstructor
@EqualsAndHashCode(of = "account", callSuper = false)
public class Member implements Serializable {

    private static final long serialVersionUID = 395368712192880218L;

    @NotNull(message = "{y.model.Member.account.notNull.message}")
    @Size(min = 1, message = "{y.model.Member.account.notEmpty.message}")
    @Getter
    @Setter
    @Column
    private String account;
    @NotNull(message = "{y.model.Member.passwd.notNull.message}")
    @Size(min = 1, message = "{y.model.Member.passwd.notEmpty.message}")
    @Getter
    @Setter
    @Column(name = "passwd")
    private String password;
    @NotNull(message = "{y.model.Member.name.notNull.message}")
    @Size(min = 1, message = "{y.model.Member.name.notEmpty.message}")
    @Getter
    @Setter
    @Column
    private String name;
    @Column
    private String enabled = "Y";
    @Column
    private Date birthday;
    private List<Authority> authorities;

    public Member(String account, String name) {
        this.account = account;
        this.name = name;
    }

    public String getEnabled() {
        return "Y".equals(enabled) ? "Y" : "N";
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public Date getBirthday() {
        return birthday == null ? null : new Date(birthday.getTime());
    }

    public void setBirthday(Date birthday) {
        if (birthday == null) {
            this.birthday = null;
        } else if (this.birthday == null) {
            this.birthday = new Date(birthday.getTime());
        } else {
            this.birthday.setTime(birthday.getTime());
        }
    }

    public List<Authority> getAuthorities() {
        if (authorities == null) {
            authorities = new ArrayList<>();
        }
        return authorities;
    }

    public void setAuthorities(List<Authority> authorities) {
        if (authorities == null || authorities.isEmpty()) {
            if (this.authorities != null) {
                this.authorities.clear();
            }
        } else {
            if (this.authorities == null) {
                this.authorities = new ArrayList<>(authorities.size());
            } else if(!this.authorities.isEmpty()){
                this.authorities.clear();
            }
            this.authorities.addAll(authorities);
        }
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", name, account);
    }
}
