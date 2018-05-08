package com.touristhotel.www.main;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Order {
    String item;
    String price;
    String id;
    String username;
    String date;
    String status;
    String category;
    static ArrayList<Order> ordersList;
    public static void getOrders(final Activity activity){
        ordersList=new ArrayList<>(100);
        RequestBody body=new FormBody.Builder()
                .add("request","all")
                .build();
        LoginActivity.post(LoginActivity.url + "/tourist/getOrder.php", body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("ERROR",e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String r=response.body().string();
                Log.d("RESPONSE",r);
                String result=r;
                if(result.contains(";")){
                    final String[] orders=result.split(";");
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for(int i=0;i<orders.length;i++){
                                if(orders[i]!=null && orders[i].contains(",")){
                                    String[] order=orders[i].split(",");
                                    Order o=new Order();
                                    o.id=order[0];
                                    o.username=order[2];
                                    o.status=order[3];
                                    o.date=order[4];
                                    o.item=order[5];
                                    o.price=order[6];
                                    o.category=order[7];
                                    ordersList.add(o);
                                    LayoutInflater inflater = (LayoutInflater)activity.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                                    final TableRow row =(TableRow)inflater.inflate(R.layout.tablerow, null );
                                    TextView tv=row.findViewById(R.id.txtNo);
                                    TextView tvUser=row.findViewById(R.id.txtOrder);
                                    TextView tvItem=row.findViewById(R.id.txtItem);
                                    tv.setText(""+(i+1));
                                    tvUser.setText(o.username);
                                    tvItem.setText(o.item);
                                    row.setId(Integer.parseInt(o.id));
                                    OrdersFragment.tblOrders.addView(row);
                                    row.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Log.d("ROW","Row clicked id: "+row.getId());
                                            OrdersFragment.instance.showDetail(row.getId());
                                        }
                                    });
                                }
                            }
                        }
                    });

                }
            }
        });
    }

}
