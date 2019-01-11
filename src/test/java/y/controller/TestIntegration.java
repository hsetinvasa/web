package y.controller;

import y.context.WebDriverFactory;
import java.io.IOException;
import lombok.extern.log4j.Log4j2;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.core.StringEndsWith.endsWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 *
 * @author Kent Yeh
 */
@Test(groups = {"integrate"})
@Log4j2
public class TestIntegration {

    private int httpPort = 80;
    private String contextPath = "";
    private WebDriver driver;

    @BeforeClass
    @Parameters({"http.port", "contextPath"})
    public void setup(@Optional("http.port") int httpPort, @Optional("contextPath") String contextPath) {
        this.httpPort = httpPort;
        log.debug("http port is {}", httpPort);
        this.contextPath = contextPath;
        driver = WebDriverFactory.getInstance(WebDriverFactory.Brwoser.HTMLUNIT);
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void test404() throws IOException {
        String url = String.format("http://localhost:%d/%s/unknownpath/404.html", httpPort, contextPath);
        driver.get(url);
        assertThat("404 failed ", driver.getCurrentUrl(), is(endsWith("404.html")));
    }

    @Test
    public void testMyInfo() {
        String url = String.format("http://localhost:%d/%s/user/myinfo", httpPort, contextPath);
        log.debug("Test myinfo with {}", url);
        driver.get(url);
        WebElement form =  driver.findElement(By.tagName("form"));
        form.findElement(By.name("j_username")).sendKeys("admin");
        form.findElement(By.name("j_password")).sendKeys("admin");
        form.submit();
        WebElement h1 = driver.findElement(By.xpath("//h1"));
        assertThat("Fail to get My Info", h1.getText(), is(containsString("admin")));
    }
    
    @Test(dependsOnMethods = "testMyInfo")
    public void logout() throws IOException {
        String url = String.format("http://localhost:%d/%s/", httpPort, contextPath);
        log.debug("Integration Test: logout with {}", url);
        driver.get(url);
        WebElement form =  driver.findElement(By.tagName("form"));
        form.submit();
        assertThat("logout failed ", driver.getCurrentUrl(), is(containsString("/index")));
    }
}
