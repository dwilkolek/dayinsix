package eu.wilkolek.diary.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import eu.wilkolek.diary.model.Day;
import eu.wilkolek.diary.model.ShareStyleEnum;
import eu.wilkolek.diary.model.Sitemap;
import eu.wilkolek.diary.model.User;


public interface SitemapRepositoryCustom{

    public Sitemap getLatest();
    
}
