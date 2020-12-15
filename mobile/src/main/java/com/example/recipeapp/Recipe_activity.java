package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Recipe_activity extends AppCompatActivity {
    private TextView title, ready_in, servings, instructions, healthy;
    private ImageView img, vegeterian;
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    private JSONArray ingredientsArr;
    private List<Ingredient> ingredientsLst = new ArrayList<Ingredient>();
    private RecyclerView myrv;
    private FloatingActionButton fab;
    private boolean like = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        final Intent intent = getIntent();
        final String recipeId = Objects.requireNonNull(intent.getExtras()).getString("id");
        mAuth = FirebaseAuth.getInstance();
        final String uid = mAuth.getCurrentUser().getUid();
        mRootRef = FirebaseDatabase.getInstance().getReference().child(uid).child(recipeId);
        img = findViewById(R.id.recipe_img);
        title = findViewById(R.id.recipe_title);
        ready_in = findViewById(R.id.recipe_ready_in);
        servings = findViewById(R.id.recipe_servings);
        healthy = findViewById(R.id.recipe_healthy);
        vegeterian = findViewById(R.id.recipe_vegetarian);
        instructions = findViewById(R.id.recipe_instructions);
        fab = findViewById(R.id.floatingActionButton);
        try {
            getRecipeInstructions(recipeId);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mRootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("mRootRef", String.valueOf(dataSnapshot));
                if (dataSnapshot.getValue() != null) {
                    fab.setImageResource(R.drawable.ic_favorite_black_24dp);
                    like = true;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like = !like;
                mRootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (like) {
                            fab.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Map favorites = new HashMap();
                            favorites.put("img", intent.getExtras().getString("img"));
                            favorites.put("title", intent.getExtras().getString("title"));
                            mRootRef.setValue(favorites);
                        } else {
                            try {
                                fab.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                                mRootRef.setValue(null);
                            } catch (Exception e) {
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
        myrv = findViewById(R.id.recipe_ingredients_rv);
        myrv.setLayoutManager(new GridLayoutManager(this, 2));

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

        //  RequestQueue requestQueue = Volley.newRequestQueue(this);
        client.newCall(request).enqueue( new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String myResponse;
                myResponse =response.body().string();
                try {
                    JSONObject results = new JSONObject(myResponse);
                    try {
                        Picasso.get().load((String) results.get("image")).into(img);
                    } catch (Exception e) {
                        img.setImageResource(R.drawable.nopicture);
                    }
                title.setText((String) results.getString("title"));
                ready_in.setText(Integer.toString((Integer) results.get("readyInMinutes")));
                servings.setText(Integer.toString((Integer) results.get("servings")));
                    try{
                        if(results.getString("instructions").equals("")){
                            throw new Exception("No Instructions");
                        }
                        else
                            instructions.setText(Html.fromHtml((String) results.get("instructions")));
                    }
                    catch(Exception e){
                        String msg= "Unfortunately, the recipe you were looking for was not found, to view the original recipe click on the link below:" + "<a href="+results.get("spoonacularSourceUrl")+">"+results.get("spoonacularSourceUrl")+"</a>";
                        instructions.setMovementMethod(LinkMovementMethod.getInstance());
                        instructions.setText(Html.fromHtml(msg));
                    }
                    ingredientsArr = (JSONArray) results.get("extendedIngredients");
                    for (int i = 0; i < ingredientsArr.length(); i++) {
                        JSONObject jsonObject1;
                        jsonObject1 = ingredientsArr.getJSONObject(i);
                        ingredientsLst.add(new Ingredient(jsonObject1.optString("originalString"), jsonObject1.optString("image")));
                    }
                    RecyclerViewAdapterRecipeIngredient myAdapter = new RecyclerViewAdapterRecipeIngredient(getApplicationContext(), ingredientsLst);
                    myrv.setAdapter(myAdapter);
                    myrv.setItemAnimator(new DefaultItemAnimator());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}