package com.touristhotel.www.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by geek on 4/20/2018.
 */

public class Menu extends Fragment {
    int[] images={R.drawable.ic_account_circle_black_24dp,R.drawable.ic_chat_black_24dp,R.drawable.ic_email_black_24dp};
    String[] price={"ETB 25.00","ETB 50.00","ETB 30.00"};
    String[] names={"Cacke","Burger","Dominos Piza"};
    ListView listView;
    ListAdapter listAdapter;
    TextView txtItems;
    TextView txtTotal;
    Button btnOrder;
    Spinner spinner;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.food_menu,container,false);
    }
    @Override
    public void onStart(){
        super.onStart();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item=spinner.getSelectedItem().toString();
                Log.d("SELECTED",item);
                filter(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Tourist Hotel Menu");
        listView=getActivity().findViewById(R.id.listView);
        txtItems=getActivity().findViewById(R.id.txtItems);
        txtTotal=getActivity().findViewById(R.id.txtTotal);
        btnOrder=getActivity().findViewById(R.id.btnOrder);
        spinner=getActivity().findViewById(R.id.spinFood);
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog=new AlertDialog.Builder(Menu.this.getActivity());
                dialog.setTitle("Confirm Checkout");
                String items="";
                for(String item: ListAdapter.orders){
                    items+=item+" , ";
                }
                dialog.setMessage("Are you sure to order these food items? \r\n"+items);
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final Order[] order=new Order[ListAdapter.orders.size()];
                        int j=0;
                        for(String item:ListAdapter.orders){
                            order[j]=new Order();
                            order[j].item=item;
                            order[j].price=listAdapter.getPrice(item);
                            order[j].username=LoginActivity.username;
                        }
                        AlertDialog.Builder builder=new AlertDialog.Builder(Menu.this.getActivity());
                        builder.setTitle("Payment Checkout");

                        final EditText txtAccount=new EditText(builder.getContext());
                        txtAccount.setHint("Bank Account Number");
                        txtAccount.setInputType(InputType.TYPE_CLASS_NUMBER);
                        builder.setMessage("Enter your bank account details to complete the payment");
                        builder.setView(txtAccount);
                        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String account=txtAccount.getText().toString();
                                if(TextUtils.isEmpty(account)){
                                    Toast.makeText(Menu.this.getActivity(),"Invalid Account Number ",Toast.LENGTH_LONG).show();
                                }
                                processPayment(order,account);
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        builder.show();
                    }
                });
                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog.Builder dialog=new AlertDialog.Builder(Menu.this.getActivity());
                        dialog.setTitle("Order Canceled");
                        dialog.setMessage("You just canceled your order and we will take you back to the menu now.");
                        dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        dialog.show();
                    }
                });
                dialog.show();
            }
        });
        fetchAll();
    }
    public boolean processPayment(Order[] orders,String account){
        String items="";
        String username=LoginActivity.username;
        for(Order o:orders){
            items+=o.item+",";
        }
        RequestBody body=new FormBody.Builder()
                .add("username",username)
                .add("items",items)
                .add("account",account)
                .build();
        LoginActivity.post(LoginActivity.url + "/tourist/setorder.php", body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Menu.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder dialog=new AlertDialog.Builder(Menu.this.getActivity());
                        dialog.setTitle("Order Canceled");
                        dialog.setMessage("Connection . \r\n Thank you for using our service. \r\nArbaminch Tourist Hotels.");
                        dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        dialog.show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result=response.body().toString();
                if(result.contains("true")){
                    Menu.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder dialog=new AlertDialog.Builder(Menu.this.getActivity());
                            dialog.setTitle("Order Submitted");
                            dialog.setMessage("Your order is submitted successfully, we will get back to you after a momment. \r\n Thank you for using our service. \r\nArbaminch Tourist Hotels.");
                            dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            dialog.show();
                        }
                    });
                }
                else if(result.contains("LOW_BALANCE")){
                    Menu.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder dialog=new AlertDialog.Builder(Menu.this.getActivity());
                            dialog.setTitle("Order Canceled");
                            dialog.setMessage("You don't have enough balance to order those items. \r\n Thank you for using our service. \r\nArbaminch Tourist Hotels.");
                            dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            dialog.show();
                        }
                    });
                }else{
                    Menu.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder dialog=new AlertDialog.Builder(Menu.this.getActivity());
                            dialog.setTitle("Order Canceled");
                            dialog.setMessage("Sorry, An error occured processing your request, please try again later!. \r\n Thank you for using our service. \r\nArbaminch Tourist Hotels.");
                            dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            dialog.show();
                        }
                    });
                }
            }
        });
        return true;
    }
    public void filter(String foodType){
        RequestBody body=new FormBody.Builder()
                .add("request","all")
                .add("filter",foodType)
                .build();
        LoginActivity.post(LoginActivity.url + "/tourist/getmenu.php", body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Menu.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Menu.this.getContext(),"Error: connection to server failed!",Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body=response.body().string();
                Log.d("TOURIST","Result: "+body);
                if(!TextUtils.isEmpty(body) && body.contains("//")){
                    String[] main=body.split("//");
                    final String[] names=main[0].split(";");
                    final String[] prices=main[1].split(";");
                    final String[] images=main[2].split(";");
                    Menu.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Menu.this.listAdapter=new ListAdapter(Menu.this.getActivity(),names,prices,images,Menu.this);
                            Menu.this.listView.setAdapter(listAdapter);
                        }
                    });

                }
                else{
                    Menu.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Menu.this.listView.setAdapter(null);
                            Toast.makeText(Menu.this.getContext(),"No Results Found!",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
    public void fetchAll(){
        RequestBody body=new FormBody.Builder()
                .add("request","all")
                .build();
        LoginActivity.post(LoginActivity.url + "/tourist/getmenu.php", body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body=response.body().string();
                Log.d("TOURIST","Result: "+body);
                if(!TextUtils.isEmpty(body) && body.contains("//")){
                    String[] main=body.split("//");
                        final String[] names=main[0].split(";");
                        final String[] prices=main[1].split(";");
                        final String[] images=main[2].split(";");
                    Menu.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Menu.this.listAdapter=new ListAdapter(Menu.this.getActivity(),names,prices,images,Menu.this);
                            Menu.this.listView.setAdapter(listAdapter);
                        }
                    });

                }
            }
        });
    }
}
