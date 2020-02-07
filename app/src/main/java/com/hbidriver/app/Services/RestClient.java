package com.hbidriver.app.Services;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    static Retrofit mInstance;
    public static boolean SHOW_LOG=true;
    private final static String BASE_URL_V2 = "http://www.api.kcrigroup.com/api/v2/";

    public static API getServiceV2(){
        return getClient1(BASE_URL_V2).create(API.class);
    }


    private static synchronized Retrofit getClient1(String baseUrl) {
        if (null == mInstance) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            if (RetrofitClient.SHOW_LOG)
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.retryOnConnectionFailure(true);
            httpClient.readTimeout(5, TimeUnit.MINUTES);
            httpClient.connectTimeout(5, TimeUnit.MINUTES);
            httpClient.addInterceptor(logging);
            httpClient.cache(null);
            mInstance = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return mInstance;
    }
}
