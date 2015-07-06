package eu.wilkolek.diary.repository;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import eu.wilkolek.diary.model.DictionaryWord;
import eu.wilkolek.diary.model.User;

@Component
public interface DictionaryWordCustom{

	public ArrayList<DictionaryWord> findWords(String letters, Integer limit, User user);
	
}
