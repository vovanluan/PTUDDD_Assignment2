package entity;

/**
 * Created by Luan on 5/2/2016.
 */
public class DataHolder {
    private User user;
    public User getData() {return user;}
    public void setData(User data) {this.user = data;}

    private static final DataHolder holder = new DataHolder();
    public static synchronized DataHolder getInstance() {return holder;}
}
