package com.example.lab3_20213170;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiUser {

    @POST("auth/login")
    Call<User>LOGIN(
            @Body LoginRequest loginRequest);
}
