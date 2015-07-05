package eu.wilkolek.diary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

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

import eu.wilkolek.diary.model.DictionaryWord;
import eu.wilkolek.diary.model.Word;
import eu.wilkolek.diary.repository.DayRepository;
import eu.wilkolek.diary.repository.DictionaryWordRepository;

@Configuration
@EnableMongoRepositories(basePackages = "eu.wilkolek.diary.repository")
@Component
public class MongoDBConfig {
		
	@Bean
	public MongoDbFactory mongoDbFactory() throws Exception {
		MongoClient mongoClient = new MongoClient("localhost", 27017);
		UserCredentials userCredentials = new UserCredentials("", "");
		SimpleMongoDbFactory factory = new SimpleMongoDbFactory(mongoClient, "diary",
				userCredentials);
		
		return factory;
	}

	@Bean
	public MongoTemplate mongoTemplate() throws Exception {
		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
		return mongoTemplate;
	}
	
}
