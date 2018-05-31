package group4.tcss450.uw.edu.tcss450project;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import group4.tcss450.uw.edu.tcss450project.utils.ListenManager;
import group4.tcss450.uw.edu.tcss450project.utils.SendPostAsyncTask;


/**
 * {@link Fragment} that handles a chat between users.
 */
public class ChatFragment extends Fragment {

    private String mUsername;
    private String mSendUrl;
    private int mChatID;
    private TextView mOutputTextView;
    private ListenManager mListenManager;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        v.findViewById(R.id.chatSendButton).setOnClickListener(view -> sendMessage());
        mOutputTextView = v.findViewById(R.id.chatOutputTextView);
        FloatingActionButton fab = Objects.requireNonNull(getActivity()).findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            mChatID = getArguments().getInt(getString(R.string.keys_args_conversationID));
        }

        // Get user preferences
        SharedPreferences prefs =
                Objects.requireNonNull(getActivity()).getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        if (!prefs.contains(getString(R.string.keys_prefs_username))) {
            throw new IllegalStateException("No username in prefs!");
        }

        mUsername = prefs.getString(getString(R.string.keys_prefs_username), "");
        getAllMessages();

        // Build the url for sending messages to endpoint
        mSendUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_send_message))
                .build()
                .toString();

        // Build the uri for receiving messages from webservice
        Uri retrieve = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_get_message))
                .appendQueryParameter(getString(R.string.keys_json_chat_id), Integer.toString(mChatID))
                .build();

        if (prefs.contains(getString(R.string.keys_prefs_time_stamp))) {
            //ignore all of the seen messages. You may want to store these messages locally
            mListenManager = new ListenManager.Builder(retrieve.toString(),
                    this::publishProgress)
                    .setTimeStamp(prefs.getString(getString(R.string.keys_prefs_time_stamp), "0"))
                    .setExceptionHandler(this::handleError)
                    .setDelay(1000)
                    .build();
        } else {
            //no record of a saved timestamp. must be a first time login
            mListenManager = new ListenManager.Builder(retrieve.toString(),
                    this::publishProgress)
                    .setExceptionHandler(this::handleError)
                    .setDelay(1000)
                    .build();

        }
    }

    private void handleError(Exception e) {
        handleError(e.getMessage());
    }

    @Override
    public void onResume() {
        super.onResume();
        mListenManager.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        String latestMessage = mListenManager.stopListening();
        SharedPreferences prefs = Objects.requireNonNull(getActivity())
                .getSharedPreferences(getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
        // Save the most recent message timestamp
        prefs.edit().putString(
                getString(R.string.keys_prefs_time_stamp),
                latestMessage)
                .apply();
    }

    /**
     * Starts an AsyncTask to send a message to the webservice
     */
    private void sendMessage() {
        // Build the JSON message
        JSONObject messageJson = new JSONObject();
        String msg = ((EditText) Objects.requireNonNull(getView()).
                findViewById(R.id.chatInputEditText)).getText().toString();
        try {
            messageJson.put(getString(R.string.keys_json_username), mUsername);
            messageJson.put(getString(R.string.keys_json_message), msg);
            messageJson.put(getString(R.string.keys_json_chat_id), mChatID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Start the AsyncTask
        new SendPostAsyncTask.Builder(mSendUrl, messageJson)
                .onPostExecute(this::parseSendMessageResult)
                .onCancelled(this::handleError).build().execute();
    }

    private void handleError(final String msg) {
        Log.e("CHAT ERROR!!!", msg);
    }

    /**
     * Udpates the messages displayed in a conversation
     *
     * @param messages The messages sent from the webservice
     */
    private void publishProgress(JSONObject messages) {
        final String[] msgs;
        if (messages.has(getString(R.string.keys_json_messages))) {
            try {
                // Parse the JSONArray of messages
                JSONArray jMessages = messages.getJSONArray(getString(R.string.keys_json_messages));
                msgs = new String[jMessages.length()];
                for (int i = 0; i < jMessages.length(); i++) {
                    JSONObject msg = jMessages.getJSONObject(i);
                    String username = msg.get(getString(R.string.keys_json_username)).toString();
                    String userMessage = msg.get(getString(R.string.keys_json_message)).toString();
                    msgs[i] = username + ":" + userMessage;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            // Update the text view of messages (display the conversation)
            Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                for (String msg : msgs) {
                    mOutputTextView.append(msg);
                    mOutputTextView.append(System.lineSeparator());
                }
            });
        }
    }

    /**
     * Starts an AsyncTask to get all of the message in a conversation
     * from the webservice.
     */
    private void getAllMessages() {
        // Build the url to send to endpoint
        String url = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath("getAllMessages")
                .build()
                .toString();

        // Build the JSON message to send
        JSONObject messageJson = new JSONObject();
        try {
            messageJson.put(getString(R.string.keys_json_chat_id), mChatID);
        } catch (Exception e) {
            e.getStackTrace();
        }

        // Start the AsyncTask
        new SendPostAsyncTask.Builder(url, messageJson)
                .onPostExecute(this::storeMessages)
                .onCancelled(this::handleError)
                .build().execute();
    }

    /**
     * Stores the messages for a conversation that are received from the
     * webservice.
     *
     * @param result The JSON sent by the webservice
     */
    private void storeMessages(String result) {
        try {
            // Parse the JSON
            JSONObject messages = new JSONObject(result);
            if (messages.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {
                String[] messageList;
                if (messages.has(getString(R.string.keys_json_messages))) {
                    try {
                        // Parse the array of messages
                        JSONArray jMessages = messages.getJSONArray(getString(R.string.keys_json_messages));
                        messageList = new String[jMessages.length()];
                        for (int i = 0; i < jMessages.length(); i++) {
                            // For each element, save the username and message
                            JSONObject msg = jMessages.getJSONObject(i);
                            String username = msg.get(getString(R.string.keys_json_username)).toString();
                            String userMessage = msg.get(getString(R.string.keys_json_message)).toString();
                            messageList[i] = username + ":" + userMessage;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                    // Update the text view of messages (display the conversation)
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                        mOutputTextView.setText("");
                        for (String msg : messageList) {
                            mOutputTextView.append(msg);
                            mOutputTextView.append(System.lineSeparator());
                        }
                    });
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks to see if a message was sent successfully and clears
     * the input text box if it was send
     *
     * @param result JSON message to parse
     */
    private void parseSendMessageResult(final String result) {
        try {
            JSONObject res = new JSONObject(result);
            // Check to see if the message was successfully sent
            if (res.get(getString(R.string.keys_json_success))
                    .toString().equals(getString(R.string.keys_json_success_value_true))) {
                // Clear the chat input text box
                ((EditText) Objects.requireNonNull(getView()).
                        findViewById(R.id.chatInputEditText)).setText("");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
