package eu.wilkolek.diary.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

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
		
		Query query = new Query(Criteria.where("_id").is(user.getId()));
		Update update = new Update();
		update.push("days", day);
		
		operation.updateFirst(query, update, User.class);
		
		return user;
	}

    @Override
    public boolean canShareWith(User user, User user2) {
        Criteria shareCrit = Criteria.where("_id").is(user2.getId());  
        Criteria c = Criteria.where("_id").is(user.getId());
//        c.elemMatch((Criteria.where("shareWith").elemMatch(shareCrit)));
        Query query = new Query(c);
        List<User> list = operation.find(query, User.class);
        boolean canShare = false;
        for (String u : list.get(0).getSharingWith()){
            if (u.equals(user2.getId())){
                canShare = true;
            }
        }
        return canShare;
    }
	
}
