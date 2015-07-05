package eu.wilkolek.diary.dto;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.validator.EmailValidator;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

public class UserCreateForm {

    @NotEmpty
    private String email = "";

    @NotEmpty
    private String password = "";

    @NotEmpty
    private String passwordRepeated = "";

//    @NotNull
//    private Role role = Role.USER;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordRepeated() {
		return passwordRepeated;
	}

	public void setPasswordRepeated(String passwordRepeated) {
		this.passwordRepeated = passwordRepeated;
	}

	public HashMap<String,String> createMessages(List<ObjectError> allErrors) {
		HashMap<String,String> errors = new HashMap<String,String>();
		for(ObjectError e : allErrors){
			errors.put(e.getCode(), e.getDefaultMessage());
		}
		return errors;
	}

//	public Role getRole() {
//		return role;
//	}
//
//	public void setRole(Role role) {
//		this.role = role;
//	}
    
    

}