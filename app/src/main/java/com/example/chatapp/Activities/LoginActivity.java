package com.example.chatapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chatapp.Api.UserApi;
import com.example.chatapp.Constant;
import com.example.chatapp.R;
import com.example.chatapp.RetrofitClient;
import com.example.chatapp.model.User;
import com.example.chatapp.model.UserAndToken;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "LOGIN ACTIVITY";
    EditText  mEmail, mPassword;
    private UserApi userApi;
    Button mLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        initViews();

        RetrofitClient retrofitClient = (RetrofitClient) getApplication();
        Retrofit retrofit = retrofitClient.getRetrofit();
        userApi = retrofit.create(UserApi.class);

    }

    private void login() {
        User user = new User();
        user.setEmail(mEmail.getText().toString());
        user.setPassword(mPassword.getText().toString());

        Call<UserAndToken> call = userApi.userLogin(user);

        call.enqueue(new Callback<UserAndToken>() {
            @Override
            public void onResponse(Call<UserAndToken> call, Response<UserAndToken> response) {
                if(!response.isSuccessful()){
                    Log.e(TAG, "onResponse: " + response.message());
                    return;
                }

                SharedPreferences.Editor editor = getSharedPreferences(Constant.PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString(Constant.TOKEN, response.body().getToken());
                editor.putString(Constant.USER_NAME, response.body().getUser().getUsername());
                editor.apply();

                Log.d(TAG, "onResponse: " + response.body().getToken());
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("username", response.body().getUser().getUsername());
                intent.putExtra("token", response.body().getToken());

                startActivity(intent);
            }

            @Override
            public void onFailure(Call<UserAndToken> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT);
            }
        });


    }

    private void initViews() {

        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mLogin = (Button) findViewById(R.id.login);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                login();
                break;
        }
    }
}