package com.hbidriver.app.Services;

import android.support.v7.widget.CardView;

import com.hbidriver.app.model.AdminUser;
import com.hbidriver.app.model.ResponseOnChangePassword;
import com.hbidriver.app.model.UserModel;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface API {

    @POST("login")
    Call<AdminUser> logInAdmin(@Query("email") String email, @Query("password") String password);

    @POST("driver-login")
    Call<UserModel> logInDriver(@Header("Authorization") String token, @Query("email") String email, @Query("password") String password);

    @Multipart
    @POST("driver-change-profile")
    Call<UserModel> updateDriverUser(@Part MultipartBody.Part image, @PartMap() Map<String, RequestBody> partMap, @Header("Authorization") String token);

    @POST("driver-change-password")
    Call<ResponseOnChangePassword> changePassword(@Query("user_id") int user_id, @Query("password") String password, @Header("Authorization") String token);

}
