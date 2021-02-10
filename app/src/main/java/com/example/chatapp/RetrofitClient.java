package com.example.chatapp;

import android.app.Application;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient extends Application {

    private Retrofit retrofit;
    {
        retrofit = new Retrofit.Builder()
                        .baseUrl("http://10.0.2.2:3000/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
    }


    public Retrofit getRetrofit(){
        return retrofit;
    }

}
