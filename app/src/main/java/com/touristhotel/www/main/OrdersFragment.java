package com.touristhotel.www.main;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import com.evrencoskun.tableview.TableView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;


public class OrdersFragment extends Fragment {
    static TableLayout tblOrders;
    TextView txtItems;
    TextView txtPrice;
    TextView txtCustomer;
    TextView txtDate;
    TextView txtStatus;
    Button btnCancel;
    Button btnApprove;
    static OrdersFragment instance;
    int selectedOrder;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Customer Food Orders");
        tblOrders=getActivity().findViewById(R.id.tblOrders);
        txtItems=getActivity().findViewById(R.id.txtItems);
        txtPrice=getActivity().findViewById(R.id.txtPrice);
        txtCustomer=getActivity().findViewById(R.id.txtCustomer);
        txtDate=getActivity().findViewById(R.id.txtDate);
        txtStatus=getActivity().findViewById(R.id.txtStatus);
        btnCancel=getActivity().findViewById(R.id.btnCancel);
        btnApprove=getActivity().findViewById(R.id.btnApprove);
        instance=this;
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel(String.valueOf(selectedOrder));
            }
        });

    }
    @Override
    public void onStart(){
        super.onStart();
        Order.getOrders(this.getActivity());
    }
    public void cancel(String id){
        RequestBody body=new FormBody.Builder()
                .add("request","delete")
                .add("id",id)
                .build();
        LoginActivity.post(LoginActivity.url + "/tourist/getOrder.php", body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("FAILED",e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                OrdersFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder=new AlertDialog.Builder(OrdersFragment.this.getActivity());
                        builder.setTitle("Order Deleted");
                        builder.setMessage("You canceled one of your customers orders!");
                        builder.show();
                    }
                });
            }
        });
    }
    public void approve(String id){
        RequestBody body=new FormBody.Builder()
                .add("request","approve")
                .add("id",id)
                .build();
        LoginActivity.post(LoginActivity.url + "/tourist/getOrder.php", body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("FAILED",e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                OrdersFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder=new AlertDialog.Builder(OrdersFragment.this.getActivity());
                        builder.setTitle("Order Deleted");
                        builder.setMessage("You canceled one of your customers orders!");
                        builder.show();
                    }
                });
            }
        });
    }
    public void showDetail(int id){
        Order order=null;
        Log.d("ORDERS","SHOWING DETAILS: "+id);
        for(Order o:Order.ordersList){
            if(o!=null && o.id.equals(String.valueOf(id))){
                order=o;
            }
        }
        if(order==null){
            return;
        }
        selectedOrder=id;
        txtItems.setText("Item: "+order.item+" Category: "+order.category);
        txtPrice.setText("Price: "+order.price+" ETB  (complete)");
        txtCustomer.setText("Customer: "+order.username+"");
        txtDate.setText("Date/Time: "+order.date);
        txtStatus.setText("Status: "+order.status);
    }
}
