package eu.wilkolek.diary.repository;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import eu.wilkolek.diary.model.Day;
import eu.wilkolek.diary.model.User;

public class DayRepositoryImpl implements DayRepositoryCustom {

	
private MongoOperations operation;
	
	@Autowired
	public DayRepositoryImpl(MongoOperations operation){
		this.operation = operation;
	}
	
	@Override
	public ArrayList<Day> get7DaysFromDate(User user, Date date) {
		
		Date firstDay = new Date(date.getTime() - 7 * 24*60*60*1000); 
		
		Query query = new Query(Criteria.where("user").is(user).andOperator(Criteria.where("creationDate").gt(firstDay)));
		ArrayList<Day> result = new ArrayList<Day>(operation.find(query, Day.class));
		return result;

	}

	

}
