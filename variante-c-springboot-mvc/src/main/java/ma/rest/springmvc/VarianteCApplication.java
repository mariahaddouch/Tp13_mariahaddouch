package ma.rest.springmvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("ma.rest.entity")
@EnableJpaRepositories("ma.rest.springmvc.repository")
public class VarianteCApplication {

    public static void main(String[] args) {
        SpringApplication.run(VarianteCApplication.class, args);
    }

}