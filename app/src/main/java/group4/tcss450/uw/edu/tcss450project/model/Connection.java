package group4.tcss450.uw.edu.tcss450project.model;

/**
 * Represents a single connection with relevent information for display.
 */
public class Connection {

    /* username and name */
    String userName;
    String firstName;
    String lastName;

    /* whether or not the connection is pending */
    boolean verified = false;

    public Connection(String userName, String firstName, String lastName) {
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
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

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
