package eu.wilkolek.diary.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;

import eu.wilkolek.diary.model.Day;
import eu.wilkolek.diary.model.ShareStyleEnum;
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

    @Override
    public Integer countByUser(User user) {
        Query query = new Query(Criteria.where("user").is(user));
        Integer result = (int)operation.count(query, Day.class);
        return result;
    }

    @Override
    public LinkedList<Day> getLatestDays(ShareStyleEnum level, int limit) {
        Criteria c = Criteria.where("shareStyle").is(ShareStyleEnum.PUBLIC.name());
        if (level.equals(ShareStyleEnum.PUBLIC) || level.equals(ShareStyleEnum.PROTECTED)){
            c = Criteria.where("shareStyle").is(ShareStyleEnum.PUBLIC.name());
            c.andOperator(Criteria.where("userProfileVisibility").is(ShareStyleEnum.PUBLIC.name()));
        }
        if (level.equals(ShareStyleEnum.PROTECTED)){
            ArrayList<String> shareStyle = new ArrayList<String>();
            shareStyle.add(ShareStyleEnum.PROTECTED.name());
            shareStyle.add(ShareStyleEnum.PUBLIC.name());
            c = Criteria.where("shareStyle").in(shareStyle);
            c.orOperator(Criteria.where("userProfileVisibility").in(shareStyle));
        }
        
        
        Query query = new Query(c);
        query.with(new Sort(Sort.Direction.DESC, "storeDate"));
        query.limit(limit);
        LinkedList<Day> lList = new LinkedList<Day>();
        ArrayList<Day> reOp = (ArrayList<Day>)operation.find(query, Day.class);
        for (int i=0; i<reOp.size(); i++){
            lList.add(reOp.get(i));
        }
        return lList;
    }

	

}
