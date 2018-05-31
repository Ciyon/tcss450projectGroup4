package group4.tcss450.uw.edu.tcss450project.model;

/**
 * Represents a single connection with relevant information for display.
 */
public class Connection {

    /* username and name */
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private int id;

    public Connection(String userName, String firstName, String lastName, String email, int id) {
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }

}
