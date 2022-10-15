package com.example.elmapp.Activities;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.elmapp.R;
import com.example.elmapp.TcpClient.Client;
import com.example.elmapp.TcpClient.OneParametersCallable;

public class Login_Activity extends AppCompatActivity implements View.OnClickListener{

    private final String serverIP = "192.168.25.85";

    private final int port = 25601;

    Button LoginButton;
    Button RegisteredButton;

    EditText id_EditText;
    EditText password_EditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        id_EditText = findViewById(R.id.ID_editText);
        password_EditText = findViewById(R.id.password_editText);

        //登录按钮初始化
        LoginButton = findViewById(R.id.login_button);
        LoginButton.setOnClickListener(this::onClick);

        //注册按钮初始化
        RegisteredButton = findViewById(R.id.registered_button);
        RegisteredButton.setOnClickListener(this::onClick);

        TCPconnect();


    }

    private void TCPconnect(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Client client = new Client(serverIP,port);
                if(!client.isNull() && client.isConnected()){
                    client.start();
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
            case R.id.registered_button:
                Log.d("Registered","to activity");
                ToRegisteredActivity();
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
        if(Client.isConnected()){
            String ID = id_EditText.getText().toString();
            String Password = password_EditText.getText().toString();
            Client.Login(ID,Password,new LoginCb());
        }else {
            Log.e("Login","网络未连接");
        }

    }

    private void ToRegisteredActivity(){
        Intent intent = new Intent(Login_Activity.this, Registered_Activity.class);
        startActivity(intent);
    }

}