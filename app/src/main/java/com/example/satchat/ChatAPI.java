package com.example.satchat;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ChatAPI {
    @GET("/pifpafchat.php")
    Call<List<DataFromDB>> sendQuery(@Query("q") String s);

    @GET("/pifpafchat.php")
    Call<List<DataFromDB>> sendQuery(@Query("name") String name, @Query("msg") String msg);
}
