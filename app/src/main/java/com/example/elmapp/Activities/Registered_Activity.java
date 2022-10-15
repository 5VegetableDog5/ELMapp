package com.example.elmapp.Activities;

import android.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.elmapp.R;
import com.example.elmapp.TcpClient.Client;
import com.example.elmapp.TcpClient.OneParametersCallable;

public class Registered_Activity extends AppCompatActivity implements View.OnClickListener {

    private EditText re_ID_TextEdit;
    private EditText re_pwd_TextEdit;
    private EditText re_checkpwd_TextEdit;

    private Button registeredOk_button;

    private TextView titlebar_back;
    private TextView titlebar_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acttivity_registered);

        //标题栏初始化
        titlebar_back = findViewById(R.id.tv_back);
        titlebar_title = findViewById(R.id.tv_title);
        titlebar_title.setText("注册");
        titlebar_back.setOnClickListener(this);

        //输入框初始化
        re_ID_TextEdit = findViewById(R.id.re_ID_TextEdit);
        re_pwd_TextEdit = findViewById(R.id.re_pwd_TextEdit);
        re_checkpwd_TextEdit = findViewById(R.id.re_checkpwd_TextEdit);

        //按钮初始化
        registeredOk_button = findViewById(R.id.registeredOk_button);
        registeredOk_button.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.registeredOk_button:
                RegisteredUser();
                break;
            case R.id.tv_back:
                finish();
                break;
        }
    }

    private void RegisteredUser(){
        String id = re_ID_TextEdit.getText().toString();
        String password = re_pwd_TextEdit.getText().toString();
        String checkPassword = re_checkpwd_TextEdit.getText().toString();
        if(id==null||id.length()<8){
            new AlertDialog.Builder(Registered_Activity.this).setTitle("错误")
                    .setMessage("账号长度必须大于8").setPositiveButton("确定",null)
                    .create().show();
            Log.e("Registered","账号长度必须大于8");
            return;
        }

        if(password==null||checkPassword==null||password.isEmpty()||checkPassword.isEmpty()){
            new AlertDialog.Builder(Registered_Activity.this).setTitle("错误")
                    .setMessage("密码或确认密码不能为空").setPositiveButton("确定",null)
                    .create().show();
            Log.e("Registered","密码或确认密码不能为空");
            return;
        }
        if(password.length()<8){
            new AlertDialog.Builder(Registered_Activity.this).setTitle("错误")
                    .setMessage("密码长度必须大于8").setPositiveButton("确定",null)
                    .create().show();
            Log.e("Registered","密码长度必须大于8");
        }
        if(!password.equals(checkPassword)){
            new AlertDialog.Builder(Registered_Activity.this).setTitle("错误")
                    .setMessage("密码与确认密码不一致").setPositiveButton("确定",null)
                    .create().show();
            Log.e("Registered","密码与确认密码不一致");
            return;
        }
        Client.registerUser(id,password,new RegisteredCb());
    }

    private class RegisteredCb implements OneParametersCallable{

        @Override
        public Object call(byte flag) throws Exception {
            if(flag==1){
                new AlertDialog.Builder(Registered_Activity.this).setTitle("提示")
                        .setMessage("注册成功").setPositiveButton("确定",null)
                        .create().show();
                Log.d("Registered","注册成功");
            }else if(flag==-1){
                new AlertDialog.Builder(Registered_Activity.this).setTitle("错误")
                        .setMessage("注册失败").setPositiveButton("确定",null)
                        .create().show();
                Log.e("Registered","注册失败");
            }else {
                new AlertDialog.Builder(Registered_Activity.this).setTitle("错误")
                        .setMessage("超时 注册失败").setPositiveButton("确定",null)
                        .create().show();
                Log.e("Registered","超时");
            }
            return null;
        }
    }
}