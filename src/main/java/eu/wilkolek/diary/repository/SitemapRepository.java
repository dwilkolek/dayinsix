package eu.wilkolek.diary.repository;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import eu.wilkolek.diary.model.Day;
import eu.wilkolek.diary.model.Mail;
import eu.wilkolek.diary.model.Sitemap;
import eu.wilkolek.diary.model.User;

@Component
public interface SitemapRepository extends MongoRepository<Sitemap, String>,SitemapRepositoryCustom{

	
    
}
