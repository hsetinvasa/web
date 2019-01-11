package y.context;

import y.model.Dao;
import javax.sql.DataSource;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.spring.DBIFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Scope;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.session.hazelcast.config.annotation.web.http.EnableHazelcastHttpSession;

/**
 *
 * @author Kent Yeh
 */
@Configuration
@ImportResource("classpath:applicationContext.xml")
@EnableHazelcastHttpSession
public class ApplicationContext {

    @Autowired
    private DataSource datasource;

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public JdbiLog jdbiLog() {
        return new JdbiLog();
    }

    @Bean
    public DBIFactoryBean dBIFactoryBean() {
        DBIFactoryBean res = new DBIFactoryBean();
        res.setDataSource(datasource);
        return res;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public DBI dbi() throws Exception {
        DBI dbi = (DBI) dBIFactoryBean().getObject();
        dbi.setSQLLog(jdbiLog());
        return dbi;
    }

    @Bean(destroyMethod = "close")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Dao dao() throws Exception {
        return dbi().open(Dao.class);
    }

}
