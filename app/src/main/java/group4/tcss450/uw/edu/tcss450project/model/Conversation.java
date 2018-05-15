package group4.tcss450.uw.edu.tcss450project.model;

public class Conversation {

    private int id;
    private Connection[] members;

    public Conversation(int id, Connection[] members) {
        this.id = id;
        this.members = members;
    }

    public String toString() {
        /*
        StringBuilder b = new StringBuilder();
        for (Connection c : members) {
            b.append(c.getUserName());
        }
        return b.toString();
        */
        return Integer.toString(id);
    }
}
