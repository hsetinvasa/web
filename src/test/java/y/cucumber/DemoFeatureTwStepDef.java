package y.cucumber;

import y.manager.TestMemberManager;
import y.model.Member;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.extern.log4j.Log4j2;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import org.springframework.web.context.WebApplicationContext;

/**
 *
 * @author Kent Yeh
 */
@WebAppConfiguration
@ContextConfiguration(classes = y.context.TestContext.class)
@Log4j2
public class DemoFeatureTwStepDef {

    @Autowired
    protected WebApplicationContext wac;
    @Autowired
    private TestMemberManager memberManager;
    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).alwaysExpect(status().isOk()).build();
    }

    @Given("^系統人員已完成登錄$")
    public void adminAuthoritied() throws Exception {
        log.debug("系統人員已完成登錄");
    }

    @When("^系統人員點選查詢所有人員資訊$")
    public void adminWhen() {
        log.debug("系統人員點選查詢所有人員資訊");
    }

    @Then("^顯示人員數應與所有人員數相等$")
    public void testUsersInfo() throws Exception {
        mockMvc.perform(post("/admin/users").with(user("admin").roles("ADMIN"))).andDo(print()).andExpect(jsonPath("$.total", is(equalTo(memberManager.countUsers()))));
    }

    @Given("^用戶\"([^\"]*)\"已完成登錄$")
    public void alreadyLogin(String user) throws Throwable {
        log.debug("{}已完成登錄", user);
    }

    @When("^\"([^\"]*)\"點選我的個人資料$")
    public void clickMyInfo(String user) throws Throwable {
        log.debug("{}點選我的個人資料$", user);
    }

    @Then("^顯示\"([^\"]*)\"的個資$")
    public void viewMyIno(String user) throws Throwable {
        MvcResult mvcResult = mockMvc.perform(post("/user/myinfo").principal(new TestingAuthenticationToken(user, null))).andDo(print()).andReturn();
        Member member = (Member) mvcResult.getRequest().getAttribute("member");
        assertThat("顯示個資失敗", member.getAccount(), is(equalTo(user)));
    }

}
