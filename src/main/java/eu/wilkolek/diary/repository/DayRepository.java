package eu.wilkolek.diary.repository;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import eu.wilkolek.diary.model.Day;

@Component
public interface DayRepository extends MongoRepository<Day, String>,DayRepositoryCustom{

	public Optional<Day> findByCreationDate(Date date);
	
	
}
