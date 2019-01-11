package y.controller;

import y.manager.TestMemberManager;
import y.model.Member;
import lombok.extern.log4j.Log4j2;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author kent
 */
@WebAppConfiguration
@ContextConfiguration(classes = y.context.TestContext.class)
@Log4j2
public class TestDefaultController extends AbstractTestNGSpringContextTests {

    @Autowired
    WebApplicationContext wac;
    private MockMvc mockMvc;
    @Autowired
    private TestMemberManager memberManager;

    @BeforeClass
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).alwaysExpect(status().isOk()).build();
    }

    @Test
    void testDevice() throws Exception {
        mockMvc.perform(get("/").param("device", "mobile")).andExpect(view().name("index")).andExpect(model().attribute("device", is(equalTo("mobile"))));
    }

    @Test
    public void testListuser() throws Exception {
        mockMvc.perform(post("/admin/users").with(user("admin").roles("ADMIN"))).andDo(print()).andExpect(jsonPath("$.total", is(equalTo(memberManager.countUsers()))));
    }

    @Test
    public void testMyinfo() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/user/myinfo").principal(new TestingAuthenticationToken("admin", null))).andReturn();
        Member member = (Member) mvcResult.getRequest().getAttribute("member");
        log.debug("My account is \"{}\" and my name is {}", member.getAccount(), member.getName());
        assertThat("Test UserInfo error ", member.getAccount(), is(equalTo("admin")));
    }

    @Test
    public void testUserInfo() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/admin/user/{account}", "admin").with(user("admin").roles("ADMIN"))).andReturn();
        Member member = (Member) mvcResult.getRequest().getAttribute("member");
        log.debug("account \"{}\" name is {}", member.getAccount(), member.getName());
        assertThat("Test UserInfo error ", "admin", is(equalTo(member.getAccount())));
    }

}
