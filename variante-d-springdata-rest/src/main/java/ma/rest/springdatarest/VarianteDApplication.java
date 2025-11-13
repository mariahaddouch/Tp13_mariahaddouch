package ma.rest.springdatarest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("ma.rest.entity")
@EnableJpaRepositories("ma.rest.springdatarest.repository")
public class VarianteDApplication {

    public static void main(String[] args) {
        SpringApplication.run(VarianteDApplication.class, args);
    }

}