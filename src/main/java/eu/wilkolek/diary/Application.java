package eu.wilkolek.diary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


//@Configuration
//@EnableMongoRepositories(value= {"eu.wilkolek.diary.repository"})
//@EnableAutoConfiguration  // Sprint Boot Auto Configuration
//@ComponentScan(basePackages = "eu.wilkolek")
@ComponentScan
@EnableAutoConfiguration
@SpringBootApplication
public class Application {

    public static void main(String[] args) throws ClassNotFoundException {
        SpringApplication.run(Application.class, args);
   }
    
}
