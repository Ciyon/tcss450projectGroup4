package group4.tcss450.uw.edu.tcss450project.model;
import java.util.ArrayList;

public class Conversation {

    private int id;
    private ArrayList<Connection> members;

    public Conversation(int id, ArrayList<Connection> members) {
        this.id = id;
        this.members = members;
    }

    public int getID() {
        return id;
    }

    public String toString() {
        return Integer.toString(id);
    }
}
