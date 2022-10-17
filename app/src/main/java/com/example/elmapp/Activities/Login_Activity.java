package com.example.elmapp.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.elmapp.R;
import com.example.elmapp.TcpClient.Client;
import com.example.elmapp.TcpClient.OneParametersCallable;
import com.example.elmapp.TcpClient.OneStringCallable;
import com.example.elmapp.tools.mFiles;

import java.io.File;
import java.util.ArrayList;

public class Login_Activity extends AppCompatActivity implements View.OnClickListener{

    private final String serverIP = "192.168.31.120";

    private final int port = 25601;

    private ImageView login_imageview;

    Button LoginButton;
    Button RegisteredButton;

    EditText id_EditText;
    EditText password_EditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //图片初始化


        //输入框初始化
        id_EditText = findViewById(R.id.loginID_editText);
        password_EditText = findViewById(R.id.loginpassword_editText);

        //登录按钮初始化
        LoginButton = findViewById(R.id.login_button);
        LoginButton.setOnClickListener(this);

        //注册按钮初始化
        RegisteredButton = findViewById(R.id.registered_button);
        RegisteredButton.setOnClickListener(this);

        TCPconnect();

        mFiles.showLocalFiles();
        mFiles.deleteAllfiles("data/data/com.example.elmapp/files/Old",true);//成功
        mFiles.rename("data/data/com.example.elmapp/files/New","Old");//成功
        mFiles.careteDir("data/data/com.example.elmapp/files/New");
        mFiles.showLocalFiles();


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
                Test();
                ToShopsActivity();
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
        if(ID==null||ID.isEmpty()||ID.length()<8){
            new AlertDialog.Builder(Login_Activity.this).setTitle("错误")
                    .setMessage("请输入正确的账号").setPositiveButton("确定",null)
                    .create().show();
            Log.e("Login","账号输入错误");
            return;
        }
        if(Password==null||Password.isEmpty()){
            new AlertDialog.Builder(Login_Activity.this).setTitle("错误")
                    .setMessage("请输入正确的密码").setPositiveButton("确定",null)
                    .create().show();
            Log.e("Login","密码输入错误");
            return;
        }
        System.out.println(Password);
        if(Client.isConnected()){
            Client.Login(ID,Password,new LoginCb());
        }else {
            Log.e("Login","网络未连接");
        }

    }

    private void ToRegisteredActivity(){
        Intent intent = new Intent(Login_Activity.this, Registered_Activity.class);
        startActivity(intent);
    }

    private void Test(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Client.GetFile("src/Resourse/Test.txt", new OneStringCallable() {
                    @Override
                    public Object call(ArrayList BannerJSONS) throws Exception {
                        if(BannerJSONS!=null){

                        }
                        return null;
                    }
                });
            }
        }).start();
    }

    private void ToShopsActivity(){
        Intent intent = new Intent(Login_Activity.this,Shops_Activity.class);
        startActivity(intent);
        finish();
    }

}