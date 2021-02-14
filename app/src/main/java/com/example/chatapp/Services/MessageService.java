package com.example.chatapp.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.chatapp.ChatApplication;
import com.example.chatapp.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageService extends Service {

    private static final String TAG = "MESSAGE SERVICE";
    private Socket mSocket;
    public static final String CHANNEL_ID = "Channel2";
    private boolean isInChatActivity =false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: message service created");

        ChatApplication chatApplication = (ChatApplication) getApplication();
        mSocket = chatApplication.getSocket();
        mSocket.on("private message", onPrivateMessage);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand: message service started");
        isInChatActivity = intent.getBooleanExtra("status", false);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Message service destroyed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Emitter.Listener onPrivateMessage = new Emitter.Listener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void call(Object... args) {

            if (isInChatActivity){
                return;
            }

            JSONObject data = (JSONObject) args[0];
            String message = "";
            String username = "";

            try {
                message = data.getString("message");
                username = data.getString("username");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            showNotification(message, username);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showNotification(String message, String username) {

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        createNotificationChannel(notificationManager);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(username)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_baseline_chat_24)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        notificationManager.notify(1, notification);

    }

    private void createNotificationChannel(NotificationManager notificationManager) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "channelname", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Channel description");

            notificationManager.createNotificationChannel(notificationChannel);
        }

    }

}
