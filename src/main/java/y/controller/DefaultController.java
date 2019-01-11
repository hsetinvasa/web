package y.controller;

import y.context.CustomUserInfo;
import y.manager.MemberManager;
import y.model.Member;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IMap;
import java.security.Principal;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.session.MapSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Kent Yeh
 */
@Controller
@Log4j2
public class DefaultController {

    @Autowired
    private MemberManager memberManager;

    /**
     * Show how to inject hazelcast instance.<br/>
     * 示範取得 hazelcast 實例.
     */
    @Resource(name = "hazelcastInstance", type = com.hazelcast.instance.HazelcastInstanceProxy.class)
    private HazelcastInstance hazelcast;
    /**
     * Show how to inject a spring-session's map by hazelcast's name.<br/>
     * 示範使用 hazelcast 內部名稱取得 spring-session map 實例.
     */
    @Resource(name = "spring:session:sessions")
    private IMap<String, MapSession> sessionMap;
    /**
     * Show how to inject a map by identity name.<br/>
     * 使用唯一名稱插入另一個Map.
     */
    @Resource(name = "anotherMap")
    private IMap<Object, Object> anotherMap;
    /**
     * Autowire an AtomicLong variable directly.<br/>
     * 直接聯結一個 AtomicLong 變數.
     */
    @Autowired
    private IAtomicLong inStock;

    @RequestMapping("/")
    public String root(Device device, Model model) {
        if (device.isMobile()) {
            log.debug("Connect devcie is mobile");
            model.addAttribute("device", "mobile");
            //return "mobileIndex";
        } else if (device.isTablet()) {
            log.debug("Connect devcie is tablet");
            model.addAttribute("device", "tablet");
            //return "tabletIndex";
        } else {
            model.addAttribute("device", "normal");
            log.debug("Connect device is normal device.");
        }
        return "index";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public String login(Model model) throws Exception {
        model.addAttribute("members", memberManager.findAvailableUsers());
        return "login";
    }

    private String getPrincipalId(Principal principal) {
        Object p = principal instanceof AbstractAuthenticationToken ? ((AbstractAuthenticationToken) principal).getPrincipal() : principal;
        return p instanceof CustomUserInfo ? ((CustomUserInfo) p).getUsername() : principal.getName();
    }

    /**
     * Response all users' data as json.
     *
     * @return
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/admin/users", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String listuser() throws Exception {
        List<Member> users = memberManager.findAllUsers();
        if (users == null || users.isEmpty()) {
            return "{\"total\":0,\"users\":[]}";
        } else {
            StringBuilder sb = new StringBuilder("{\"total\":").append(users.size()).append(",\"users\":[");
            boolean isFirst = true;
            for (Member user : users) {
                if (!isFirst) {
                    sb.append(",");
                }
                sb.append("{\"account\":\"").append(user.getAccount()).append("\",\"name\":\"").append(user.getName())
                        .append("\",\"birthday\":\"").append(String.format("%tF", user.getBirthday())).append("\"}");
                isFirst = false;
            }
            sb.append("]}");
            return sb.toString();
        }
    }

    /**
     * Member's editor form.
     *
     * @param member
     * @param model
     * @return
     */
    @RequestMapping(value = "/member/edit/{member}", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public String editMember(@PathVariable Member member, Model model) {
        model.addAttribute("member", member);
        return "memberEditor";
    }

    /**
     * update member data.
     *
     * @param member
     * @param model
     * @return
     */
    @RequestMapping(value = "/member/update", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public String updateMember(@Valid @ModelAttribute Member member, Model model) {
        try {
            model.addAttribute("member", member);
            memberManager.updateMember(member);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            model.addAttribute("errorMsg", ex.getMessage());
        }
        return "memberEditor";
    }

    /**
     * display current user's info.
     *
     * @param request
     * @param principal
     * @return
     * @throws java.lang.Exception
     */
    @RequestMapping("/user/myinfo")
    public String myinfo(HttpServletRequest request, Principal principal) throws Exception {
        if (principal instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken upat = (UsernamePasswordAuthenticationToken) principal;
            CustomUserInfo cui = (CustomUserInfo) upat.getPrincipal();
            request.setAttribute("member", cui.getMember());
            //Alternative approach,另外一種作法
            //request.setAttribute("member", memberManager.findMemberByPrimaryKey(getPrincipalId(principal)));
        } else {
            request.setAttribute("member", memberManager.findByPrimaryKey(getPrincipalId(principal)));
        }
        return "index";
    }

    /**
     * Only adminstrator could display any user's info.
     *
     * @param account
     * @param request
     * @return
     */
    @RequestMapping("/admin/user/{account}")
    public String userinfo(@PathVariable Member account, HttpServletRequest request) {
        if (account != null) {
            request.setAttribute("member", account);
        }
        return "index";
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllException(Exception ex) {

        ModelAndView model = new ModelAndView("error");
        model.addObject("exception", ex);

        return model;

    }
}
