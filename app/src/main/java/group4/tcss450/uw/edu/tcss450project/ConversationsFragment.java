package group4.tcss450.uw.edu.tcss450project;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import group4.tcss450.uw.edu.tcss450project.model.Conversation;
import group4.tcss450.uw.edu.tcss450project.utils.ConversationsAdapter;
import group4.tcss450.uw.edu.tcss450project.utils.SendPostAsyncTask;


/**
 * {@link Fragment} that handles conversation functionality
 */
public class ConversationsFragment extends Fragment implements ConversationsAdapter.OnConversationDeleteInteractionListener {
    private OnConversationViewInteractionListener mListener;
    private RecyclerView.Adapter mAdapter;
    private String mUsername;
    private String mSendUrl;
    private String mDeleteUrl;
    private ArrayList<Conversation> mDataSet;
    private int mDeletePosition;
    private ProgressBar mProgressBar;

    public ConversationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversations, container, false);
        //Make the FAB appear on this fragment
        FloatingActionButton fab = Objects.requireNonNull(getActivity()).findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

        //Initialize recycler view with an empty dataset
        RecyclerView mRecyclerView = view.findViewById(R.id.recyclerSelectConversations);
        mRecyclerView.setHasFixedSize(true);

        mProgressBar = view.findViewById(R.id.progressBarConversations);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        //Start with an empty dataset.
        mDataSet = new ArrayList<>();
        mAdapter = new ConversationsAdapter(mDataSet, mListener, this);

        setUpRequestUrls();
        requestConversationsList();

        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mProgressBar.setVisibility(View.GONE);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnConversationViewInteractionListener) {
            mListener = (OnConversationViewInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;

    }

    /**
     * Sets up parameters/urls for making a connection request,
     * removing a connection, creating a new chat with a connection,
     * and adding multiple connections to a chat.
     */
    private void setUpRequestUrls() {
        SharedPreferences prefs =
                Objects.requireNonNull(getActivity()).getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        if (!prefs.contains(getString(R.string.keys_prefs_username))) {
            throw new IllegalStateException("No username in prefs!");
        }
        mUsername = prefs.getString(getString(R.string.keys_prefs_username), "");

        mSendUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_get_conversation_ids_and_members))
                .build()
                .toString();
        mDeleteUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_delete_conversation))
                .build()
                .toString();
    }

    /**
     * Start an async task to get the user's list of conversations.
     */
    private void requestConversationsList() {
        mProgressBar.setVisibility(View.VISIBLE);

        // Build the JSON message
        JSONObject messageJson = new JSONObject();
        try {
            messageJson.put(getString(R.string.keys_json_username), mUsername);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Start async task
        new SendPostAsyncTask.Builder(mSendUrl, messageJson)
                .onPostExecute(this::createConversationsList)
                .onCancelled(this::handleError)
                .build().execute();

    }

    private void handleError(final String msg) {
        mProgressBar.setVisibility(View.GONE);
        Log.e("Conversation ERROR!!!", msg);
    }

    /**
     * Sets up and formats the user's list of conversations
     *
     * @param result JSON result to parse
     */
    private void createConversationsList(final String result) {
        try {
            mProgressBar.setVisibility(View.GONE);
            JSONObject res = new JSONObject(result);
            if (res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {

                @SuppressLint("UseSparseArrays") Map<Integer, ArrayList<String>> data = new HashMap<>();
                if (res.has(getString(R.string.keys_json_chat_information))) {
                    JSONArray chats = res.getJSONArray(getString(R.string.keys_json_chat_information));
                    int chatId;
                    String username;
                    for (int i = 0; i < chats.length(); i++) {
                        JSONObject chat = chats.getJSONObject(i);
                        chatId = chat.getInt(getString(R.string.keys_json_chatid));
                        username = chat.getString(getString(R.string.keys_json_username));
                        if (data.containsKey(chatId)) {
                            data.get(chatId).add(username);
                        } else {
                            ArrayList<String> members = new ArrayList<>();
                            members.add(username);
                            data.put(chatId, members);
                        }
                    }

                    ArrayList<Conversation> conversations = new ArrayList<>();
                    for (int key : data.keySet()) {
                        ArrayList<String> finalMembersList = data.get(key);
                        finalMembersList.remove(mUsername.toLowerCase());
                        conversations.add(new Conversation(key, finalMembersList));
                    }
                    //conversations.add(new Conversation(chatId,null));
                    //Update the recycler view
                    mDataSet.addAll(conversations);
                    mAdapter.notifyDataSetChanged();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts a {@link SendPostAsyncTask} to delete a conversation.
     *
     * @param conversationID the conversation to be deleted
     * @param position       the conversation's position in the recycler view
     */
    @Override
    public void onConversationDeleted(int conversationID, int position) {
        mDeletePosition = position;
        JSONObject messageJson = new JSONObject();
        try {
            messageJson.put(getString(R.string.keys_json_username), mUsername);
            messageJson.put(getString(R.string.keys_json_chat_id), conversationID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        new SendPostAsyncTask.Builder(mDeleteUrl, messageJson)
                .onPostExecute(this::deleteConversation)
                .onCancelled(this::handleError)
                .build().execute();
    }

    /**
     * If the webservice has successfully deleted a conversation, remove
     * it from our dataset as well.
     *
     * @param result
     */
    private void deleteConversation(final String result) {
        try {
            JSONObject res = new JSONObject(result);
            if (res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {
                mDataSet.remove(mDeletePosition);
                mAdapter.notifyItemRemoved(mDeletePosition);
                mAdapter.notifyItemRangeChanged(mDeletePosition, mDataSet.size() - mDeletePosition);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface OnConversationViewInteractionListener {
        void onConversationSelected(int conversationID);
    }

}
