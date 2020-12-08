package com.example.recipeapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;import java.io.IOException;import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;import java.io.IOException;import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.os.Bundle;

public class search extends AppCompatActivity {

    private TextView result;
    String st;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        result = (TextView) findViewById(R.id.result);

        OkHttpClient client = new OkHttpClient();
        st=getIntent().getExtras().getString("ingredient_value");


        String url = "https://api.spoonacular.com/recipes/findByIngredients?apiKey=e5f41960a96343569669c5435cdc2710&number=2&ingredients="+st;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    final String myResponse=response.body().string();

                    search.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            result.setText(myResponse);
                        }
                    });
                }
            }
        });

    };
}