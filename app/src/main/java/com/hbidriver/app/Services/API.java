package com.hbidriver.app.Services;

import com.hbidriver.app.model.AdminUser;
import com.hbidriver.app.model.ResponseOnChangePassword;
import com.hbidriver.app.model.ResponseOnUpdateLocation;
import com.hbidriver.app.model.SlidesModel;
import com.hbidriver.app.model.UserFromGetProfileModel;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface API {

    @POST("driver-login")
    Call<AdminUser> logInAdmin(@Query("username") String email, @Query("password") String password);

    @GET("driver-profile")
    Call<UserFromGetProfileModel> getUserProfile(@Query("user_id") int user_id, @Header("Authorization") String token);

    @Multipart
    @POST("driver-change-profile")
    Call<UserFromGetProfileModel> updateDriverUser(@Part MultipartBody.Part image, @PartMap() Map<String, RequestBody> partMap, @Header("Authorization") String token);

    @POST("driver-change-profile")
    Call<UserFromGetProfileModel> updateDriverUserNameLocation(
            @Query("user_id") int user_id,
            @Query("username") String username,
            @Query("location") String location,
            @Header("Authorization") String token
    );

    @POST("driver-change-password")
    Call<ResponseOnChangePassword> changePassword(@Query("user_id") int user_id, @Query("new_password") String password, @Header("Authorization") String token);

    @POST("driver-post-location")
    Call<ResponseOnUpdateLocation> updateLocation(
            @Query("user_id") int user_id,
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("status") int status,
            @Header("Authorization") String token
    );

    @GET("slides")
    Call<SlidesModel> getSlides(@Header("Authorization") String token);

    @GET("driver-profile/{user_id}")
    Call<UserFromGetProfileModel> getDriverProfile(@Path("user_id") int user_id, @Header("Authorization") String token);

}
