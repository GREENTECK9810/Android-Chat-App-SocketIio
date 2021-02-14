package com.example.chatapp.PushNotification;

import com.example.chatapp.Constant;
import com.example.chatapp.R;
import com.squareup.okhttp.ResponseBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NotificationApi {

    @Headers({"Authorization: key=" + Constant.SERVER_KEY, "Content-Type:application/json"})
    @POST("fcm/send")
    Call<PushNotificationData> pushNotification(@Body PushNotificationData pushNotificationData);

}
