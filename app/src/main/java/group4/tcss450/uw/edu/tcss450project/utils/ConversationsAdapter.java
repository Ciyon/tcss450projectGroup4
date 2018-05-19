package group4.tcss450.uw.edu.tcss450project.utils;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import group4.tcss450.uw.edu.tcss450project.ConversationsFragment;
import group4.tcss450.uw.edu.tcss450project.R;
import group4.tcss450.uw.edu.tcss450project.model.Conversation;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder> {

    private ArrayList<Conversation> mDataSet;
    private ConversationsFragment.OnConversationViewInteractionListener mSelectListener;
    private OnConversationDeleteInteractionListener mDeleteListener;
    // Provide a reference to the views for each data item
    public class ViewHolder extends RecyclerView.ViewHolder {

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

    public ConversationsAdapter(ArrayList<Conversation> conversations,
                                ConversationsFragment.OnConversationViewInteractionListener selectListener,
                                OnConversationDeleteInteractionListener deleteListener) {
        mDataSet = conversations;
        mSelectListener = selectListener;
        mDeleteListener = deleteListener;
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

        holder.mTextView.setText(mDataSet.get(position).toString());
        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectListener.onConversationSelected(mDataSet.get(position).getID());
            }
        });

        holder.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //disable the button so the user cannot press it twice
                //while the task removes the item from the database and the list
                holder.mButton.setEnabled(false);
                mDeleteListener.onConversationDeleted(mDataSet.get(position).getID(),position);
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public interface OnConversationDeleteInteractionListener {
        void onConversationDeleted(int conversationID, int position);
    }

}