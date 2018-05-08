package com.touristhotel.www.main;


import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddItemFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_item, container, false);
    }
    private EditText txtItem;
    private EditText txtPrice;
    private EditText txtDescription;
    private Spinner spinMain;
    private Spinner spinSub;
    private Button btnCamera;
    private Button btnCancel;
    private Button btnSubmit;
    private ImageView imgItem;
    private Bitmap profileImage;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtItem=getActivity().findViewById(R.id.txtItem);
        txtPrice=getActivity().findViewById(R.id.txtPrice);
        txtDescription=getActivity().findViewById(R.id.txtDescription);
        spinMain=getActivity().findViewById(R.id.spinMain);
        spinSub=getActivity().findViewById(R.id.spinSub);
        btnCamera=getActivity().findViewById(R.id.btnImage);
        btnCancel=getActivity().findViewById(R.id.btnCancel);
        btnSubmit=getActivity().findViewById(R.id.btnSubmit);
        imgItem=getActivity().findViewById(R.id.imgItem);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(txtItem.getText())){
                    Toast.makeText(AddItemFragment.this.getContext(),"Please enter the name of the item first!",Toast.LENGTH_LONG).show();
                }else
                capture();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
    }
    public void submit(){
        String item=txtItem.getText().toString();
        String price=txtPrice.getText().toString();
        String description=txtDescription.getText().toString();
        String image="foods/"+item+".png";
        String main=spinMain.getSelectedItem().toString();
        String sub=spinSub.getSelectedItem().toString();
        RequestBody body=new FormBody.Builder()
                .add("item",item)
                .add("price",price)
                .add("description",description)
                .add("main",main)
                .add("sub",sub)
                .add("image",image)
                .build();
        LoginActivity.post(LoginActivity.url + "/tourist/addmenu.php", body, new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                AddItemFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                            AlertDialog.Builder builder=new AlertDialog.Builder(AddItemFragment.this.getContext());
                            builder.setTitle("Error");
                            builder.setMessage("Connection to tourist server has broken "+e.getMessage());
                            builder.show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result=response.body().string();
                Log.d("SUBMIT",result);
                AddItemFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(result!=null && result.contains("true")){
                            AlertDialog.Builder builder=new AlertDialog.Builder(AddItemFragment.this.getContext());
                            builder.setTitle("Item Added Succesfully");
                            builder.setMessage("You have added new item succesfully");
                            builder.show();
                        }
                        else{
                            AlertDialog.Builder builder=new AlertDialog.Builder(AddItemFragment.this.getContext());
                            builder.setTitle("Error");
                            builder.setMessage("Sorry, an error occured!");
                            builder.show();
                        }
                    }
                });
            }
        });
    }
    public static final int CAMERA_PIC_REQUEST=1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    public void capture(){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == CAMERA_PIC_REQUEST) {
                if (resultCode == getActivity().RESULT_OK) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    imgItem.setImageBitmap(photo);
                    this.profileImage=photo;
                    if(this.profileImage==null){
                        Log.d("Result","Success but image is null");
                    }
                    else {
                        Log.d("Result", "Success");
                        try{
                            File file=convertBitmap(this.profileImage,"tempname");
                            upload(LoginActivity.url+"/tourist/upload.php",file);
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                }else{
                    Log.d("Result","Failer");
                }
            }
            else{
                Log.d("Result","Something else happened");
            }
        }
    }
    public void upload(String url, File file) throws IOException {
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse("text/plain"), file))
                .addFormDataPart("item", txtItem.getText().toString())
                .build();
        LoginActivity.post(LoginActivity.url+"/tourist/upload.php", formBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Error","Connection error");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("UPLOAD","Response: "+response.body().string());
            }
        });
    }
    public File convertBitmap(Bitmap bitmap,String filename) throws Exception{
        File f = new File(this.getActivity().getCacheDir(), filename);
        f.createNewFile();

//Convert bitmap to byte array

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();
        return f;
    }
}
