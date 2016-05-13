package entity;

/**
 * Created by Admin on 5/12/2016.
 */
public class Review {
    private String title;
    private String description;
    private int rating;
    private String created_by;
    private String for_card;
    private String _id;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getFor_card() {
        return for_card;
    }

    public void setFor_card(String for_card) {
        this.for_card = for_card;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
