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
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.media.ExifInterface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hbidriver.app.R;
import com.hbidriver.app.Services.RestClient;
import com.hbidriver.app.Services.RetrofitClient;
import com.hbidriver.app.model.UserFromGetProfileModel;
import com.hbidriver.app.utils.NextActivity;
import com.hbidriver.app.utils.RotateTransformation;
import com.hbidriver.app.utils.SharedPrefManager;
import com.ipaulpro.afilechooser.utils.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private Activity activity = EditAccount.this;
    private EditText edUserName, edLocation;
    private CardView btnTakePhoto;
    private TextView tvTakePhoto;
    private Uri uri_profile_image;
    private Button btnSubmit;
    String userName, location;
    private Drawable camera, check, uncheck;
    private static final int gallery_pick_code = 0;
    private static final int camera_pick_code = 1;
    private static final int PERMISSION_CODE = 1000;
    private MultipartBody.Part image;
    HashMap<String, RequestBody> map;
    private SpotsDialog spotsDialog;
    private File cameraImageFile;
    private ImageView thumnail_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        initGUI();
        setData();
    }

    private void initGUI() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Account");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edUserName = findViewById(R.id.edit_account_username);
        edLocation = findViewById(R.id.edit_account_location);
        btnTakePhoto = findViewById(R.id.edit_account_take_photo);
        tvTakePhoto = findViewById(R.id.edit_account_textview_take_photo);
        btnSubmit = findViewById(R.id.edit_account_update);
        camera = getResources().getDrawable(R.drawable.ic_add_a_photo_white_24dp);
        check = getResources().getDrawable(R.drawable.ic_check_circle_green_24dp);
        uncheck = getResources().getDrawable(R.drawable.ic_cancel_black_24dp);
        map = new HashMap<>();
        spotsDialog = new SpotsDialog(activity, R.style.Custom);
    }

    private void setData() {

        edUserName.setText(MainActivity.user.getData().getUsername());
        edLocation.setText(MainActivity.user.getData().getAddress());

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                userName = edUserName.getText().toString();
                location = edLocation.getText().toString();
                if (!userName.equals("") && !location.equals("") && uri_profile_image != null) {

                    RequestBody rbUserId = createPartFromString(String.valueOf(SharedPrefManager.getUserData(activity).getUser_id()));
                    RequestBody rbUserName = createPartFromString(userName);
                    RequestBody rbLocation = createPartFromString(location);
                    //image = prepareFilePart("image", uri_profile_image);
                    image = prepareFilePart("image", cameraImageFile);

                    map.put("user_id", rbUserId);
                    map.put("username", rbUserName);
                    map.put("location", rbLocation);

                    spotsDialog.show();
                    RestClient.getServiceV2().updateDriverUser(image, map, "Bearer " + SharedPrefManager.getUserData(activity).getToken()).enqueue(new Callback<UserFromGetProfileModel>() {
                        @Override
                        public void onResponse(Call<UserFromGetProfileModel> call, Response<UserFromGetProfileModel> response) {
                            UserFromGetProfileModel model = response.body();
                            spotsDialog.hide();
//                            Gson gson = new Gson();
//                            String json = gson.toJson(model);
//
//                            SharedPrefManager.setUserData(activity, json);
                            NextActivity.goActivityWithClearTasks(activity, new MainActivity());
                        }

                        @Override
                        public void onFailure(Call<UserFromGetProfileModel> call, Throwable t) {
                            spotsDialog.hide();
                            Toast.makeText(activity, "No internet connection...", Toast.LENGTH_LONG).show();
                        }
                    });
                } else if (!userName.equals("") && !location.equals("") && uri_profile_image == null) {
                    spotsDialog.show();
                    RetrofitClient.getService().updateDriverUserNameLocation(SharedPrefManager.getUserData(activity).getUser_id(), userName, location, "Bearer " + SharedPrefManager.getUserData(activity).getToken()).enqueue(new Callback<UserFromGetProfileModel>() {
                        @Override
                        public void onResponse(Call<UserFromGetProfileModel> call, Response<UserFromGetProfileModel> response) {
                            UserFromGetProfileModel model = response.body();
                            spotsDialog.hide();
//                            Gson gson = new Gson();
//                            String json = gson.toJson(model);
//                            SharedPrefManager.setUserData(activity, json);
                            NextActivity.goActivityWithClearTasks(activity, new MainActivity());
                        }

                        @Override
                        public void onFailure(Call<UserFromGetProfileModel> call, Throwable t) {
                            spotsDialog.hide();
                            Toast.makeText(activity, "No internet connection...", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {

                    if (userName.equals("")) {
                        edUserName.setError("username is required");
                    }
                    if (location.equals("")) {
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
                final String[] options = {"Take Photo", "Choose from Gallery"};

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Choose one to upload image");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (options[i].equals("Take Photo")) {

                            //cameraIntent();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                                    String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                                    requestPermissions(permission, PERMISSION_CODE);
                                } else {
                                    ContentValues values = new ContentValues();
                                    values.put(MediaStore.Images.Media.TITLE, "New Picture");
                                    values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
                                    uri_profile_image = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri_profile_image);
                                    startActivityForResult(intent, camera_pick_code);
                                }
                            } else {
                                ContentValues values = new ContentValues();
                                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                                values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
                                uri_profile_image = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri_profile_image);
                                startActivityForResult(intent, camera_pick_code);
                            }

                        } else if (options[i].equals("Choose from Gallery")) {
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

    private void pickImageFromGallary(int code) {
        if (checkPermissions()) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, code);
        }
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions();
            //Toast.makeText(getActivity(), "No Location", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                100);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "New Picture");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
                    uri_profile_image = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri_profile_image);
                    startActivityForResult(intent, camera_pick_code);
                } else {
                    Toast.makeText(activity, "Permission denied...", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 100: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallary(gallery_pick_code);
                }
            }
        }
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, camera_pick_code);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == gallery_pick_code) {
            uri_profile_image = data.getData();
            saveBitmapToFile(FileUtils.getFile(activity, uri_profile_image));
            tvTakePhoto.setCompoundDrawablesWithIntrinsicBounds(camera, null, check, null);
        } else if (resultCode == RESULT_OK && requestCode == camera_pick_code) {
            cameraImageFile = saveBitmapToFile(FileUtils.getFile(activity, uri_profile_image));

            Glide.with(this).load(cameraImageFile).asBitmap()

                    .transform(new RotateTransformation(this, getRotation()))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            try {
                                File tempFile = new File(cameraImageFile.getAbsolutePath());
                                tempFile.delete();
                                cameraImageFile = createFile(resource);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

            tvTakePhoto.setCompoundDrawablesWithIntrinsicBounds(camera, null, check, null);
        }
    }

    private File createFile(Bitmap bitmap) throws IOException {
        File f = new File(getCacheDir(), String.valueOf(System.currentTimeMillis()));
        f.createNewFile();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return f;
    }


    public File saveBitmapToFile(File file) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            FileInputStream inputStream = new FileInputStream(file);
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();
            final int REQUIRED_SIZE = 75;
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }

    private int getRotation() {
        if(Build.MANUFACTURER.toUpperCase().equals("SAMSUNG")) {
            return 90;
        }
        return 0;
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(okhttp3.MultipartBody.FORM, descriptionString);
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        File file = FileUtils.getFile(activity, fileUri);
        RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, File fileUri) {
        //File file = FileUtils.getFile(activity, fileUri);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), fileUri);
        return MultipartBody.Part.createFormData(partName, fileUri.getName(), requestFile);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
