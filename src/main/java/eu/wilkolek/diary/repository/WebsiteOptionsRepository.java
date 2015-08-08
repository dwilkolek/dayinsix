package eu.wilkolek.diary.repository;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import eu.wilkolek.diary.model.Day;
import eu.wilkolek.diary.model.Mail;
import eu.wilkolek.diary.model.Meta;
import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.model.WebsiteOptions;

@Component
public interface WebsiteOptionsRepository extends MongoRepository<WebsiteOptions, String>{

    public WebsiteOptions findByOpt(String opt);
}
