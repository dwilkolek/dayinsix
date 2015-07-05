package eu.wilkolek.diary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import eu.wilkolek.diary.model.DictionaryWord;
import eu.wilkolek.diary.repository.DictionaryWordRepository;

@Component
public class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {
    
    private DictionaryWordRepository dictionaryWordRepository;
    
    @Autowired
    public ApplicationStartup(DictionaryWordRepository dictionaryWordRepository) {
        this.dictionaryWordRepository = dictionaryWordRepository;
    }
    
    private void setupData() {
        String fileLocalization = System.getProperty("dictionary.words");
        File file = new File(fileLocalization);
        LinkedList<DictionaryWord> words = new LinkedList<DictionaryWord>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                DictionaryWord word = new DictionaryWord();
                word.setValue(line);
                words.add(word);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        dictionaryWordRepository.save(words);
        System.out.println("dictionary word list: " + fileLocalization
                + "; added " + words.size() + " words in EN");
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!StringUtils.isEmpty(System.getProperty("dictionary.words"))){
            this.setupData();
        }
        
    }
}
