package eu.wilkolek.diary.dto;

import java.util.HashMap;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.validation.ObjectError;

import eu.wilkolek.diary.model.NotificationTypesEnum;
import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.model.UserOptions;

public class ProfileForm{

    
    public ProfileForm() {
        super();
    }
    
    @NotNull
    private String notificationFrequency;
    
    @NotNull
    private String profileVisibility;
    
    public ProfileForm(User user) {
        this.setEmail(user.getEmail());
        this.setUsername(user.getUsername());
        this.setPassword("");
        this.setPasswordRepeated("");
        this.setAbout(user.getAbout());
        
        this.setInputType(user.getOptions().get(UserOptions.INPUT_TYPE));
        this.setNotificationFrequency(user.getOptions().get(UserOptions.NOTIFICATION_FREQUENCY));
        this.setShareStyle(user.getOptions().get(UserOptions.SHARE_STYLE));
//        this.setTimezone(user.getOptions().get(UserOptions.TIMEZONE));
        this.setProfileVisibility(user.getOptions().get(UserOptions.PROFILE_VISIBILITY));
        
    }

    public String getNotificationFrequency() {
        return notificationFrequency;
    }
    public String getNotificationFrequencyAsString() {
        return notificationFrequency.toString();
    }
    public void setNotificationFrequency(String notificationFrequency) {
        this.notificationFrequency = notificationFrequency;
    }
    
    @NotEmpty
    private String username = "";
    
    @NotEmpty
    private String email = "";

    private String password = "";

    private String passwordRepeated = "";
    
    private String about;
    
    @NotEmpty
    private String inputType = "";
    
//    @NotEmpty
//    private String timezone = "";
    
    @NotEmpty
    private String shareStyle = "";

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

    public String getShareStyle() {
        return shareStyle;
    }

    public void setShareStyle(String shareStyle) {
        this.shareStyle = shareStyle;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    
    public String getNotificationAsEnumString(String days){
        return NotificationTypesEnum.getInEnum(days).name();
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getProfileVisibility() {
        return profileVisibility;
    }

    public void setProfileVisibility(String profileVisibility) {
        this.profileVisibility = profileVisibility;
    }
    
    
    
}
