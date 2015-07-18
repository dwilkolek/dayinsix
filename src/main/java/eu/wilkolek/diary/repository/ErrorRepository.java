package eu.wilkolek.diary.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import eu.wilkolek.diary.model.User;

public interface ErrorRepository extends MongoRepository<eu.wilkolek.diary.model.Error, String>{

}
