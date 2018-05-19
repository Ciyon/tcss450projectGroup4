package group4.tcss450.uw.edu.tcss450project.utils;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import group4.tcss450.uw.edu.tcss450project.R;
import group4.tcss450.uw.edu.tcss450project.model.Connection;

public class NewConversationAdapter extends RecyclerView.Adapter<NewConversationAdapter.ViewHolder> {

    private OnConnectionSelectedInteractionListener mListener;
    private ArrayList<Connection> mDataSet;
    private boolean multiSelect = false;
    private ArrayList<Integer> mSelectedItems = new ArrayList<Integer>();

    public NewConversationAdapter(ArrayList<Connection> connections, OnConnectionSelectedInteractionListener listener) {
        mDataSet = connections;
        mListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NewConversationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_select_user, parent, false);

        NewConversationAdapter.ViewHolder vh = new NewConversationAdapter.ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(NewConversationAdapter.ViewHolder holder, int position) {
        holder.mUsernameTextView.setText(mDataSet.get(position).getUserName());
        holder.mUsernameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!holder.selected) {
                    holder.mUsernameTextView.setTextAppearance(R.style.TextAppearance_AppCompat_Large);
                    holder.selected = true;
                    mSelectedItems.add(position);
                    mListener.onConnectionSelected(mSelectedItems);
                } else {
                    holder.mUsernameTextView.setTextAppearance(R.style.TextAppearance_AppCompat_Medium);
                    holder.selected = false;
                    mSelectedItems.remove((Integer)position);
                    mListener.onConnectionSelected(mSelectedItems);
                }

            }


        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public ArrayList<Integer> getSelectedItems() {
        return mSelectedItems;
    }
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mUsernameTextView;
        public boolean selected;
        public ViewHolder(View v) {
            super(v);
            selected = false;
            mUsernameTextView = v.findViewById(R.id.new_chat_name_text);
        }

    }

    public interface OnConnectionSelectedInteractionListener {
        void onConnectionSelected(ArrayList<Integer> selectedItems);
    }
}
