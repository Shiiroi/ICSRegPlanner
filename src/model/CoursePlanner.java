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

    public boolean hasConflict(Course newCourse) {
        // check each enrolled course against the new course
        for (Course c : enrolledCourses) {
            // skip courses with no fixed schedule (TBA = to be announced)
            if (c.getDays().equals("TBA") || newCourse.getDays().equals("TBA")) {
                continue;
            }
            
            // expand day strings  TTh becomes [T, Th]
            List<String> days1 = StudentDashboard.expandDays(c.getDays());
            List<String> days2 = StudentDashboard.expandDays(newCourse.getDays());
            
            // check if courses meet on any common day
            boolean sameDay = false;
            for (String day : days1) {
                if (days2.contains(day)) {
                    sameDay = true;
                    break;
                }
            }
            
            // continue if no common days
            if (!sameDay) {
                continue;
            }
            
            // get time ranges
            String[] time1 = c.getTimes().split("-");
            String[] time2 = newCourse.getTimes().split("-");
            
            // skip if time format is invalid (TBA or missing range line 7:00 AM - nothing)
            if (time1.length < 2 || time2.length < 2) {
                continue;
            }
            
            // convert to 24-hour format for comparison
            int start1 = StudentDashboard.convertTo24Hour(time1[0].trim(), c.getSection());
            int end1 = StudentDashboard.convertTo24Hour(time1[1].trim(), c.getSection());
            int start2 = StudentDashboard.convertTo24Hour(time2[0].trim(), newCourse.getSection());
            int end2 = StudentDashboard.convertTo24Hour(time2[1].trim(), newCourse.getSection());
            
            // skip if time conversion failed
            if (start1 < 0 || end1 < 0 || start2 < 0 || end2 < 0) {
                continue;
            }
            
            // check overlap: course1 starts before course2 ends AND course1 ends after course2 starts
            if (start1 < end2 && end1 > start2) {
                return true; // conflict found
            }
        }
        return false; // no conflicts
    }


    
    public void setEnrolledCourses(List<Course> courses) {
    	this.enrolledCourses.clear();
        this.enrolledCourses.addAll(courses);
    }
}
