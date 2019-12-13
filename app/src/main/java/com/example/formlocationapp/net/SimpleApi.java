package com.example.formlocationapp.net;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface SimpleApi {
    @GET(".")
    Call<String> get(@Query("key") String key, @Query("value") String value);

    @POST(".")
    Call<String> post(@Query("key") String key, @Query("value") String value);


    @PUT(".")
    Call<String> put(@Query("key") String key, @Query("value") String value);


    @DELETE(".")
    Call<String> delete(@Query("key") String key, @Query("value") String value);
}
