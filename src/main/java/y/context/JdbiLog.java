package y.context;

import lombok.extern.log4j.Log4j2;
import org.skife.jdbi.v2.logging.FormattedLog;

/**
 *
 * @author Kent Yeh
 */
@Log4j2
public class JdbiLog extends FormattedLog {


    @Override
    protected boolean isEnabled() {
        return true;
    }

    @Override
    protected void log(String string) {
        log.debug(string);
    }
}
