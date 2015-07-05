package eu.wilkolek.diary.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import eu.wilkolek.diary.model.Day;
import eu.wilkolek.diary.model.User;

public class UserRepositoryImpl implements UserRepositoryCustom{

	private MongoOperations operation;
	
	@Autowired
	public UserRepositoryImpl(MongoOperations operation){
		this.operation = operation;
	}
	
	@Override
	public User addDayRef(User user, Day day){
		
		BasicDBObject newDay = new BasicDBObject();
		newDay.append("$set", new BasicDBObject("days",day));
		
		BasicDBObject searchQuery = new BasicDBObject().append("_id", user.getId());
		
		
		Query query = new Query(Criteria.where("_id").is(user.getId()));
		Update update = new Update();
		update.push("days", day);
		
		operation.updateFirst(query, update, User.class);
		
		
		
		return user;
		
	}
	
}
