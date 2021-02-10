package com.example.chatapp;

import android.app.Application;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class ChatApplication extends RetrofitClient {
    private static final String TAG = "APPLICATION";
    private Socket mSocket;



    public void initSocket(String username) {
        try {
            IO.Options mOptions = new IO.Options();
            mOptions.query = "username=" + username;
            mSocket = IO.socket("http://192.168.1.104:3000", mOptions);

            mSocket.connect();


        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket(){
        return mSocket;
    }


}
