package eu.wilkolek.diary.controller;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.Gson;

import eu.wilkolek.diary.model.CurrentUser;
import eu.wilkolek.diary.model.Meta;
import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.repository.UserRepository;
import eu.wilkolek.diary.service.MetaService;

@Controller
public class ExploreController {

    @Autowired
    private UserRepository userRepository;

    
    @Autowired
    private MetaService metaService;
    
    
    @RequestMapping(value = "/explore", method = RequestMethod.GET)
    public String explore(Model model) {
        model = metaService.updateModel(model, "/explore", new Meta(), null,"");
        return "explore";
    }

    @RequestMapping(value = "/explore/{search}", method = RequestMethod.GET)
    public String explore(Model model, @PathVariable(value = "search") String search, CurrentUser user) {

        Gson gson = new Gson();
        List<User> dbQ = userRepository.findAllByPartUserName(search, 25);
        LinkedList<String> result = new LinkedList<String>();
        for (User u : dbQ) {
            if (user != null && user.getUser() != null) {
                int s = 1;
                int f = 1;
                if (user.getUser().getSharingWith() != null) {
//                    s = 1;
                    for (String id : user.getUser().getSharingWith()) {
                        if (id.equals(u.getId())) {
                            s = 2;
                        }
                    }
                }
                if (user.getUser().getFollowingBy() != null) {
//                    f = 1;
                    for (String id : user.getUser().getFollowingBy()) {
                        if (id.equals(u.getId())) {
                            f = 2;
                        }
                    }
                }
                // if (user.getUser().getFollowingBy() != null){
                // f = user.getUser().getFollowingBy().contains(u.getId()) ? 2 :
                // 1;
                // }
                boolean isTheSameUser = false;
                if (user != null){
                    isTheSameUser = user.getId().equals(u.getId());
                }
                if (!isTheSameUser){
                    result.add(u.getUsername() + ";" + s + ";" + f);
                }
            } else {
                result.add(u.getUsername() + ";0;0");
            }
        }
        model.asMap().put("results", gson.toJson(result));
        
        

        return "plain";
    }

}
