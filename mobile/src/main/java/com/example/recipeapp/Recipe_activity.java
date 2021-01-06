package com.example.recipeapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Recipe_activity extends AppCompatActivity {

    private static final String TAG = "Recipe_activity";
    
    private TextView title, ready_in, servings, instructions, healthy;
    private ImageView img, vegeterian;
//    private DatabaseReference mRootRef;
//    private FirebaseAuth mAuth;
    private JSONArray ingredientsArr;
    private List<Ingredient> ingredientsLst = new ArrayList<Ingredient>();
    private RecyclerView myrv;
    private FloatingActionButton fab;
    private FloatingActionButton useontab;
    private FloatingActionButton sendtowatch;

    private boolean like = false;
    private int RecipeAlarmTime;

    AlarmManager myAlarmManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        myAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Log.i(TAG, " : OnCreate - starting");

        final Intent intent = getIntent();
        final String recipeId = Objects.requireNonNull(intent.getExtras()).getString("id");
//        mAuth = FirebaseAuth.getInstance();
//        final String uid = mAuth.getCurrentUser().getUid();
//        mRootRef = FirebaseDatabase.getInstance().getReference().child(uid).child(recipeId);
        img = findViewById(R.id.recipe_img);
        title = findViewById(R.id.recipe_title);
        ready_in = findViewById(R.id.recipe_ready_in);
        servings = findViewById(R.id.recipe_servings);
        healthy = findViewById(R.id.recipe_healthy);
        vegeterian = findViewById(R.id.recipe_vegetarian);
        instructions = findViewById(R.id.recipe_instructions);
        fab = findViewById(R.id.floatingActionButton);
        useontab = findViewById(R.id.fab_useontab);
        sendtowatch = findViewById(R.id.fab_sendtowatch);


        Log.i(TAG, "OnCreate - try getRecipeInstructions");
        try {
            getRecipeInstructions(recipeId);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

//        Log.i(TAG, "OnCreate - add listeners to firebase");
//        mRootRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.i("mRootRef", String.valueOf(dataSnapshot));
//                if (dataSnapshot.getValue() != null) {
//                    fab.setImageResource(R.drawable.ic_favorite_black_24dp);
//                    like = true;
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        Log.i(TAG, "OnCreate - add listeners to fab");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like = !like;
//                mRootRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (like) {
//                            fab.setImageResource(R.drawable.ic_favorite_black_24dp);
//                            Map favorites = new HashMap();
//                            favorites.put("img", intent.getExtras().getString("img"));
//                            favorites.put("title", intent.getExtras().getString("title"));
//                            mRootRef.setValue(favorites);
//                        } else {
//                            try {
//                                fab.setImageResource(R.drawable.ic_favorite_border_black_24dp);
////                                mRootRef.setValue(null);
//                            } catch (Exception e) {
//                            }
//                        }
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                    }
//                });
            }
        });
        useontab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startcountdown to alarm
                StartRecipeAlarm(v);
            }
        });

        sendtowatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send instructions to watch
            }
        });
        Log.i(TAG, "OnCreate - set recycler view");
        myrv = findViewById(R.id.recipe_ingredients_rv);
        myrv.setLayoutManager(new GridLayoutManager(this, 2));

    }

    private void getRecipeInstructions(final String recipeId) throws IOException, JSONException {
        //https://api.spoonacular.com/recipes/informationBulk?ids=1&apiKey=e5f41960a96343569669c5435cdc2710
//        String URL = " https://api.spoonacular.com/recipes/" + recipeId + "/information?apiKey=e5f41960a96343569669c5435cdc2710";
        String URL = "https://api.spoonacular.com/recipes/informationBulk?ids=" + recipeId + "&apiKey=e5f41960a96343569669c5435cdc2710&instructions=true";
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
                final String myResponse = response.body().string();
                Recipe_activity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONArray jsonArray = new JSONArray(myResponse);
                            JSONObject results = jsonArray.getJSONObject(0);
                            Log.i(TAG, results.toString());
                            try {
                                Picasso.get().load((String) results.get("image")).into(img);
                            } catch (Exception e) {
                                img.setImageResource(R.drawable.nopicture);
                            }
                            title.setText((String) results.getString("title"));
                            ready_in.setText(Integer.toString((Integer) results.get("readyInMinutes")));
                            servings.setText(Integer.toString((Integer) results.get("servings")));
                            RecipeAlarmTime = (int) results.get("readyInMinutes");
                            try{
                                if(results.getString("instructions").equals("")){
                                    throw new Exception("No Instructions");
                                }
                                else {
                                    instructions.setText(Html.fromHtml((String) results.get("instructions")));
                                }
                            } catch(Exception e) {
                                String msg= "Unfortunately, the instructions you were looking for not found, to view the original recipe click on the link below:" + "<a href="+results.get("sourceUrl")+">"+results.get("sourceUrl")+"</a>";
                                instructions.setMovementMethod(LinkMovementMethod.getInstance());
                                instructions.setText(Html.fromHtml(msg));
                            }
                            ingredientsArr = (JSONArray) results.get("extendedIngredients");
                            for (int i = 0; i < ingredientsArr.length(); i++) {
                                JSONObject ingredient = ingredientsArr.getJSONObject(i);
                                ingredientsLst.add(new Ingredient(ingredient.optString("originalString"), ingredient.optString("image")));
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
        });
    }

    private void StartRecipeAlarm(View view){
        Intent i1 = new Intent();
        i1.setAction("com.example.recipeapp.receiver.Message");
        i1.addCategory("android.intent.category.DEFAULT");
        PendingIntent pd = PendingIntent.getBroadcast(this,0,i1,0);
        myAlarmManager.set(AlarmManager.RTC_WAKEUP,RecipeAlarmTime*60*1000,pd);
    }

    private void StopRecipeAlarm(View view){
        Intent i1 = new Intent();
        i1.setAction("com.example.recipeapp.receiver.Message");
        i1.addCategory("android.intent.category.DEFAULT");
        PendingIntent pd = PendingIntent.getBroadcast(this,0,i1,0);
        myAlarmManager.cancel(pd);
    }
}