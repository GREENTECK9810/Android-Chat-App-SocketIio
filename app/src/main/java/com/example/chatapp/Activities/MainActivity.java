package com.example.chatapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.chatapp.Api.UserApi;
import com.example.chatapp.ChatApplication;
import com.example.chatapp.Constant;
import com.example.chatapp.R;
import com.example.chatapp.RetrofitClient;
import com.example.chatapp.model.User;
import com.github.nkzawa.emitter.Emitter;

import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private String TAG = "ACTIVITY";
    EditText mSearch;
    TextView mUsername;
    Button mSearchBtn, mChatButton;
    UserApi userApi;
    private Socket mSocket;
    SharedPreferences sharedPreferences;
    Retrofit retrofit;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(Constant.PREFS_NAME, MODE_PRIVATE);


        //init views
        mSearch = findViewById(R.id.search);
        mUsername = findViewById(R.id.username);
        mSearchBtn = findViewById(R.id.search_button);
        mChatButton = findViewById(R.id.navigate_chat_activity);

        //connect socket
        ChatApplication application = (ChatApplication) getApplication();
        application.initSocket(sharedPreferences.getString(Constant.USER_NAME, "null"));
        mSocket = application.getSocket();
        initSocketEvents();

        //init retrofit
        RetrofitClient retrofitClient = (RetrofitClient) getApplication();
        retrofit = retrofitClient.getRetrofit();

        userApi = retrofit.create(UserApi.class);

        sendFirebaseTokenToServer();

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

    private void sendFirebaseTokenToServer() {

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                Call<Void> call = userApi.setFirebaseToken(token, getSharedPreferences(Constant.PREFS_NAME, MODE_PRIVATE).getString(Constant.TOKEN, ""));
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                            if (!response.isSuccessful()){
                                Log.e(TAG, "onResponse: unsuccessful" + response.message() );
                                return;
                            }
                            Log.d(TAG, "onResponse: Successfully added firebase token : " + token);
                        }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getMessage());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: " + e.getMessage() );
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

        Call<User> call = userApi.user(mSearch.getText().toString(), sharedPreferences.getString(Constant.TOKEN, "null")  );

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
                intent.putExtra("from", sharedPreferences.getString(Constant.USER_NAME, "null")  );
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