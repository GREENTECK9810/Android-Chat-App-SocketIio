package com.example.chatapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chatapp.Adapter.ChatAdapter;
import com.example.chatapp.ChatApplication;
import com.example.chatapp.R;
import com.example.chatapp.model.Message;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "CHAT ACTIVITY";
    private Socket mSocket;
    private List<Message> messageList;
    private ChatAdapter mChatAdapter;
    RecyclerView mRecyclerView;
    EditText mMessageText;
    ImageView mSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageList = new ArrayList<>();
        mChatAdapter = new ChatAdapter(messageList, getApplicationContext());
        mRecyclerView = (RecyclerView) findViewById(R.id.chat_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mChatAdapter);
        mMessageText = (EditText) findViewById(R.id.message_text);
        mSend = (ImageView) findViewById(R.id.send);

        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();

        initSocketEvents();

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(mMessageText.getText())){
                    mMessageText.setText("");
                    return;
                }

                attemptSend();
            }
        });

    }

    private void attemptSend() {

        JSONObject obj = new JSONObject();
        try {
            obj.put("from", getIntent().getStringExtra("from"));
            obj.put("to", getIntent().getStringExtra("to"));
            obj.put("message", mMessageText.getText().toString());

            mSocket.emit("private message", obj);
            Message message = new Message();
            message.setSender(true);
            message.setMessage(mMessageText.getText().toString());
            if (messageList.size() >= 1){
                if (messageList.get(messageList.size()-1).isSender()){
                    message.setPosition("last");
                    if(messageList.get(messageList.size()-1).getPosition() == "single"){
                        messageList.get(messageList.size()-1).setPosition("first");
                    }
                    if(messageList.get(messageList.size()-1).getPosition() == "last"){
                        messageList.get(messageList.size()-1).setPosition("middle");
                    }
                }else {
                    message.setPosition("single");
                }
            }else {
                message.setPosition("single");
            }

            messageList.add(message);
            mChatAdapter.notifyDataSetChanged();
            mMessageText.setText("");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    private void initSocketEvents() {


        mSocket.on("private message", onPrivateMessage);

    }



    Emitter.Listener onPrivateMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    JSONObject data = (JSONObject) args[0];
                    String username = "";
                    String message = "";

                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(ChatActivity.this, message, Toast.LENGTH_SHORT).show();
                    Message message1 = new Message();
                    message1.setMessage(message);
                    message1.setSender(false);

                    if (messageList.size() >= 1){
                        if (!messageList.get(messageList.size()-1).isSender()){
                            message1.setPosition("last");
                            if(messageList.get(messageList.size()-1).getPosition() == "single"){
                                messageList.get(messageList.size()-1).setPosition("first");
                            }
                            if(messageList.get(messageList.size()-1).getPosition() == "last"){
                                messageList.get(messageList.size()-1).setPosition("middle");
                            }
                        }else {
                            message1.setPosition("single");
                        }
                    }else {
                        message1.setPosition("single");
                    }

                    messageList.add(message1);
                    mChatAdapter.notifyDataSetChanged();


                }
            });
        }
    };
}