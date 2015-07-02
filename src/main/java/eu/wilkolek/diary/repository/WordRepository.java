package eu.wilkolek.diary.repository;

import java.util.Date;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import eu.wilkolek.diary.model.Day;
import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.model.Word;
@Component
public interface WordRepository extends MongoRepository<Word, String>{

	public Word findByValue(String value);
	
}
