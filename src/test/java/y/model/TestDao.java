package y.model;

import org.skife.jdbi.v2.sqlobject.SqlQuery;

/**
 *
 * @author Kent Yeh
 */
public interface TestDao extends Dao {

    @SqlQuery("SELECT count(8) FROM appmember")
    int countUsers();
}
