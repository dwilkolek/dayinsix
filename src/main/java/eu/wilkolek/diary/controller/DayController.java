package eu.wilkolek.diary.controller;

import java.util.ArrayList;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.wilkolek.diary.dto.DayForm;
import eu.wilkolek.diary.model.Day;
import eu.wilkolek.diary.model.DictionaryWord;
import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.repository.DayRepository;
import eu.wilkolek.diary.repository.DictionaryWordRepository;
import eu.wilkolek.diary.repository.UserRepository;
import eu.wilkolek.diary.repository.WordRepository;

@Controller
public class DayController {

	
	private DayRepository dayRepository;
	private WordRepository wordRepository;
	private DictionaryWordRepository dictionaryWordRepository;
	private UserRepository userRepository;

	@Autowired
	DayController(DayRepository dayRepository, WordRepository wordRepository, DictionaryWordRepository dictionaryWordRepository, UserRepository userRepository){
		this.dayRepository = dayRepository;
		this.wordRepository = wordRepository;
		this.dictionaryWordRepository = dictionaryWordRepository;
		this.userRepository = userRepository;
	}
	
	
	@RequestMapping(value = "/user/day/add", method = RequestMethod.GET)
	public String add(Model model) {
		model.addAttribute("form", new DayForm());
		return "day/add";
	}
	
	@RequestMapping(value = "/user/day/add", method = RequestMethod.POST)
    public String saveNote(@Valid DayForm dayForm, BindingResult result) {
        if (result.hasErrors()) {
            return "day/add";
        }
        
        //TODO :view :currentUser :auth
       User user = userRepository.findAll().get(0);
       if (user == null) {
    	   user = new User();
       }
        
        if (dayForm.getWords().size() > 0){
        	ArrayList<DictionaryWord> resultList = new ArrayList<DictionaryWord>();
        	for (int i=0; i<dayForm.getWords().size(); i++){
        		String value = dayForm.getWords().get(i);
        		
        		Optional<DictionaryWord> dictWordO = dictionaryWordRepository.findByValue(value);
        		if (!dictWordO.isPresent()){
        			resultList.add(dictWordO.get());
        		} else {
        			DictionaryWord dw = new DictionaryWord();
        			dw.setValue(value);
        			DictionaryWord dwSaved = dictionaryWordRepository.save(dw);
        			resultList.add(dwSaved);
        		}
        	}
        	dayForm.setDictionaryWords(resultList);
        }
        
        		
        		
        
        dayRepository.save(new Day(dayForm, user));
        return "redirect:/user/day/list";
    }
 
	@RequestMapping(value = "/user/day/list")
	public String listDays(Model model){
		
		model.addAttribute("days", dayRepository.findAll());
		
		return "day/list";
	}

}
