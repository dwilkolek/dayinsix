package eu.wilkolek.diary.repository;

import java.util.Date;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import eu.wilkolek.diary.model.Day;
import eu.wilkolek.diary.model.User;

@Component
public interface DayRepository extends MongoRepository<Day, String>,DayRepositoryCustom{

	public Day findByCreationDate(String date);
	
	
}
