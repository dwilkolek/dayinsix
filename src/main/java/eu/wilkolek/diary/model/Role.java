package eu.wilkolek.diary.model;

public class Role {

    private RoleEnum roleName;
    
    public String getRole(){
    	return roleName.name();
    }

}