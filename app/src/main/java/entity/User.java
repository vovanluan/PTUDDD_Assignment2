package entity;

import java.util.ArrayList;

/**
 * Created by Luan on 29/03/2016.
 */
public class User {
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
    private ArrayList<String> cards = new ArrayList<>();
    private ArrayList<String> followers = new ArrayList<>();
    private ArrayList<String> following = new ArrayList<>();
    private ArrayList<String> upvoted = new ArrayList<>();
    private ArrayList<String> reviews = new ArrayList<>();
    private ArrayList<String> students = new ArrayList<>();
    private ArrayList<String> teachers = new ArrayList<>();
    public User() {
        local = new Local();
        bio = new Bio();
        cards = new ArrayList<>();
        followers = new ArrayList<>();
        following = new ArrayList<>();
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

    public ArrayList<String> getCards() {
        return cards;
    }

    public void setCards(ArrayList<String> cards) {
        this.cards = cards;
    }

    public ArrayList<String> getFollowing() {
        return following;
    }

    public void setFollowing(ArrayList<String> following) {
        this.following = following;
    }

    public ArrayList<String> getFollowers() {
        return followers;
    }

    public void setFollowers(ArrayList<String> followers) {
        this.followers = followers;
    }

    public ArrayList<String> getUpvoted() {
        return upvoted;
    }

    public void setUpvoted(ArrayList<String> upvoted) {
        this.upvoted = upvoted;
    }

    public ArrayList<String> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<String> reviews) {
        this.reviews = reviews;
    }

    public ArrayList<String> getStudents() {
        return students;
    }

    public void setStudents(ArrayList<String> students) {
        this.students = students;
    }

    public ArrayList<String> getTeachers() {
        return teachers;
    }

    public void setTeachers(ArrayList<String> teachers) {
        this.teachers = teachers;
    }
}

