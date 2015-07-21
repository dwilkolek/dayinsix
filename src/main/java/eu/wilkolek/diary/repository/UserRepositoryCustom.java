package eu.wilkolek.diary.repository;

import eu.wilkolek.diary.model.Day;
import eu.wilkolek.diary.model.User;

public interface UserRepositoryCustom {

	User addDayRef(User user, Day day);
	
    boolean canShareWith(User user, User user2);
}
