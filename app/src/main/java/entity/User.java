package entity;

/**
 * Created by Luan on 29/03/2016.
 */

import java.io.Serializable;

/**
 *
 * @author Luan
 */
public class User{
    private Local local;
    private Bio bio;
    private String[] image;

    public User(){
        local = new Local();
        bio = new Bio();
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
}

