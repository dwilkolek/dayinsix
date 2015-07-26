package eu.wilkolek.diary.dto;

import java.util.HashMap;
import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.validation.ObjectError;

public class UserCreateForm {

    @NotEmpty
    private String username = "";
    
    @NotEmpty
    private String email = "";

    @NotEmpty
    private String password = "";

    @NotEmpty
    private String passwordRepeated = "";
    
    @NotEmpty
    private String inputType = "";
    
    @NotEmpty
    private String profileVisibility = "";
    
//    @NotEmpty
//    private String timezone = "";
    
//    @NotEmpty
//    private String shareStyle = "";

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

	public String getInputType() {
		return this.inputType;
	}

	public void setInputType(String inputType) {
		this.inputType = inputType;
	}

//    public String getTimezone() {
//        return timezone;
//    }
//
//    public void setTimezone(String timezone) {
//        this.timezone = timezone;
//    }

//    public String getShareStyle() {
//        return shareStyle;
//    }
//
//    public void setShareStyle(String shareStyle) {
//        this.shareStyle = shareStyle;
//    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileVisibility() {
        return profileVisibility;
    }

    public void setProfileVisibility(String profileVisibility) {
        this.profileVisibility = profileVisibility;
    }
    
    
	
	
}