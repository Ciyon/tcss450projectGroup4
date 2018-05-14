package group4.tcss450.uw.edu.tcss450project.utils;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import group4.tcss450.uw.edu.tcss450project.R;
import group4.tcss450.uw.edu.tcss450project.model.Connection;

public class NewConversationAdapter extends ConnectionsAdapter {

    public NewConversationAdapter(Connection[] connections) {
        super(connections);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(NewConversationAdapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Connection connection = mDataSet[position];
        holder.mTextView.setText(mDataSet[position].getUserName());
        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connection.setSelected(!connection.isSelected());
                holder.mTextView.setBackgroundColor(connection.isSelected() ? Color.LTGRAY : Color.WHITE);
            }
        });

    }

}