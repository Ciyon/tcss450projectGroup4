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

public class PendingRequestsAdapter extends RecyclerView.Adapter<PendingRequestsAdapter.ViewHolder> {

    private ArrayList<Connection> mDataSet;
    private OnPendingRequestsAdapterInteractionListener mListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mUsernameTextView;
        public Button mAcceptButton;
        public Button mDenyButton;

        public ViewHolder(View v) {
            super(v);
            mUsernameTextView = v.findViewById(R.id.connection_username_text);
            mAcceptButton = v.findViewById(R.id.accept_request_button);
            mDenyButton = v.findViewById(R.id.deny_request_button);
        }
    }

    public PendingRequestsAdapter(ArrayList<Connection> connections, OnPendingRequestsAdapterInteractionListener listener) {
        mDataSet = connections;
        mListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PendingRequestsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pending_request, parent, false);

        PendingRequestsAdapter.ViewHolder vh = new PendingRequestsAdapter.ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(PendingRequestsAdapter.ViewHolder holder, int position) {
        holder.mUsernameTextView.setText(mDataSet.get(position).getUserName());
        holder.mAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //disable the button so the user cannot press it twice
                holder.mAcceptButton.setEnabled(false);
                mListener.onConnectionAccepted(mDataSet.get(position).getUserName(), position);
            }
        });

        holder.mDenyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //disable the button so the user cannot press it twice
                //while the task removes the item from the database and the list
                holder.mDenyButton.setEnabled(false);
                mListener.onConnectionDenied(mDataSet.get(position).getUserName(),position, true);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public interface OnPendingRequestsAdapterInteractionListener {
        void onConnectionAccepted(String contactUsername, int position);
        void onConnectionDenied(String contactUsername, int position, boolean fromPending);
    }

}
