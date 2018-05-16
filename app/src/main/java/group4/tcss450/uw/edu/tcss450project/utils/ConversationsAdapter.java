package group4.tcss450.uw.edu.tcss450project.utils;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import group4.tcss450.uw.edu.tcss450project.ConversationsFragment;
import group4.tcss450.uw.edu.tcss450project.R;
import group4.tcss450.uw.edu.tcss450project.model.Conversation;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder> {

    private ArrayList<Conversation> mDataSet;
    private ConversationsFragment.OnConversationViewInteractionListener mListener;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mView;
        public TextView mTextView;
        public Button mButton;
        public ViewHolder(View v) {
            super(v);
            mView = v;
            mTextView = (TextView) v.findViewById(R.id.chat_name_text);
            mButton = (Button) v.findViewById((R.id.chat_remove_button));
        }
    }

    public ConversationsAdapter(ArrayList<Conversation> connections,
                                ConversationsFragment.OnConversationViewInteractionListener listener) {
        mDataSet = connections;
        mListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ConversationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversation, parent, false);

        ConversationsAdapter.ViewHolder vh = new ConversationsAdapter.ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ConversationsAdapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.mTextView.setText(mDataSet.get(position).toString());
        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onConversationSelected(mDataSet.get(position).getID());
            }
        });

        holder.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = mDataSet.get(position).getID();
                //Does it need to get removed from the dataset?
                mDataSet.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mDataSet.size() - position);
                 //Remove the item from the view


                //make webservice call to remove user from that chat in the database
                Log.d("Remove Conversation Testing", Integer.toString(id));
                //mListener.onConversationSelected(mDataSet.get(position).getID());
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

}