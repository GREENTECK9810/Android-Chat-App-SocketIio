package com.example.chatapp.PushNotification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.chatapp.Api.UserApi;
import com.example.chatapp.Constant;
import com.example.chatapp.R;
import com.example.chatapp.RetrofitClient;
import com.example.chatapp.model.User;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirebaseService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseService";
    private static final String CHANNEL_ID = "Channel1";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        sendTokenToAppServer(s);
    }

    private void sendTokenToAppServer(String s) {

        RetrofitClient retrofitClient = (RetrofitClient) getApplication();
        UserApi userApi = retrofitClient.getRetrofit().create(UserApi.class);
        Call call = userApi.setFirebaseToken(s, getSharedPreferences(Constant.PREFS_NAME, MODE_PRIVATE).getString(Constant.TOKEN, "null"));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()){
                    Log.e(TAG, "Sending firebase token failed: " + response.message());
                }

                Log.d(TAG, "Token added successfully");

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "onMessageReceived: notification received");

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        createNotificationChannel(notificationManager);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("message"))
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
