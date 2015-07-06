package eu.wilkolek.diary.repository;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import eu.wilkolek.diary.model.Day;
import eu.wilkolek.diary.model.DictionaryWord;
import eu.wilkolek.diary.model.User;

public class DictionaryWordRepositoryImpl implements DictionaryWordCustom{

	private MongoOperations operation;
	
	@Autowired
	public DictionaryWordRepositoryImpl(MongoOperations operation){
		this.operation = operation;
	}
	
	
    @Override
    public ArrayList<DictionaryWord> findWords(String letters, Integer limit, User user){
        
        Query query = new Query(Criteria.where("user").is(user).and("value").regex("^"+letters+"(.*)$"));
        
        return new ArrayList<DictionaryWord>(operation.find(query.limit(limit), DictionaryWord.class));

    }
	
}
