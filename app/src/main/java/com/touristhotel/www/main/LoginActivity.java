package com.touristhotel.www.main;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private Button btnLogin;
    private Button btnSignup;
    static OkHttpClient client;
    static String url="http://192.168.137.1";
    EditText txtUsername;
    EditText txtPassword;
    public static String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLogin=(Button)findViewById(R.id.login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login(view);
            }
        });
        btnSignup=(Button)findViewById(R.id.signup);
        txtUsername=findViewById(R.id.txtUser);
        txtPassword=findViewById(R.id.txtPass);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp(view);
            }
        });
        CookieManager cookieManager=new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        client=new OkHttpClient();
        client=new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .build();
    }
    public void Login(View v){
        String username=txtUsername.getText().toString();
        String password=txtPassword.getText().toString();
        LoginActivity.username=username;
        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
            Toast.makeText(this,"Both Fields are required!",Toast.LENGTH_LONG).show();
        }else{
            RequestBody body=new FormBody.Builder()
                    .add("username",username)
                    .add("password",password)
                    .build();
            LoginActivity.post(LoginActivity.url + "/tourist/login.php", body, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog dialog =new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("Connection Failed")
                                    .setIcon(R.drawable.ic_report_black_24dp)
                                    .setMessage("Connection to tourist server failed, connect to our wifi network and try again!")
                                    .create();
                            dialog.show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String r=response.body().string();
                    Log.d("TOURIST","Response:"+r);
                    if(r!=null && !TextUtils.isEmpty(r)){
                        if(r.equals("true")){
                            LoginActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Intent intent=new Intent(LoginActivity.this,UserActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });

                        }
                        else if(r.equals("admin")){
                            LoginActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent=new Intent(LoginActivity.this,AdminActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                        else if(r.equals("false")){
                            LoginActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    vibrate();
                                    Toast.makeText(LoginActivity.this,"Wrong username or password",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                }
            }
        });
    }
    }
    public void vibrate(){
            Vibrator v=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
            if(v.hasVibrator()){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    v.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
                }
            }else{
                v.vibrate(500);
            }
    }
    public void signUp(View v){
        Intent intent=new Intent(this,RegisterActivity.class);
        startActivity(intent);
        finish();
    }
    static Call post(String url, RequestBody body, Callback callback){
        Request request=new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call=client.newCall(request);
        call.enqueue(callback);
        return call;
    }
}
