package group4.tcss450.uw.edu.tcss450project.model;

import android.content.SharedPreferences;
import android.net.Uri;

import group4.tcss450.uw.edu.tcss450project.R;

/**
 * Represents a single connection with relevent information for display.
 */
public class Connection {

    /* username and name */
    private String userName;
    private String firstName;
    private String lastName;
    private String email;

    private boolean isSelected = false;

    /* whether or not the connection is pending */
    private boolean verified = false;

    public Connection(String userName, String firstName, String lastName, String email) {
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() { return email; }

    //Probably do not need
    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }
}
