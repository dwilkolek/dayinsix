package eu.wilkolek.diary.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import eu.wilkolek.diary.dto.ProfileForm;
import eu.wilkolek.diary.dto.UserCreateForm;
import eu.wilkolek.diary.util.DateTimeUtils;

@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String username;
    
    private String email;

    private String passwordHash;

    private Collection<String> roles;

    private HashMap<String, String> options;
    
    private HashMap<String, Date> optionsLastUpdate;
    
    private Date lastLogIn;
    
    private Date lastNotification;
    
    private Date created;
    
    private ArrayList<String> followingBy;
    
    private ArrayList<String> followedBy;
    
    private ArrayList<String> sharingWith;
    
    private String token;
    
    private String about;
    
    private boolean enabled;

    private ArrayList<String> blocked;
    
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public User(){
    	
    }
    
    public User(UserCreateForm form){
    	
    	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    	
    	this.setUsername(form.getUsername());
    	this.email = form.getEmail();
    	this.passwordHash = encoder.encode(form.getPassword());
    	this.roles = new ArrayList<String>();
    	this.roles.add(RoleEnum.USER.name());
    	
    	this.options = new HashMap<String, String>();
    	this.options.put(UserOptions.INPUT_TYPE, form.getInputType());
//    	this.options.put(UserOptions.SHARE_STYLE, form.getShareStyle());
//    	this.options.put(UserOptions.TIMEZONE, form.getTimezone());
    	this.options.put(UserOptions.NOTIFICATION_FREQUENCY, NotificationTypesEnum.DAY.name()); //in days
    	
    	this.options.put(UserOptions.PROFILE_VISIBILITY, form.getProfileVisibility());
    	
    	this.optionsLastUpdate = new HashMap<String, Date>();
        this.optionsLastUpdate.put(UserOptions.INPUT_TYPE, new Date(1));
//        this.optionsLastUpdate.put(UserOptions.SHARE_STYLE, DateTimeUtils.getCurrentUTCTime());
        this.optionsLastUpdate.put(UserOptions.PROFILE_VISIBILITY, DateTimeUtils.getCurrentUTCTime());
//        this.optionsLastUpdate.put(UserOptions.TIMEZONE, DateTimeUtils.getCurrentUTCTime());
        this.optionsLastUpdate.put(UserOptions.NOTIFICATION_FREQUENCY,DateTimeUtils.getCurrentUTCTime());
        this.enabled = false;
        this.token = "";
        this.lastLogIn = DateTimeUtils.getCurrentUTCTime();
        this.followedBy = new ArrayList<String>();
        this.followingBy = new ArrayList<String>();
        this.sharingWith = new ArrayList<String>();
        this.setBlocked(new ArrayList<String>());
        this.created = DateTimeUtils.getCurrentUTCTime();

    }
    
    public void updateUser(ProfileForm form){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        if (!StringUtils.isEmpty(form.getUsername())){
            this.setUsername(form.getUsername());
        }
        
        if (!StringUtils.isEmpty(form.getEmail())){
            this.email = form.getEmail();
        }
        
        if (!StringUtils.isEmpty(form.getPassword())){
            this.passwordHash = encoder.encode(form.getPassword());
        }
        
        if (!this.options.get(UserOptions.INPUT_TYPE).equalsIgnoreCase(form.getInputType())){
            this.options.put(UserOptions.INPUT_TYPE, form.getInputType());
            this.optionsLastUpdate.put(UserOptions.INPUT_TYPE, DateTimeUtils.getCurrentUTCTime());
        }

//        if (!this.options.get(UserOptions.SHARE_STYLE).equalsIgnoreCase(form.getShareStyle())){
//            this.options.put(UserOptions.SHARE_STYLE, form.getShareStyle());
//            this.optionsLastUpdate.put(UserOptions.SHARE_STYLE, DateTimeUtils.getCurrentUTCTime());
//        }
        
        if (!this.options.get(UserOptions.PROFILE_VISIBILITY).equalsIgnoreCase(form.getProfileVisibility())){
            this.options.put(UserOptions.PROFILE_VISIBILITY, form.getProfileVisibility());
            this.optionsLastUpdate.put(UserOptions.PROFILE_VISIBILITY, DateTimeUtils.getCurrentUTCTime());
        }
        
//        if (this.options.get(UserOptions.TIMEZONE) != form.getInputType()){
//            this.options.put(UserOptions.TIMEZONE, form.getTimezone());
//            this.optionsLastUpdate.put(UserOptions.TIMEZONE, DateTimeUtils.getCurrentUTCTime());
//        }
        
        if (!this.options.get(UserOptions.NOTIFICATION_FREQUENCY).equalsIgnoreCase(form.getNotificationFrequency())){
            this.options.put(UserOptions.NOTIFICATION_FREQUENCY, form.getNotificationFrequencyAsString()); //in days
            this.optionsLastUpdate.put(UserOptions.NOTIFICATION_FREQUENCY,DateTimeUtils.getCurrentUTCTime());
        }
        
        
        if (!StringUtils.isEmpty(form.getAbout())){
            this.setAbout(form.getAbout());
        }
        
        this.enabled = StringUtils.isEmpty((form.getEnabled())) ? true : false;
        
        
        
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public HashMap<String, Date> getOptionsLastUpdate() {
        return optionsLastUpdate;
    }

    public void setOptionsLastUpdate(HashMap<String, Date> optionsLastUpdate) {
        this.optionsLastUpdate = optionsLastUpdate;
    }

    public Date getLastLogIn() {
        return lastLogIn;
    }

    public void setLastLogIn(Date lastLogIn) {
        this.lastLogIn = lastLogIn;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }


    public ArrayList<String> getFollowingBy() {
        return followingBy;
    }

    public void setFollowingBy(ArrayList<String> followingBy) {
        this.followingBy = followingBy;
    }

    public ArrayList<String> getSharingWith() {
        return sharingWith;
    }

    public void setSharingWith(ArrayList<String> sharingWith) {
        this.sharingWith = sharingWith;
    }

    public ArrayList<String> getFollowedBy() {
        return followedBy;
    }

    public void setFollowedBy(ArrayList<String> followedBy) {
        this.followedBy = followedBy;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public ArrayList<String> getBlocked() {
        return blocked;
    }

    public void setBlocked(ArrayList<String> blocked) {
        this.blocked = blocked;
    }

    public Date getLastNotification() {
        if (lastNotification == null){
            Calendar cal = new GregorianCalendar();
            cal.set(1990, 1, 1);
            return cal.getTime();
        }
        return lastNotification;
    }

    public void setLastNotification(Date lastNotification) {
        this.lastNotification = lastNotification;
    }


    
    
    
//	public Collection<Day> getDays() {
//		return days;
//	}
//
//	public void setDays(Collection<Day> days) {
//		this.days = days;
//	}

  
    
}