package eu.wilkolek.diary.model;

import java.util.Collection;
import java.util.HashMap;

import org.springframework.security.core.authority.AuthorityUtils;

import eu.wilkolek.diary.util.DateTimeUtils;

public class CurrentUser extends org.springframework.security.core.userdetails.User {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7985212075642157531L;
	private User user;

    public CurrentUser(User user) {
        super(user.getEmail(), user.getPasswordHash(), AuthorityUtils.createAuthorityList(user.rolesToArray()));
        this.user = user;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public String getEmail(){
    	return user.getEmail();
    }
    
    public String getId() {
        return user.getId();
    }

    public Collection<String> getRoles() {
        return user.getRoles();
    }
    
    public HashMap<String, String> getOptions(){
    	return user.getOptions();
    }

    public void setOptions(HashMap<String, String> options){
    	user.setOptions(options);
    }
    
    public String getUsername(){
        return user.getUsername();
    }
    
    public String getLastLogInString(){
        try {
        return DateTimeUtils.format(user.getLastLogIn());
        } catch (Exception e){
            e.printStackTrace();
            return "unknown";
        }
    }
    
    @Override
    public String toString() {
        return "CurrentUser{" +
                "user=" + user +
                "} ";
    }
}
