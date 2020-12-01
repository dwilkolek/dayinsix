package eu.wilkolek.diary.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import eu.wilkolek.diary.model.Meta;

@Component
public interface MetaRepository extends MongoRepository<Meta, String>{

    public Meta findByUrl(String url);
    public Optional<Meta> findById(String id);
}
