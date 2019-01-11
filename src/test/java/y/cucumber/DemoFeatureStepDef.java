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
public class DemoFeatureStepDef {


    private MockMvc mockMvc;

    @Autowired
    private TestMemberManager memberManager;
    @Autowired
    WebApplicationContext wac;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).alwaysExpect(status().isOk()).build();
    }

    @Given("^administrator has been authorized\\.$")
    public void administratorHasBeenAuthorized() throws Throwable {
        log.debug("Administrator has been authorized.");
    }

    @When("^administrator click to view all users' infomation\\.$")
    public void administratorClickToViewAllUsersInfomation() throws Throwable {
        log.debug("Administrator click to view all users' infomation");
    }

    @Then("^return headcount should equals all user's amount\\.$")
    public void testUsersInfo() throws Exception {
        mockMvc.perform(post("/admin/users").with(user("admin").roles("ADMIN"))).andDo(print()).andExpect(jsonPath("$.total", is(equalTo(memberManager.countUsers()))));
    }

    @Given("^acouunt user \"([^\"]*)\" has already been authorized\\.$")
    public void acouuntUserHasAlreadyBeenAuthorized(String user) throws Throwable {
        log.debug("Acouunt user \"{}\" has already been authorized.", user);
    }

    @When("^\"([^\"]*)\" click MyInfo anchor\\.$")
    public void clickMyInfoAnchor(String user) throws Throwable {
        log.debug("\"{}\" click MyInfo anchor.", user);
    }

    @Then("^display personal info to \"([^\"]*)\"$")
    public void displayPersonalInfoTo(String user) throws Throwable {
        MvcResult mvcResult = mockMvc.perform(post("/user/myinfo").principal(new TestingAuthenticationToken(user, null))).andDo(print()).andReturn();
        Member member = (Member) mvcResult.getRequest().getAttribute("member");
        assertThat("Display MyInfo failed!", member.getAccount(), is(equalTo(user)));
    }

}
