package eu.wilkolek.diary.model;

import java.util.Collection;

import org.springframework.security.core.authority.AuthorityUtils;

public class CurrentUser extends org.springframework.security.core.userdetails.User {

    private User user;

    public CurrentUser(User user) {
        super(user.getEmail(), user.getPasswordHash(), AuthorityUtils.createAuthorityList(user.rolesToArray()));
        this.user = user;
    }

    public User getUser() {
        return user;
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

    @Override
    public String toString() {
        return "CurrentUser{" +
                "user=" + user +
                "} ";
    }
}
