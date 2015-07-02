package eu.wilkolek.diary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Component;

import com.mongodb.DB;
import com.mongodb.MongoClient;

import eu.wilkolek.diary.repository.DayRepository;

@Configuration
@EnableMongoRepositories(basePackages = "eu.wilkolek.diary.repository")
@Component
public class MongoDBConfig {
	
	@Autowired
	DayRepository dayRepository;
	
	@Bean
	public MongoDbFactory mongoDbFactory() throws Exception {
		MongoClient mongoClient = new MongoClient("localhost", 27017);
		UserCredentials userCredentials = new UserCredentials("", "");
		SimpleMongoDbFactory factory = new SimpleMongoDbFactory(mongoClient, "diary",
				userCredentials);
		DB db = factory.getDb();
		for (String name : db.getCollectionNames()){
			System.out.println("name : " + name);
		}
		
		return factory;
	}

	@Bean
	public MongoTemplate mongoTemplate() throws Exception {
		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
		return mongoTemplate;
	}
}
