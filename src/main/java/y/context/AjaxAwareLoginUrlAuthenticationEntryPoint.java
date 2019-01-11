package y.context;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.util.StringUtils;

/**
 *
 * @author Kent Yeh
 */
@Log4j2
public class AjaxAwareLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    @Autowired(required = false)
    @Qualifier("messageAccessor")
    MessageSourceAccessor messageAccessor;
    private String accessDenied = "Access denied! 人員未登錄，禁止存取 !";

    public AjaxAwareLoginUrlAuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        if (messageAccessor != null) {
            accessDenied = messageAccessor.getMessage("AbstractAccessDecisionManager.accessDenied", accessDenied);
        }
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            log.debug("Ajax fail owing forbidden!");
            //jetty sendError only support ISO-8859-1
            response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDenied);
        } else {
            String pathInfo = request.getServletPath();
            if (StringUtils.hasText(pathInfo) && pathInfo.contains("/json")) {
                log.debug("Ajax fail owing forbidden!");
                response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDenied);
            } else {
                super.commence(request, response, authException);
            }
        }
    }
}
