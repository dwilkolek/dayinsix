package eu.wilkolek.diary.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import eu.wilkolek.diary.dto.UserCreateForm;

@Document(collection = "users")
public class User {


    private String id;


    private String email;

    private String passwordHash;

    private Collection<String> roles;

    private HashMap<String, String> options;
    
    
    public User(){
    	
    }
    
    public User(UserCreateForm form){
    	
    	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    	
    	this.email = form.getEmail();
    	this.passwordHash = encoder.encode(form.getPassword());
    	this.roles = new ArrayList<String>();
    	this.roles.add(RoleEnum.USER.name());
    	
    	this.options = new HashMap<String, String>();
    	this.options.put("inputType", form.getInputType());
    }
    
	public String[] rolesToArray() {
		String[] roles = new String[this.roles.size()];
		int i = 0;
		for (String r : this.roles){
			roles[i]=r;
			i++;
		}
		return roles;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public Collection<String> getRoles() {
		return roles;
	}

	public void setRoles(Collection<String> roles) {
		this.roles = roles;
	}

	public HashMap<String, String> getOptions() {
		return options;
	}

	public void setOptions(HashMap<String, String> options) {
		this.options = options;
	}

//	public Collection<Day> getDays() {
//		return days;
//	}
//
//	public void setDays(Collection<Day> days) {
//		this.days = days;
//	}


    
}