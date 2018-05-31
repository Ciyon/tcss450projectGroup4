package group4.tcss450.uw.edu.tcss450project.model;

import java.util.ArrayList;

/**
 * Represents a single conversation with relevant information for display.
 */
public class Conversation {
    //Max number of members to display
    private static final int MAX_DISPLAY = 4;
    private int id;
    private ArrayList<String> members;

    public Conversation(int id, ArrayList<String> members) {
        this.id = id;
        this.members = members;
    }

    public int getID() {
        return id;
    }

    /**
     * Get a text representation of every member in the conversation.
     *
     * @return A string to display in a label.
     */
    public String getMembersLabel() {
        String label = "";
        if (members.size() == 0) { //only our user is in this chat
            label += "Only You =(";
        } else {
            int count = 1;

            // Build the members label
            StringBuilder labelBuilder = new StringBuilder();
            for (int i = 0; i < members.size(); i++) {
                if (count <= MAX_DISPLAY) { //only list max members
                    labelBuilder.append(members.get(i));
                    if (i < members.size() - 1) { //more to list
                        labelBuilder.append(", ");
                        count++;
                    }
                } else { // have already displayed the names of max members, display ...(+n) to show the rest
                    labelBuilder.append("...+(").append(members.size() - MAX_DISPLAY).append(")");
                    i = members.size();
                }
            }
            label = labelBuilder.toString();
        }
        return label;
    }

    public String toString() {
        return "Chat ID: " + id + "Members: " + members.toString();
    }
}
