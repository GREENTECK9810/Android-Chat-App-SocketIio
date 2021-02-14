package com.example.chatapp.Api;

import com.example.chatapp.model.User;
import com.example.chatapp.model.UserAndToken;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApi {

    @GET("users/user")
    Call<User> user(@Query("username") String username, @Header("Authorization") String token);

    @POST("users/login")
    Call<UserAndToken> userLogin(@Body User user);

    @POST("users")
    Call<UserAndToken> createUser(@Body User user);

    @GET("users/firebasetoken")
    Call<Void> setFirebaseToken(@Query("firebaseToken") String firebaseToken, @Header("Authorization") String token);
}
