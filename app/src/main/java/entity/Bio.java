package entity;

/**
 * Created by Luan on 4/18/2016.
 */
public class Bio {
    private String firstName;
    private String lastName;
    private int age;
    private String university;
    private String phoneNumber;

    public Bio() {
        this.firstName = "some first name";
        this.lastName = "some last name";
        this.age = 10;
        this.university = "some university";
        this.phoneNumber = "090111111";
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
