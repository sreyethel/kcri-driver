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
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
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
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAccount extends AppCompatActivity {

    private static final String TAG = "EditAccount";
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
    private Bitmap afterRotateImage;

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
                    spotsDialog.show();
                    RequestBody rbUserId = createPartFromString(String.valueOf(SharedPrefManager.getUserData(activity).getUser_id()));
                    RequestBody rbUserName = createPartFromString(userName);
                    RequestBody rbLocation = createPartFromString(location);
                    //image = prepareFilePart("image", uri_profile_image);
                    image = prepareFilePart("image", cameraImageFile);

                    map.put("user_id", rbUserId);
                    map.put("username", rbUserName);
                    map.put("location", rbLocation);

                    RestClient.getServiceV2().updateDriverUser(image, map, "Bearer " + SharedPrefManager.getUserData(activity).getToken()).enqueue(new Callback<UserFromGetProfileModel>() {
                        @Override
                        public void onResponse(Call<UserFromGetProfileModel> call, Response<UserFromGetProfileModel> response) {
                            UserFromGetProfileModel model = response.body();
                            spotsDialog.hide();
//                            Gson gson = new Gson();
//                            String json = gson.toJson(model);
//
//                            SharedPrefManager.setUserData(activity, json);
                            if (cameraImageFile.exists()) {
                                cameraImageFile.delete();
                            }
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
                            if (cameraImageFile.exists()) {
                                cameraImageFile.delete();
                            }
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
            cameraImageFile = saveBitmapToFile(FileUtils.getFile(activity, uri_profile_image));
            tvTakePhoto.setCompoundDrawablesWithIntrinsicBounds(camera, null, check, null);
        } else if (resultCode == RESULT_OK && requestCode == camera_pick_code) {
            //cameraImageFile = saveBitmapToFile(activity,FileUtils.getFile( uri_profile_image));
            //File tempFile = new File(cameraImageFile.getAbsolutePath());
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(EditAccount.this.getContentResolver(), uri_profile_image);
                        afterRotateImage = flipImage(bitmap);
                        cameraImageFile = saveBitmapToFile(storeImage(afterRotateImage));
                        tvTakePhoto.setCompoundDrawablesWithIntrinsicBounds(camera, null, check, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    private File storeImage(Bitmap image) {

        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm", Locale.getDefault()).format(new Date());
        String mImageName = "MI_" + timeStamp + ".png";

        File f = new File(Environment.getExternalStorageDirectory()
                .toString() + "/" + mImageName);
        try {
            f.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            if (fOut != null) {
                fOut.flush();
                fOut.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName = "MI_" + timeStamp + ".png";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
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

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }

    private int getRotation() {
        if (Build.MANUFACTURER.toUpperCase().equals("SAMSUNG")) {
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

    public static Bitmap flipImage(Bitmap bitmap) {
        //Moustafa: fix issue of image reflection due to front camera settings
        Matrix matrix = new Matrix();
        int rotation = fixOrientation(bitmap);
        matrix.postRotate(rotation);
        //matrix.preScale(-1, 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private static int fixOrientation(Bitmap bitmap) {
        if (bitmap.getWidth() > bitmap.getHeight()) {
            return 90;
        }
        return 0;
    }
}
