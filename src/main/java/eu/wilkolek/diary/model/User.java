package eu.wilkolek.diary.model;

import java.util.Collection;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {


    private String id;


    private String email;

    private String passwordHash;

    private Collection<String> roles;

    
    private Collection<Day> days;
    
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

	public Collection<Day> getDays() {
		return days;
	}

	public void setDays(Collection<Day> days) {
		this.days = days;
	}


    
}