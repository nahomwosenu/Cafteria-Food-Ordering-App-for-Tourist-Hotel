package com.touristhotel.www.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activit_register_next);
        final Button signup=findViewById(R.id.btnFinish);
        final EditText txtFullName=findViewById(R.id.txtFullName);
        final EditText txtTableNo=findViewById(R.id.tableNumber);
        final EditText phone=findViewById(R.id.txtPhone);
        final CheckBox cboAgree=findViewById(R.id.cboAgree);
        signup.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String fullname=txtFullName.getText().toString();
                        String table=txtTableNo.getText().toString();
                        String phoneNo=phone.getText().toString();

                        if(TextUtils.isEmpty(fullname) || TextUtils.isEmpty(table) || TextUtils.isEmpty(phoneNo)){
                            Toast.makeText(SignupActivity.this,"Please fill all forms and try again",Toast.LENGTH_LONG).show();
                        }
                        else if(phoneNo.length()!=10){
                            Toast.makeText(SignupActivity.this,"Phone number length should be 10 digits",Toast.LENGTH_LONG).show();
                        }
                        else if(!fullname.contains(" ")){
                            Toast.makeText(SignupActivity.this,"Full Name should include your first & last names",Toast.LENGTH_LONG).show();
                        }
                        else if(!cboAgree.isChecked()){
                            Toast.makeText(SignupActivity.this,"Agree to the terms to proceed!",Toast.LENGTH_LONG).show();
                        }
                        else{
                            String name[]=fullname.split(" ");
                            RegisterActivity.firstName=name[0];
                            RegisterActivity.lastName=name[1];
                            RegisterActivity.phone=phoneNo;
                            RegisterActivity.tableNo=Integer.parseInt(table);
                            RequestBody body=new FormBody.Builder()
                                    .add("customer","true")
                                    .add("firstname",RegisterActivity.firstName)
                                    .add("lastname",RegisterActivity.lastName)
                                    .add("email",RegisterActivity.email)
                                    .add("phone",RegisterActivity.phone)
                                    .add("username",RegisterActivity.username)
                                    .add("password",RegisterActivity.password)
                                    .add("table",String.valueOf(RegisterActivity.tableNo))
                                    .build();
                            LoginActivity.post(LoginActivity.url + "/tourist/register.php", body, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    SignupActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            AlertDialog dialog =new AlertDialog.Builder(SignupActivity.this)
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
                                            SignupActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    AlertDialog dialog=new AlertDialog.Builder(SignupActivity.this)
                                                            .setTitle("Registration Succesfull")
                                                            .setMessage("You are succefully registered on our guest list, please login now to access our services")
                                                            .setPositiveButton("Login Now", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    Intent intent=new Intent(SignupActivity.this,LoginActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            })
                                                            .create();
                                                    dialog.show();
                                                }
                                            });

                                        }
                                        else{
                                            SignupActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(SignupActivity.this,"Registration Failed",Toast.LENGTH_LONG).show();
                                                     }
                                            });

                                        }
                                    }
                                }
                            });

                        }
                    }
                }
        );
    }
}
