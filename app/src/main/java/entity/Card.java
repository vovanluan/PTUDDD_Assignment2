package entity;

/**
 * Created by Luan on 5/2/2016.
 */


public class Card
{
    private String _id;

    private int status;

    private User create_by;

    private String __v;

    private int upvotes;

    private String date;

    private String updatedAt;

    private String time;

    private String title;

    private String category;

    private String createdAt;

    private String description;

    private int rating;

    private String place;


    public Card(){
        this.create_by = new User();
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getStatus ()
    {
        return status;
    }

    public void setStatus (int status)
    {
        this.status = status;
    }

    public User getCreate_by ()
    {
        return create_by;
    }

    public void setCreate_by (User create_by)
    {
        this.create_by = create_by;
    }

    public String get__v ()
    {
        return __v;
    }

    public void set__v (String __v)
    {
        this.__v = __v;
    }

    public int getUpvotes ()
    {
        return upvotes;
    }

    public void setUpvotes (int upvotes)
    {
        this.upvotes = upvotes;
    }

    public String getDate ()
    {
        return date;
    }

    public void setDate (String date)
    {
        this.date = date;
    }

    public String getUpdatedAt ()
    {
        return updatedAt;
    }

    public void setUpdatedAt (String updatedAt)
    {
        this.updatedAt = updatedAt;
    }

    public String getTime ()
    {
        return time;
    }

    public void setTime (String time)
    {
        this.time = time;
    }

    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

    public String getCategory ()
    {
        return category;
    }

    public void setCategory (String category)
    {
        this.category = category;
    }

    public String getCreatedAt ()
    {
        return createdAt;
    }

    public void setCreatedAt (String createdAt)
    {
        this.createdAt = createdAt;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public int getRating ()
    {
        return rating;
    }

    public void setRating (int rating)
    {
        this.rating = rating;
    }

    public String getPlace ()
    {
        return place;
    }

    public void setPlace (String place)
    {
        this.place = place;
    }

}


