package group4.tcss450.uw.edu.tcss450project.model;

public class Conversation {


    private Connection[] members;

    public Conversation(Connection[] members) {
        this.members = members;
    }

    public String toString() {
        StringBuilder b = new StringBuilder();
        for (Connection c : members) {
            b.append(c.getUserName());
        }
        return b.toString();
    }
}
