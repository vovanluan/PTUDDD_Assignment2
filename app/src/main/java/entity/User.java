package entity;

import java.util.ArrayList;

/**
 * Created by Luan on 29/03/2016.
 */
public class User{
    private String _id;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    private Local local;
    private Bio bio;
    private String[] image;
    private ArrayList<Course> courses;

    public User(){
        local = new Local();
        bio = new Bio();
        courses = new ArrayList<>();

    }
    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }

    public Bio getBio() {
        return bio;
    }

    public void setBio(Bio bio) {
        this.bio = bio;
    }

    public String[] getImage() {
        return image;
    }

    public void setImage(String[] image) {
        this.image = image;
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<Course> courses) {
        this.courses = courses;
    }
}

