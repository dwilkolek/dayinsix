package eu.wilkolek.diary.repository;

import java.util.ArrayList;
import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;

import eu.wilkolek.diary.model.Day;
import eu.wilkolek.diary.model.Sitemap;

public class SitemapRepositoryImpl implements SitemapRepositoryCustom{

    private MongoOperations operation;
    @Autowired
    public SitemapRepositoryImpl(MongoOperations operation){
        this.operation = operation;
    }
    
    @Override
    public Sitemap getLatest() {
        Query query = new Query();
        query.with(new Sort(Sort.Direction.DESC, "storeDate"));
        query.limit(1);
     
        
        return operation.findOne(query, Sitemap.class);
    }

}
