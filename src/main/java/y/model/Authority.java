package y.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Data;

/**
 *
 * @author Kent Yeh
 */
@Data
@Entity
@EqualsAndHashCode(of = "aid", callSuper = false)
public class Authority implements Serializable {

    private static final long serialVersionUID = -7454760999684175357L;
    @Column
    private long aid = -1;
    @Column
    @NotNull
    private String authority;
    @Column
    private String account;

    public Authority() {
    }

    public Authority(int aid, String authority, String account) {
        this.aid = aid;
        this.authority = authority;
        this.account = account;
    }

    public boolean same(Authority other) {
        return other == null ? false : this.authority.equals(other.getAuthority());
    }
}
