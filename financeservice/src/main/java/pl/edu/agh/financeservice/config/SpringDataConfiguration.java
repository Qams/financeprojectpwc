package pl.edu.agh.financeservice.config;

import com.datastax.driver.core.Session;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.config.java.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@EnableAutoConfiguration
class SpringDataConfiguration {

    @Configuration
    @EnableCassandraRepositories("pl.edu.agh.financeservice.repository")
    static class CassandraConfig extends AbstractCassandraConfiguration {

        @Override
        public String getKeyspaceName() {
            return "voting";
        }

        @Bean
        public CassandraTemplate cassandraTemplate(Session session) {
            return new CassandraTemplate(session);
        }

//        @Override
//        public String[] getEntityBasePackages() {
//            return new String[] { Activity.class.getPackage().getName(), ExchangeRate.class.getPackage()};
//        }

        @Override
        public SchemaAction getSchemaAction() {
            return SchemaAction.CREATE_IF_NOT_EXISTS;
        }
    }
}
