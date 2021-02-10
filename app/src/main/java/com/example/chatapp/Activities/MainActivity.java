package com.example.chatapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.chatapp.Api.UserApi;
import com.example.chatapp.ChatApplication;
import com.example.chatapp.R;
import com.example.chatapp.RetrofitClient;
import com.example.chatapp.model.User;
import com.github.nkzawa.emitter.Emitter;

import com.github.nkzawa.socketio.client.Socket;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private String TAG = "ACTIVITY";
    EditText mSearch;
    TextView mUsername;
    Button mSearchBtn, mChatButton;
    String token="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjYwMWVkNjU3OTg0ZDIyMjA5ODYxMjIxOCIsImlhdCI6MTYxMjYzNDcxM30.0qsWf9EHsA1w477MU2P9F-Z6BB71GGuQ0rTHjRajWIU";
    UserApi userApi;
    private Socket mSocket;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init views
        mSearch = findViewById(R.id.search);
        mUsername = findViewById(R.id.username);
        mSearchBtn = findViewById(R.id.search_button);
        mChatButton = findViewById(R.id.navigate_chat_activity);

        //connect socket
        ChatApplication application = (ChatApplication) getApplication();
        application.initSocket(getIntent().getStringExtra("username"));
        mSocket = application.getSocket();
        initSocketEvents();

        //init retrofit
        RetrofitClient retrofitClient = (RetrofitClient) getApplication();
        Retrofit retrofit = retrofitClient.getRetrofit();

        userApi = retrofit.create(UserApi.class);

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUser();
            }
        });

        mChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initSocketEvents() {

        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off("private message");
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
    }


    private void getUser() {

        Call<User> call = userApi.user(mSearch.getText().toString(), getIntent().getStringExtra("token")  );

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(!response.isSuccessful()){
                    mUsername.setText(response.message());
                    return;
                }

                mUsername.setText(response.body().getUsername());
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("to",response.body().getUsername());
                intent.putExtra("from", getIntent().getStringExtra("username")  );
                startActivity(intent);

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                mUsername.setText(t.getMessage());
            }
        });

    }

    //socket events listener
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "connected...");
            // This doesn't run in the UI thread, so use:
            // .runOnUiThread if you want to do something in the UI

        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "Error connecting...");
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "disconnected...");
        }
    };

}