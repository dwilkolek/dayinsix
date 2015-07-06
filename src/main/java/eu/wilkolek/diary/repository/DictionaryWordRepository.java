package eu.wilkolek.diary.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import eu.wilkolek.diary.model.DictionaryWord;
import eu.wilkolek.diary.model.User;

@Component
public interface DictionaryWordRepository extends MongoRepository<DictionaryWord, String>, DictionaryWordCustom{

	public Optional<DictionaryWord> findByValueAndUser(String value, User user);
	
}
