package model;

import java.util.ArrayList;
import java.util.List;

import application.StudentDashboard;

public class CoursePlanner {
	private List<Course> enrolledCourses;
	
	
	public CoursePlanner() {
		this.enrolledCourses = new ArrayList<>();
	}
	
    public List<Course> getEnrolledCourses() {
        return enrolledCourses;
    }
    
    public boolean addCourse(Course course) {
        for (Course c : enrolledCourses) {
            if (c.getCourseCode().equals(course.getCourseCode()) &&
                StudentDashboard.isLab(c.getSection()) == StudentDashboard.isLab(course.getSection())) {
                return false;
            }
        }
        enrolledCourses.add(course);
        return true;
    }
    
    public boolean removeCourse(Course course) {
        for (int i = 0; i<enrolledCourses.size();i++) {
            if (enrolledCourses.get(i).getCourseCode().equals(course.getCourseCode())) {
            	enrolledCourses.remove(i);
                return true;
            }
        }
        return false;
    }

    public int getTotalUnits() {
    	int totalUnits = 0;
    	
        for (Course c : enrolledCourses) {
        	int unit = c.getUnits();
        	totalUnits += unit;
        }
        return totalUnits;
    } 

    // Check for schedule conflicts 
    public boolean hasConflict(Course newCourse) {
        // Iterate through all enrolled courses
        for (Course c : enrolledCourses) {
            // Skip courses with no fixed schedule (TBA = to be announced)
            if (c.getDays().equalsIgnoreCase("TBA") || newCourse.getDays().equalsIgnoreCase("TBA")) {
                continue;
            }
            
            // Expand abbreviated day strings (e.g., "TTh" becomes ["Tue", "Thu"])
            List<String> days1 = StudentDashboard.expandDays(c.getDays());
            List<String> days2 = StudentDashboard.expandDays(newCourse.getDays());
            
            // Check if courses share any common day
            boolean sameDay = false;
            for (String day : days1) {
                if (days2.contains(day)) {
                    sameDay = true;
                    break;
                }
            }
            
            // Skip if courses don't meet on the same day
            if (!sameDay) {
                continue;
            }
            
            // Parse time ranges 
            String[] time1 = c.getTimes().split("-");
            String[] time2 = newCourse.getTimes().split("-");
            
            // Skip if time format is invalid
            if (time1.length < 2 || time2.length < 2) {
                continue;
            }
            
            // Extract start and end hours for both courses
            int startHour1 = parseHour(time1[0].trim());
            int endHour1 = parseHour(time1[1].trim());
            int startHour2 = parseHour(time2[0].trim());
            int endHour2 = parseHour(time2[1].trim());
            
            // Skip if any hour is invalid
            if (startHour1 == -1 || endHour1 == -1 || startHour2 == -1 || endHour2 == -1) {
                continue;
            }
            
            // Convert to 24-hour format for accurate comparison
            int start1, end1, start2, end2;
            
            // Convert first course to 24-hour format
            if (endHour1 < startHour1) {
                // Case 1: End hour is smaller (crosses noon)
                start1 = startHour1;
                end1 = (endHour1 == 12) ? 12 : endHour1 + 12;
            } else {
                if (startHour1 >= 7) {
                    // Case 2: Morning class (7 AM or later, before noon)
                    start1 = startHour1;
                    end1 = endHour1;
                } else {
                    // Case 3: Afternoon class (1-6 PM range)
                    start1 = (startHour1 == 12) ? 12 : startHour1 + 12;
                    end1 = (endHour1 == 12) ? 12 : endHour1 + 12;
                }
            }
            
            // Convert second course to 24-hour format using same logic
            if (endHour2 < startHour2) {
                start2 = startHour2;
                end2 = (endHour2 == 12) ? 12 : endHour2 + 12;
            } else {
                if (startHour2 >= 7) {
                    start2 = startHour2;
                    end2 = endHour2;
                } else {
                    start2 = (startHour2 == 12) ? 12 : startHour2 + 12;
                    end2 = (endHour2 == 12) ? 12 : endHour2 + 12;
                }
            }
            
            // Check for time overlap using interval intersection formula:

            if (start1 < end2 && end1 > start2) {
                return true; // Conflict found
            }
        }
        return false; // No conflicts found
    }

    // Helper function to get time to int 
    private int parseHour(String timeStr) {
        try {
            String hourPart = timeStr.split(":")[0].trim();
            return Integer.parseInt(hourPart);
        } catch (Exception e) {
            return -1;
        }
    }

    
    public void setEnrolledCourses(List<Course> courses) {
    	this.enrolledCourses.clear();
        this.enrolledCourses.addAll(courses);
    }
}
