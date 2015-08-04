package eu.wilkolek.diary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.wilkolek.diary.model.CurrentUser;
import eu.wilkolek.diary.repository.DayRepository;

@Controller
public class StatsController {

    @Autowired
    private DayRepository dayRepository;
    
    @RequestMapping(value = "/stats", method = RequestMethod.GET)
    public String explore(Model model,  CurrentUser user) {
        
        
        
        
        return "stats";
    }
    
}
