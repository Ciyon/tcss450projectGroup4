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

public class SentRequestsAdapter extends RecyclerView.Adapter<SentRequestsAdapter.ViewHolder> {

    private ArrayList<Connection> mDataSet;
    private PendingRequestsAdapter.OnPendingRequestsAdapterInteractionListener mListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mUsernameTextView;
        public Button mDeleteButton;

        public ViewHolder(View v) {
            super(v);
            mUsernameTextView = v.findViewById(R.id.sent_request_name_text);
            mDeleteButton = v.findViewById(R.id.delete_request_button);
        }
    }

    public SentRequestsAdapter(ArrayList<Connection> connections, PendingRequestsAdapter.OnPendingRequestsAdapterInteractionListener listener) {
        mDataSet = connections;
        mListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SentRequestsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sent_request, parent, false);

        SentRequestsAdapter.ViewHolder vh = new SentRequestsAdapter.ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(SentRequestsAdapter.ViewHolder holder, int position) {
        holder.mUsernameTextView.setText(mDataSet.get(position).getUserName());
        holder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //disable the button so the user cannot press it twice
                holder.mDeleteButton.setEnabled(false);
                mListener.onConnectionDenied(mDataSet.get(position).getUserName(), position,false);
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

}


