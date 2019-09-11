package com.hbidriver.app.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hbidriver.app.R;
import com.hbidriver.app.Services.RetrofitClient;
import com.hbidriver.app.model.UserFromGetProfileModel;
import com.hbidriver.app.utils.NextActivity;
import com.hbidriver.app.utils.SharedPrefManager;
import com.ipaulpro.afilechooser.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAccount extends AppCompatActivity {

    private Toolbar toolbar;
    private Activity activity=EditAccount.this;
    private EditText edUserName, edLocation;
    private CardView btnTakePhoto;
    private TextView tvTakePhoto;
    private Uri uri_profile_image;
    private Button btnSubmit;
    String userName, location;
    private Drawable camera, check, uncheck;
    private static final int gallery_pick_code=0;
    private static final int camera_pick_code=1;
    private static final int PERMISSION_CODE=1000;
    private MultipartBody.Part image;
    HashMap<String, RequestBody> map;
    private SpotsDialog spotsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        initGUI();
        setData();
    }
    private void initGUI(){
        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Account");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edUserName=findViewById(R.id.edit_account_username);
        edLocation=findViewById(R.id.edit_account_location);
        btnTakePhoto=findViewById(R.id.edit_account_take_photo);
        tvTakePhoto=findViewById(R.id.edit_account_textview_take_photo);
        btnSubmit=findViewById(R.id.edit_account_update);
        camera=getResources().getDrawable(R.drawable.ic_add_a_photo_white_24dp);
        check=getResources().getDrawable(R.drawable.ic_check_circle_green_24dp);
        uncheck=getResources().getDrawable(R.drawable.ic_cancel_black_24dp);
        map=new HashMap<>();
        spotsDialog=new SpotsDialog(activity,R.style.Custom);
    }
    private void setData(){

        edUserName.setText(MainActivity.user.getUsername());
        edLocation.setText(MainActivity.user.getAddress());

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                userName=edUserName.getText().toString();
                location=edLocation.getText().toString();
                if(!userName.equals("") && !location.equals("") && uri_profile_image!=null){

                    RequestBody rbUserId=createPartFromString(String.valueOf(SharedPrefManager.getUserData(activity).getUser_id()));
                    RequestBody rbUserName=createPartFromString(userName);
                    RequestBody rbLocation=createPartFromString(location);
                    image=prepareFilePart("image", uri_profile_image);

                    map.put("user_id",rbUserId);
                    map.put("username",rbUserName);
                    map.put("location",rbLocation);

                    spotsDialog.show();
                    RetrofitClient.getService().updateDriverUser(image,map,"Bearer "+SharedPrefManager.getUserData(activity).getToken()).enqueue(new Callback<UserFromGetProfileModel>() {
                        @Override
                        public void onResponse(Call<UserFromGetProfileModel> call, Response<UserFromGetProfileModel> response) {
                            UserFromGetProfileModel model=response.body();
                            spotsDialog.hide();
//                            Gson gson = new Gson();
//                            String json = gson.toJson(model);
//
//                            SharedPrefManager.setUserData(activity, json);
                            NextActivity.goActivityWithClearTasks(activity,new MainActivity());
                        }

                        @Override
                        public void onFailure(Call<UserFromGetProfileModel> call, Throwable t) {
                            spotsDialog.hide();
                            Toast.makeText(activity,"No internet connection...",Toast.LENGTH_LONG).show();
                        }
                    });
                }else if (!userName.equals("") && !location.equals("") && uri_profile_image==null){
                    spotsDialog.show();
                    RetrofitClient.getService().updateDriverUserNameLocation(SharedPrefManager.getUserData(activity).getUser_id(),userName,location,"Bearer "+SharedPrefManager.getUserData(activity).getToken()).enqueue(new Callback<UserFromGetProfileModel>() {
                        @Override
                        public void onResponse(Call<UserFromGetProfileModel> call, Response<UserFromGetProfileModel> response) {
                            UserFromGetProfileModel model=response.body();
                            spotsDialog.hide();
//                            Gson gson = new Gson();
//                            String json = gson.toJson(model);
//                            SharedPrefManager.setUserData(activity, json);
                            NextActivity.goActivityWithClearTasks(activity,new MainActivity());
                        }

                        @Override
                        public void onFailure(Call<UserFromGetProfileModel> call, Throwable t) {
                            spotsDialog.hide();
                            Toast.makeText(activity,"No internet connection...",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else {
                    if(userName.equals("")){
                        edUserName.setError("username is required");
                    }
                    if(location.equals("")){
                        edLocation.setError("location is required");
                    }
//                    if(uri_profile_image==null){
//                        tvTakePhoto.setCompoundDrawablesWithIntrinsicBounds(camera,null,uncheck,null);
//                    }
                }
            }
        });

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] options={"Take Photo", "Choose from Gallery"};

                AlertDialog.Builder builder=new AlertDialog.Builder(activity);
                builder.setTitle("Choose one to upload image");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (options[i].equals("Take Photo")){

                            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                                if(checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED){
                                    String[] permission={Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                                    requestPermissions(permission,PERMISSION_CODE);
                                }
                                else {
                                    ContentValues values=new ContentValues();
                                    values.put(MediaStore.Images.Media.TITLE,"New Picture");
                                    values.put(MediaStore.Images.Media.DESCRIPTION,"From the Camera");
                                    uri_profile_image=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                                    Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT,uri_profile_image);
                                    startActivityForResult(intent,camera_pick_code);
                                }
                            }
                            else {
                                ContentValues values=new ContentValues();
                                values.put(MediaStore.Images.Media.TITLE,"New Picture");
                                values.put(MediaStore.Images.Media.DESCRIPTION,"From the Camera");
                                uri_profile_image=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT,uri_profile_image);
                                startActivityForResult(intent,camera_pick_code);
                            }

                        }
                        else if(options[i].equals("Choose from Gallery")){
                            pickImageFromGallary(gallery_pick_code);
                        }
                    }
                });
                builder.setPositiveButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });
    }
    private void pickImageFromGallary(int code){
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE:{
                if(grantResults.length>0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                    ContentValues values=new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE,"New Picture");
                    values.put(MediaStore.Images.Media.DESCRIPTION,"From the Camera");
                    uri_profile_image=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,uri_profile_image);
                    startActivityForResult(intent,camera_pick_code);
                }
                else {
                    Toast.makeText(activity,"Permission denied...",Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==RESULT_OK && requestCode==gallery_pick_code){
            uri_profile_image=data.getData();
            saveBitmapToFile(FileUtils.getFile(activity,uri_profile_image));
            tvTakePhoto.setCompoundDrawablesWithIntrinsicBounds(camera,null, check,null);
        }
        else if(resultCode==RESULT_OK && requestCode==camera_pick_code){
            saveBitmapToFile(FileUtils.getFile(activity,uri_profile_image));
            tvTakePhoto.setCompoundDrawablesWithIntrinsicBounds(camera,null, check,null);
        }
    }

    public File saveBitmapToFile(File file){
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            FileInputStream inputStream = new FileInputStream(file);
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();
            final int REQUIRED_SIZE=75;
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(okhttp3.MultipartBody.FORM, descriptionString);
    }
    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        File file= FileUtils.getFile(activity,fileUri);
        RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
