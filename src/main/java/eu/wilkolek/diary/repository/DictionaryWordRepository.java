package eu.wilkolek.diary.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import eu.wilkolek.diary.model.DictionaryWord;

@Component
public interface DictionaryWordRepository extends MongoRepository<DictionaryWord, String>{

	public Optional<DictionaryWord> findByValue(String value);
	
}
