package eu.wilkolek.diary.repository;

import java.util.ArrayList;
import java.util.Date;

import eu.wilkolek.diary.model.Day;
import eu.wilkolek.diary.model.User;


public interface DayRepositoryCustom{

	public ArrayList<Day> get7DaysFromDate(User user, Date date);
	
	
}
