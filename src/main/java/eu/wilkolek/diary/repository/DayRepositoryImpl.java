package eu.wilkolek.diary.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
	public ArrayList<Day> getDaysFromDateToDate(User user, Date dateStart, Date dateEnd) {
		Query query = new Query(Criteria.where("user").is(user).andOperator(Criteria.where("creationDate").gte(dateStart).andOperator(Criteria.where("creationDate").lte(dateEnd))));
		query.with(new Sort(Sort.Direction.DESC, "creationDate"));
		
		ArrayList<Day> result = new ArrayList<Day>(operation.find(query, Day.class));
		return result;

	}

	

}
