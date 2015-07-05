package eu.wilkolek.diary.repository;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import eu.wilkolek.diary.model.Day;
import eu.wilkolek.diary.model.User;


public interface DayRepositoryCustom{

	public ArrayList<Day> get7DaysFromDate(User user, Date date);
	
	
}
