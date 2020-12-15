package com.example.recipeapp;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchResultsActivity extends AppCompatActivity {
    private RecyclerView search_results;
    private JSONArray resultsArr;
    private List<Recipe> lstRecipe = new ArrayList<>();
    String class_tag = "SearchResultsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        // String searchText=getStringFromList(RecyclerViewAdapterIngredient.ingredientsList);
        String searchText=getIntent().getExtras().getString("ingredient_value");
        try {

            Log.i(class_tag,searchText);
            getResults(searchText);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private String getStringFromList(List<String> ingredientsList) {
        StringBuilder result= new StringBuilder(ingredientsList.get(0));
        for (int i=1;i < ingredientsList.size();i++)
        {
            result.append(", ").append(ingredientsList.get(i));
        }
        return result.toString();
    }

    private void getResults(String searchText) throws JSONException, IOException {
        search_results = findViewById(R.id.ingredients_search_result);
        search_results.setLayoutManager(new GridLayoutManager(this, 2));
        String URL = "https://api.spoonacular.com/recipes/findByIngredients?ingredients=" + searchText + "&number=30&instructionsRequired=true&apiKey=e5f41960a96343569669c5435cdc2710";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
                .get()
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "0c62c820-d52c-fa96-eab0-b6829dc11b00")
                .build();
        //Response response = null;



        //String jsonData = response.body().string();

        //final JSONArray Jarray = new JSONArray(jsonData);
        //  RequestQueue requestQueue = Volley.newRequestQueue(this);
        client.newCall(request).enqueue( new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String jsonData = response.body().string();
                            JSONArray resultsArr = new JSONArray(jsonData);
                            // resultsArr = Jarray;
                            Log.i("the res is:", String.valueOf(resultsArr));
                            for (int i = 0; i < resultsArr.length(); i++) {
                                JSONObject jsonObject1;
                                jsonObject1 = resultsArr.getJSONObject(i);
                                lstRecipe.add(new Recipe(jsonObject1.optString("id"), jsonObject1.optString("title"), jsonObject1.optString("image"), 0, 0, 0));
                            }
                            SearchResultsActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    RecyclerViewAdapterSearchResult myAdapter = new RecyclerViewAdapterSearchResult(getApplicationContext(), lstRecipe);
                                    search_results.setAdapter(myAdapter);
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

    }


}