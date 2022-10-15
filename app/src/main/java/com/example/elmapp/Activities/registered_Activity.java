package com.example.elmapp.Activities;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.elmapp.R;
import com.example.elmapp.TcpClient.Client;
import com.example.elmapp.TcpClient.OneParametersCallable;

import java.net.ConnectException;

public class registered_Activity extends AppCompatActivity implements View.OnClickListener{

    private final String serverIP = "192.168.25.85";

    private final int port = 25601;

    private boolean TCPAvailable = false;

    Button LoginButton;

    EditText id_EditText;
    EditText password_EditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);

        id_EditText = findViewById(R.id.ID_editText);
        password_EditText = findViewById(R.id.password_editText);

        LoginButton = findViewById(R.id.login_button);
        LoginButton.setOnClickListener(this::onClick);


        TCPconnect();


    }

    private void TCPconnect(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Client client = new Client(serverIP,port);
                if(!client.isNull() && client.isConnected()){
                    client.start();
                    TCPAvailable = true;
                    Log.d("TCP","TCP connect success");
                }
                else Log.e("TCP","client 初始化失败");
            }
        }).start();
    }

    //按钮回调函数
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_button:
                Login();
                break;
        }
    }

    //登录回调函数
    private class LoginCb implements OneParametersCallable{

        @Override
        public Object call(byte flag) throws Exception {
            if(flag==1){
                Log.d("Login","Login success");
            }else if(flag==-1){
                Log.e("Login","Login failed");
            }else {
                Log.e("Login","登录超时");
            }
            return null;
        }
    }

    private void Login(){
        String ID = id_EditText.getText().toString();
        String Password = password_EditText.getText().toString();
        Client.Login(ID,Password,new LoginCb());
    }
}