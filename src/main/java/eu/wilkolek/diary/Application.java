package eu.wilkolek.diary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan
@EnableAutoConfiguration
@SpringBootApplication
@EnableMongoRepositories
@EnableScheduling
public class Application {

    public static void main(String[] args) throws ClassNotFoundException {
        SpringApplication.run(Application.class, args);
   }
    
    

}
