package eu.wilkolek.diary.repository;

import eu.wilkolek.diary.model.Day;
import eu.wilkolek.diary.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public interface DayRepository extends MongoRepository<Day, String>,DayRepositoryCustom{

	public Optional<Day> findByCreationDate(Date date);

    public Optional<Day> findByCreationDateAndUser(Date date, User user);
    
    public List<Day> findAllByUser(User user);
	
}
