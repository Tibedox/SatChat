package com.example.satchat;

import com.google.gson.annotations.SerializedName;

public class DataFromDB {
    @SerializedName("id")
    int id;

    @SerializedName("name")
    String name;

    @SerializedName("msg")
    String msg;

    @SerializedName("msgcreated")
    String msgcreated;
}
