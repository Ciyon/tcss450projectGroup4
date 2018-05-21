package group4.tcss450.uw.edu.tcss450project.utils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import group4.tcss450.uw.edu.tcss450project.R;
import group4.tcss450.uw.edu.tcss450project.model.Connection;

public class ConnectionsAdapter extends RecyclerView.Adapter<ConnectionsAdapter.ViewHolder> {

    private ArrayList<Connection> mDataSet;
    private OnConnectionAdapterInteractionListener mListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mUsernameTextView;
        public TextView mNameTextView;
        public TextView mEmailTextView;
        public Button mChatButton;
        public Button mDeleteButton;

        public ViewHolder(View v) {
            super(v);
            mUsernameTextView = v.findViewById(R.id.connection_username_text);
            mNameTextView = v.findViewById(R.id.connection_name_text);
            mEmailTextView = v.findViewById(R.id.connection_email_text);
            mChatButton = v.findViewById(R.id.connection_start_chat_button);
            mDeleteButton = v.findViewById(R.id.connection_remove_button);
        }
    }

    public ConnectionsAdapter(ArrayList<Connection> connections, OnConnectionAdapterInteractionListener listener) {
        mDataSet = connections;
        mListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ConnectionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_connection, parent, false);

        ConnectionsAdapter.ViewHolder vh = new ConnectionsAdapter.ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ConnectionsAdapter.ViewHolder holder, int position) {
        holder.mUsernameTextView.setText(mDataSet.get(position).getUserName());
        holder.mNameTextView.setText(mDataSet.get(position).getFirstName() + " " + mDataSet.get(position).getLastName());
        holder.mEmailTextView.setText(mDataSet.get(position).getEmail());
        holder.mChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //disable the button so the user cannot press it twice
                holder.mChatButton.setEnabled(false);
                mListener.onChatStarted(mDataSet.get(position).getId());
            }
        });

        holder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //disable the button so the user cannot press it twice
                //while the task removes the item from the database and the list
                holder.mDeleteButton.setEnabled(false);
                mListener.onConnectionDeleted(mDataSet.get(position).getUserName(),position);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public interface OnConnectionAdapterInteractionListener {
        void onConnectionDeleted(String contactUsername, int position);
        void onChatStarted(int contactId);
    }
}