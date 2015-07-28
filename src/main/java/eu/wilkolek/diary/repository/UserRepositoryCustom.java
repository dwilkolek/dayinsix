package eu.wilkolek.diary.repository;

import java.util.List;

import eu.wilkolek.diary.model.Day;
import eu.wilkolek.diary.model.User;

public interface UserRepositoryCustom {

	User addDayRef(User user, Day day);
	
	List<User> findAllByPartUserName(String search, int limit);
	
    boolean canShareWith(User user, User user2);
}
