package com.touristhotel.www.main;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by geek on 4/20/2018.
 */

public class Feedback extends Fragment {
    EditText txtFeedback;
    Button btnPost;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.feedback_fragment,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Your Feedback");
        txtFeedback=getActivity().findViewById(R.id.txtFeedback);
        btnPost=getActivity().findViewById(R.id.btnPost);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postFeedback();
            }
        });
    }
    public void postFeedback(){
        if(TextUtils.isEmpty(txtFeedback.getText())){
            Toast.makeText(getActivity(),"Please enter your feedback! ",Toast.LENGTH_LONG).show();
            return;
        }
        String feedback=txtFeedback.getText().toString();
        RequestBody body=new FormBody.Builder()
                .add("username",LoginActivity.username)
                .add("feedback",feedback)
                .add("add","add")
                .build();
        LoginActivity.post(LoginActivity.url + "/tourist/feedback.php", body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Connection",e.getMessage());
                Feedback.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Feedback.this.getActivity(),"No Internet Connection!",Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result=response.body().string();
                Log.d("Response: ",response.);
                Feedback.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(result!=null && result.contains("true")){
                            AlertDialog.Builder builder=new AlertDialog.Builder(Feedback.this.getActivity());
                            builder.setTitle("Your feedback has been sent");
                            builder.setMessage("your feedback to our service has been succesfully posted to administrator online. Tahnas for using our service.");
                            builder.show();
                        }
                        else{
                            AlertDialog.Builder builder=new AlertDialog.Builder(Feedback.this.getActivity());
                            builder.setTitle("Error sending ffedback");
                            builder.setMessage("Your feedback is not sent due to technical difficulties, please try again later!.");
                            builder.show();
                        }
                    }
                });
            }
        });
    }
}
