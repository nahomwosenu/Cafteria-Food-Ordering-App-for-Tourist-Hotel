package com.touristhotel.www.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    static String firstName;
    static String lastName;
    static String email;
    static String password;
    static String username;
    static String phone;
    static int tableNo;
    boolean validated=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button btnNext=findViewById(R.id.btnNext);
        final EditText txtUsername=findViewById(R.id.txtUsername);
        final EditText txtEmail=findViewById(R.id.txtEmail);
        final EditText txtPassword=findViewById(R.id.txtPasssword);
        final EditText txtPassword2=findViewById(R.id.txtPasssword2);
        btnNext.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        username=txtUsername.getText().toString();
                        email=txtEmail.getText().toString();
                        password=txtPassword.getText().toString();
                        String password2=txtPassword2.getText().toString();
                        if(TextUtils.isEmpty(username)){
                            Toast.makeText(RegisterActivity.this,"Username is required",Toast.LENGTH_LONG).show();
                        }
                        else if(TextUtils.isEmpty(email)){
                            Toast.makeText(RegisterActivity.this,"Email is required",Toast.LENGTH_LONG).show();
                        }
                        else if(TextUtils.isEmpty(password)){
                            Toast.makeText(RegisterActivity.this,"Password is required",Toast.LENGTH_LONG).show();
                        }
                        else if(TextUtils.isEmpty(password2)){
                            Toast.makeText(RegisterActivity.this,"Please confirm the password",Toast.LENGTH_LONG).show();
                        }
                        else if(!password.contentEquals(password2)){
                            Toast.makeText(RegisterActivity.this,"Confirmed password do not match the original",Toast.LENGTH_LONG).show();
                        }
                        else if(!email.contains("@") || !email.contains(".")){
                            Toast.makeText(RegisterActivity.this,"Please enter a valid email address",Toast.LENGTH_LONG).show();
                        }
                        else {
                            Intent intent = new Intent(RegisterActivity.this, SignupActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
        );

        txtPassword2.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        String password=txtPassword.getText().toString();
                        String confirm=txtPassword2.getText().toString();
                        if(!password.contentEquals(confirm)){
                            Toast.makeText(RegisterActivity.this,"Passwords do not match",Toast.LENGTH_LONG).show();
                            validated=false;
                        }else{
                            validated=true;
                        }
                        return true;
                    }
                }
        );
    }
}
