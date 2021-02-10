package com.example.chatapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{
    Retrofit retrofit;
    EditText mName, mEmail, mUsername, mPassword;
    Button mSignUpBtn;
    TextView mLoginBtn;
    UserApi userApi;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        RetrofitClient retrofitClient = (RetrofitClient) getApplication();
        retrofit = retrofitClient.getRetrofit();
        userApi = retrofit.create(UserApi.class);

        initViews();
        checkToken(); //checks if user is already logged in, if token is available than user is already logged in

        mSignUpBtn.setOnClickListener(this);
        mLoginBtn.setOnClickListener(this);

    }

    private void checkToken() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constant.PREFS_NAME, MODE_PRIVATE);
        ;
        if(!sharedPreferences.getString(Constant.TOKEN, "null").equals("null")){
            goToMainActivity();
        }
    }

    private void goToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class  );
        startActivity(intent);
    }

    private void initViews() {

        mName = (EditText) findViewById(R.id.name);
        mEmail = (EditText) findViewById(R.id.email);
        mUsername = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        mSignUpBtn = (Button) findViewById(R.id.sign_up);
        mLoginBtn = (TextView) findViewById(R.id.login);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.sign_up:
                doSignUp();
                break;
            case R.id.login:
                goToLoginActivity();
                break;

        }

    }

    private void goToLoginActivity() {
    }

    private void doSignUp() {
        if(!validate()){
            return;
        }

        User user = new User();
        user.setName(mName.getText().toString());
        user.setEmail(mEmail.getText().toString());
        user.setPassword(mPassword.getText().toString());
        user.setUsername(mUsername.getText().toString());
        Call<UserAndToken> call = userApi.createUser(user);
        call.enqueue(new Callback<UserAndToken>() {
            @Override
            public void onResponse(Call<UserAndToken> call, Response<UserAndToken> response) {
                if (!response.isSuccessful()){
                    makeToast(response.message());
                    return;
                }

                //save user details in shared prefs
                SharedPreferences.Editor editor = getSharedPreferences(Constant.PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString(Constant.TOKEN, response.body().getToken());
                editor.putString(Constant.USER_NAME, response.body().getUser().getUsername());
                editor.apply();

                makeToast("Signed up Succeessfully");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);



            }

            @Override
            public void onFailure(Call<UserAndToken> call, Throwable t) {
                makeToast(t.getMessage());
            }
        });

    }

    private boolean validate() {
        if (TextUtils.isEmpty(mName.getText())){
            makeToast("Name cannot be empty!");
            return false;
        }
        if (TextUtils.isEmpty(mEmail.getText())){
            makeToast("Email cannot be empty!");
            return false;
        }
        if (TextUtils.isEmpty(mPassword.getText())){
            makeToast("Password cannot be empty!");
            return false;
        }
        if (TextUtils.isEmpty(mUsername.getText())){
            makeToast("Username cannot be empty!");
            return false;
        }
        return true;
    }

    private void makeToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}