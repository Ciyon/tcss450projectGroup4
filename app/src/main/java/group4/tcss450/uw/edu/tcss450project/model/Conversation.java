package group4.tcss450.uw.edu.tcss450project.model;
import android.util.Log;

import java.util.ArrayList;

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

    public ArrayList<String> getMembers() {
        ArrayList<String> copy = members;
        return copy;
    }

    public String getMembersLabel() {
        String label = "";
        if(members.size() == 0) { //only our user is in this chat
            label += "Only You =(";
        } else {
            int count = 1;
            for(int i = 0; i < members.size(); i++) {
                if(count <= MAX_DISPLAY) { //only list max members
                    label += members.get(i);
                    if (i < members.size() - 1) { //more to list
                        label += ", ";
                        count++;
                    }
                } else { // have already displayed the names of max members, display ...(+n) to show the rest
                    label += "...+(" + (members.size() - MAX_DISPLAY) + ")";
                    i = members.size();
                }
            }
        }
        return label;
    }

    public String toString() {
        return "Chat ID: " + id + "Members: " + members.toString();
    }
}
