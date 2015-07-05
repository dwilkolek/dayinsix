package eu.wilkolek.diary.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;

import eu.wilkolek.diary.model.DictionaryWord;
import eu.wilkolek.diary.repository.DictionaryWordRepository;

@Controller
public class SugetionController {

    @Autowired
    private DictionaryWordRepository dictionaryWordRepository;
    
    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(value = "/sugestions")
    public ModelAndView getSuggestion(@RequestParam("letters") String letters){
        
        ModelAndView model = new ModelAndView("sugestion/plainText");
       
        ArrayList<DictionaryWord> sugestions = dictionaryWordRepository.findWords(letters, 10);
        ArrayList<String> sugestionsInString = new ArrayList<String>();
        for (DictionaryWord word : sugestions){
            sugestionsInString.add(word.getValue());
        }
        Gson gson = new Gson();
        
        
        
        model.getModelMap().put("sugestions", gson.toJson(sugestionsInString).toString());
        
        return model;
    }
    
    
}
