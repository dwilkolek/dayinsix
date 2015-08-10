package eu.wilkolek.diary.service;

import java.util.List;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import eu.wilkolek.diary.model.Mail;
import eu.wilkolek.diary.model.Sitemap;
import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.repository.DayRepository;
import eu.wilkolek.diary.repository.ErrorRepository;
import eu.wilkolek.diary.repository.MailRepository;
import eu.wilkolek.diary.repository.SitemapRepository;
import eu.wilkolek.diary.repository.UserRepository;
import eu.wilkolek.diary.util.DateTimeUtils;
import eu.wilkolek.diary.util.MailUtil;

@Service
public class SitemapService {

    
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DayRepository dayRepository;
    
    @Autowired
    private SitemapRepository sitemapRepository;
    
    
    
    @Scheduled(cron = "0 0 */6 * * ?")
    public void generateSitemap(){
        System.out.println("Generating sitemap");
        String usersString = "";
        String usersSubpagesString = "";
        List<User> users = userRepository.findAll();
        
        for (User u : users){
            Integer allDays = dayRepository.countByUser(u);
            
            Integer pages = (int)Math.ceil(allDays/10);
            
            if (pages*10 < allDays){
                pages++;
            }
            
            usersString += generateTemplate(u.getUsername(), null, "0.70");
            for (int i=1;i<=pages;i++){
                usersSubpagesString += generateTemplate(u.getUsername(), i, "0.5");
            }
            
        }
        
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\">";
        String websites = "<url><loc>http://dayinsix.com/</loc><priority>1.00</priority></url><url>  <loc>http://dayinsix.com/remind</loc>  <priority>0.80</priority></url><url>  <loc>http://dayinsix.com/register</loc>  <priority>0.80</priority></url><url>  <loc>http://dayinsix.com/stats</loc>  <priority>0.80</priority></url><url>  <loc>http://dayinsix.com/explore</loc>  <priority>0.80</priority></url><url>  <loc>http://dayinsix.com/faq</loc>  <priority>0.80</priority></url><url>  <loc>http://dayinsix.com/feedback</loc>  <priority>0.80</priority></url>";
        String footer = "</urlset>";
        
        
        String compact = header + websites + usersString + usersSubpagesString + footer;
        
        Sitemap sitemap = new Sitemap();
        sitemap.setDate(DateTimeUtils.getUTCDate());
        sitemap.setSitemap(compact);
        
        sitemap = sitemapRepository.save(sitemap);
        System.out.println("Generating sitemap ("+sitemap.getId()+") - finished " + sitemap.getDate());
        
    }
    
    private String generateTemplate(String username, Integer page, String rank){
        
        return "<url><loc>http://dayinsix.com/s/"+username+(page != null ? "/"+page : "")+"</loc><priority>"+rank+"</priority></url>";
        
    }
    
}
