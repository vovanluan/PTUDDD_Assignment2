package entity;

import java.util.ArrayList;

/**
 * Created by Luan on 5/2/2016.
 */
public class DataHolder {
    private User user;

    public ArrayList<User> userList;

    public ArrayList<Course> courseList;

    private ArrayList<Notification> oldNotifications;

    private ArrayList<Notification> newNotifications;

    public ArrayList<Course> getCourseList() {
        return courseList;
    }

    public void setCourseList(ArrayList<Course> courseList) {
        this.courseList = courseList;
    }

    public ArrayList<User> getUserList() {
        return userList;
    }

    public void removeCourse(String coursId){
        for (Course c:
             courseList) {
            if(c.get_id().equals(coursId)) {
                courseList.remove(c);
                return;
            }
        }
    }

    public void setUserList(ArrayList<User> userList) {
        this.userList = userList;
    }

    public User getUser() {
        return user;
    }

    public User getUserById(String id) {
        for (User u: userList) {
            if(u.get_id().equals(id)){
                return u;
            }
        }
        return null;
    }

    public Course getCourseById(String id){
        for (Course c: courseList){
            if(c.get_id().equals(id)){
                return c;
            }
        }
        return null;
    }

    public void setUser(User data) {
        this.user = data;
    }

    public ArrayList<Notification> getOldNotifications() {
        return oldNotifications;
    }

    public void setOldNotifications(ArrayList<Notification> oldNotifications) {
        this.oldNotifications = oldNotifications;
    }

    public ArrayList<Notification> getNewNotifications() {
        return newNotifications;
    }

    public void setNewNotifications(ArrayList<Notification> newNotifications) {
        this.newNotifications = newNotifications;
    }

    private static final DataHolder holder = new DataHolder();

    public static synchronized DataHolder getInstance() {
        return holder;
    }
}
