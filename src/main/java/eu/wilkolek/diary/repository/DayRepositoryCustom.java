package eu.wilkolek.diary.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import eu.wilkolek.diary.model.Day;
import eu.wilkolek.diary.model.ShareStyleEnum;
import eu.wilkolek.diary.model.User;


public interface DayRepositoryCustom{

	public ArrayList<Day> getDaysFromDateToDate(User user, Date dateStart, Date dateEnd);
	public Integer countByUser(User user);
	
	public LinkedList<Day> getLatestDays(ShareStyleEnum level, int limit);
	
}
