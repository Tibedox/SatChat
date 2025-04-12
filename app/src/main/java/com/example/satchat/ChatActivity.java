package com.example.satchat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatActivity extends AppCompatActivity {
    ListView listView;
    EditText editText;
    String name;
    List<DataFromDB> db = new ArrayList<>();
    List<String> messages = new ArrayList<>();
    private Handler handler;
    private Runnable periodicTask;
    private int numMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main2), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        name = getIntent().getStringExtra("KEY_NAME");
        listView = findViewById(R.id.listView);
        editText = findViewById(R.id.editMessage);

        handler = new Handler(Looper.getMainLooper());
        periodicTask = new Runnable() {
            @Override
            public void run() {
                loadFromInternetDB();
                updateList();
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(periodicTask);
    }

    public void sendMessage(View view) {
        if(editText.getText().toString().isEmpty()) return;
        sendToInternetDB(editText.getText().toString());
    }

    public void loadFromInternetDB(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://sch120.ru")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ChatAPI api = retrofit.create(ChatAPI.class);
        Call<List<DataFromDB>> call = api.sendQuery("ask");

        call.enqueue(new Callback<List<DataFromDB>>() {
            @Override
            public void onResponse(Call<List<DataFromDB>> call, Response<List<DataFromDB>> response) {
                db = response.body();
            }

            @Override
            public void onFailure(Call<List<DataFromDB>> call, Throwable t) {

            }
        });
    }

    public void sendToInternetDB(String message){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://sch120.ru")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ChatAPI api = retrofit.create(ChatAPI.class);
        Call<List<DataFromDB>> call = api.sendQuery(name, message);

        call.enqueue(new Callback<List<DataFromDB>>() {
            @Override
            public void onResponse(Call<List<DataFromDB>> call, Response<List<DataFromDB>> response) {
                db = response.body();
            }

            @Override
            public void onFailure(Call<List<DataFromDB>> call, Throwable t) {

            }
        });
    }

    void updateList(){
        if(numMessages<db.size()){
            messages.clear();
            for(DataFromDB a:db) messages.add(a.name+"   "+a.msgcreated+"\n"+a.msg);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, messages);
            listView.setAdapter(adapter);
            scrollDown();
            numMessages=db.size();
        }
    }

    void scrollDown(){
        int itemCount = listView.getAdapter().getCount();
        if (itemCount > 0) {
            listView.setSelection(itemCount - 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && periodicTask != null) {
            handler.removeCallbacks(periodicTask);
        }
    }
}