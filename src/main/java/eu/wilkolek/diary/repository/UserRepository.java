package eu.wilkolek.diary.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import eu.wilkolek.diary.model.User;
@Component
public interface UserRepository extends MongoRepository<User, String>, UserRepositoryCustom{

	Optional<User> findByEmail(String email);
	
	Optional<User> findById(String id);
	
	Optional<User> findByUsername(String username);

}
