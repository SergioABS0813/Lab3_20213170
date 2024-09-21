package com.example.lab3_20213170;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiUser {

    @POST("auth/login")
    Call<User>LOGIN(
            @Body LoginRequest loginRequest);

    @GET("todos/user/{id}")
    Call<TodoResponse> getTodos(@Path("id") int userId);

    @PUT("todos/{id}")
    Call<TodoResponse> updateTodo(@Path("id") int id, @Body TodoUpdateRequest todoUpdateRequest);
}
