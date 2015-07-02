package eu.wilkolek.diary.repository;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import eu.wilkolek.diary.model.Day;
import eu.wilkolek.diary.model.DictionaryWord;
import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.model.Word;
@Component
public interface DictionaryWordRepository extends MongoRepository<DictionaryWord, String>{

	public Optional<DictionaryWord> findByValue(String value);
	
}
