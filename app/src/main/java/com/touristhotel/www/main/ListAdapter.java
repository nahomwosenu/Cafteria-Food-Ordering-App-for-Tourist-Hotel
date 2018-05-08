package com.touristhotel.www.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by geek on 4/20/2018.
 */

public class ListAdapter extends BaseAdapter{
    Context context;
    private final String[] values;
    private final String[] numbers;
    private final String[] images;
    static ArrayList<String> orders;
    static double total;
    static Bitmap bitmap;
    Menu menu;
    public ListAdapter(Context context,String[] values, String[] numbers,String[] images,Menu menu){
        this.context=context;
        this.values=values;//names
        this.numbers=numbers;//price
        this.images=images;//image paths
        this.menu=menu;//menu object
        if(orders==null){
            orders=new ArrayList<>(10);
            total=0.0f;
        }
    }
    public String getPrice(String item){
        int i=0;
        for(String v:values){
            if(v!=null && v.equals(item)){
                return numbers[i];
            }
            i++;
        }
        return null;
    }
    public Bitmap getImage(String path,final ImageView icon){
        RequestBody body=new FormBody.Builder()
                .build();
        bitmap=null;
        LoginActivity.post(LoginActivity.url + "/tourist/" + path, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is=response.body().byteStream();
                bitmap= BitmapFactory.decodeStream(is);
                if(menu!=null) {
                    if(menu.getActivity()!=null) {
                        menu.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                icon.setImageBitmap(bitmap);
                            }
                        });
                    }
                }
                Log.d("TOURIST","Results: "+is.toString());
            }
        });
        return bitmap;
    }
    @Override
    public int getCount(){
        return values.length;
    }
    @Override
    public Object getItem(int i){
        return i;
    }
    @Override
    public long getItemId(int i){
        return i;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        ViewHolder viewHolder;
        final View result;
        if(convertView==null){
            viewHolder=new ViewHolder();
            LayoutInflater inflater=LayoutInflater.from(context);
            convertView=inflater.inflate(R.layout.single_item,parent,false);
            viewHolder.txtName=(TextView)convertView.findViewById(R.id.txtTitle);
            viewHolder.txtPrice=(TextView)convertView.findViewById(R.id.txtBelow);
            viewHolder.icon=(ImageView)convertView.findViewById(R.id.foodIcon);
            viewHolder.btnAdd=(ImageButton)convertView.findViewById(R.id.btnAdd);
            result=convertView;
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
            result=convertView;
        }
        viewHolder.txtName.setText(values[position]);
        viewHolder.txtPrice.setText(numbers[position]);
        getImage(images[position],viewHolder.icon);
        viewHolder.btnAdd.setImageResource(R.drawable.ic_add_circle_black_24dp);
        final int p=position;
        viewHolder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem(values[p]);
            }
        });
        return convertView;
    }
    static double totalCost=0;
    public void addItem(final String name){
        AlertDialog dialog=new AlertDialog.Builder(menu.getActivity())
                .setTitle("Adding New Item")
                .setMessage("Are you sure to add this item? \r\nItem Name: "+name)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        menu.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                orders.add(name);
                                int count=orders.size();
                                menu.txtItems.setText("Total Items: "+count);
                                int index=orders.indexOf(name);
                                totalCost+=Double.parseDouble(numbers[index]);
                                menu.txtTotal.setText("ETB: "+totalCost+"");
                            }
                        });
                    }
                })
                .setNegativeButton("Nop", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
        dialog.show();
    }


    private static class ViewHolder{
        TextView txtName;
        TextView txtPrice;
        ImageView icon;
        ImageButton btnAdd;
    }
}
