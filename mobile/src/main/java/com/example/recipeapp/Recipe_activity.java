package com.example.recipeapp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Recipe_activity extends AppCompatActivity {
    private TextView title, ready_in, servings, instructions;
    private ImageView img, vegeterian;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
    }

    private void getRecipeInstructions(final String recipeId) throws IOException, JSONException {
        String URL = " https://api.spoonacular.com/recipes/" + recipeId + "/information?apiKey=e5f41960a96343569669c5435cdc2710";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
                .get()
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "0c62c820-d52c-fa96-eab0-b6829dc11b00")
                .build();
        Response response = null;
        String jsonData = response.body().string();

        final JSONArray Jarray = new JSONArray(jsonData);
        //  RequestQueue requestQueue = Volley.newRequestQueue(this);
        client.newCall(request).enqueue( new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    try {
                        Picasso.get().load((String) response.get("image")).into(img);
                    } catch (Exception e) {
                        img.setImageResource(R.drawable.nopicture);
                    }
                }
            }
        });
    }
}