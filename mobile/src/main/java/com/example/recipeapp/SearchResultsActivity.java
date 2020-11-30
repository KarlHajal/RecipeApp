package com.example.recipeapp;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {
    private RecyclerView search_results;
    private JSONArray resultsArr;
    private List<Recipe> lstRecipe = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        String searchText=getStringFromList(RecyclerViewAdapterIngredient.ingredientsList);
        getResults(searchText);
    }

    private void getResults(String searchText) {
        search_results = findViewById(R.id.ingredients_search_result);
        search_results.setLayoutManager(new GridLayoutManager(this, 2));
        String URL = "https://api.spoonacular.com/recipes/findByIngredients?ingredients="+searchText+"&number=30&instructionsRequired=true&apiKey=c957b6816ba048139fbc25a67d2cff33";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            resultsArr = response;
                            Log.i("the res is:", String.valueOf(resultsArr));
                            for (int i = 0; i < resultsArr.length(); i++) {
                                JSONObject jsonObject1;
                                jsonObject1 = resultsArr.getJSONObject(i);
                                lstRecipe.add(new Recipe(jsonObject1.optString("id"),jsonObject1.optString("title"),jsonObject1.optString("image"), 0, 0, 0));
                            }

                            RecyclerViewAdapterSearchResult myAdapter = new RecyclerViewAdapterSearchResult(getApplicationContext(), lstRecipe);
                            search_results.setAdapter(myAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("the res is error:", error.toString());
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    private String getStringFromList(List<String> ingredientsList) {
        StringBuilder result= new StringBuilder(ingredientsList.get(0));
        for (int i=1;i < ingredientsList.size();i++)
        {
            result.append(", ").append(ingredientsList.get(i));
        }
        return result.toString();
    }


}