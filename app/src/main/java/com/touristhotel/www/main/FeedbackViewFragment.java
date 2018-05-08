package com.touristhotel.www.main;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FeedbackViewFragment extends Fragment {


    private LinearLayout container;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feedback_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        container=getActivity().findViewById(R.id.layoutContainer);
        getFeedbacks();

    }
    public void getFeedbacks(){
        RequestBody body=new FormBody.Builder()
                .add("feedback","feedback")
                .build();
        LoginActivity.post(LoginActivity.url + "/tourist/feedback.php", body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Error",e.getMessage());
                FeedbackViewFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FeedbackViewFragment.this.getContext(), "Connection to server failed, try again later!", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result=response.body().string();
                Log.d("Feedback",result);
                FeedbackViewFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(result.contains(";")){
                            String[] feedbacks=result.split(";");
                            for(String f:feedbacks){
                                if(f!=null && f.contains(",")){
                                    String[] data=f.split(",");
                                    String message=data[0];
                                    String date=data[1];
                                    String user=data[2];
                                    final String id=data[3];
                                    LayoutInflater inflater = (LayoutInflater)FeedbackViewFragment.this.getActivity().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                                    LinearLayout layout=(LinearLayout)inflater.inflate(R.layout.feedback_row,null);
                                    TextView tvUser=layout.findViewById(R.id.txtUser);
                                    TextView tvFeedback=layout.findViewById(R.id.txtFeedback);
                                    TextView tvDate=layout.findViewById(R.id.txtDate);
                                    Button btnDelete=layout.findViewById(R.id.btnDelete);
                                    tvUser.setText("By User: "+user);
                                    tvFeedback.setText(message);
                                    tvDate.setText("On Date: "+date);
                                    container.addView(layout);
                                    btnDelete.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            delete(id);
                                        }
                                    });

                                }
                            }
                        }
                    }
                });
            }
        });
    }
    public void delete(String id){
        RequestBody body=new FormBody.Builder()
                .add("delete","delete")
                .add("id",id)
                .build();
        LoginActivity.post(LoginActivity.url + "/tourist/feedback.php", body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("ERROR",e.getMessage());
                FeedbackViewFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FeedbackViewFragment.this.getContext(), "Connection to server failed, try again later!", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result=response.body().string();
                Log.d("DELETEFEEDBACK",result);
                FeedbackViewFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FeedbackViewFragment.this.getContext(), "Deleted Succesfully", Toast.LENGTH_LONG).show();
                        container.removeAllViews();
                        getFeedbacks();
                    }
                });
            }
        });
    }
}
