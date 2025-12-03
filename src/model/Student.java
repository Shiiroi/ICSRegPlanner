package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Student implements Serializable {
	private static final long serialVersionUID = -1L;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String password;
    private String program;
    private transient CoursePlanner coursePlanner; 
    // ADDED PROFILE DEFAULT PICTURE PATH
    private String profilePicturePath = "/img/default-profile.png";
    
    // NEW: save the courses inside the selected schedule
    private Map<String, List<Course>> savedSchedules = new HashMap<>();

    // NEW: keep a reference to the currently active schedule name
    private String activeScheduleName = "Default";
    
    public Student() {
    	this.coursePlanner = new CoursePlanner();
    }

    public Student(String firstName, String middleName, String lastName, 
                String email, String password, String program) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.program = program;
        //NEW: add the courses to default initially
        this.savedSchedules.put("Default", new ArrayList<>());
        this.activeScheduleName = "Default";
        this.coursePlanner = new CoursePlanner();
    }

    // getter and setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getProgram() { return program; }
    public void setProgram(String program) { this.program = program; }

    public String getFullName() {
        if (middleName != null && !middleName.isEmpty()) {
            return firstName + " " + middleName + " " + lastName;
        }
        return firstName + " " + lastName;
    }
    
    //NEW: added getters and setters for the schedules
    public void setSchedule(String name, List<Course> courses) {
    	if (name != null && courses != null) {
            savedSchedules.put(name, new ArrayList<>(courses));
        }
    }

    public List<Course> getSchedule(String name) {
        return savedSchedules.getOrDefault(name, new ArrayList<>());
    }

    public List<Course> getActiveSchedule() {
        return getSchedule(activeScheduleName);
    }

    public void setActiveSchedule(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.activeScheduleName = name;
            savedSchedules.putIfAbsent(name, new ArrayList<>());
        }
    }

    public Set<String> getSavedScheduleNames() {
        return savedSchedules.keySet();
    }

    public String getActiveScheduleName() {
        return activeScheduleName;
    }
    public CoursePlanner getCoursePlanner() {
        if (coursePlanner == null) {
            coursePlanner = new CoursePlanner();
        }
        return coursePlanner;
    }
    


    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    public void setProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }

}