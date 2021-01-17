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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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

    private static final String TAG = "Recipe_activity";
    
    private TextView title, ready_in, servings, instructions, healthy;
    private ImageView img, vegeterian;
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    private String recipe_sourceUrl;
    private AnalysedInstructions analysedInstructions;
    private RecyclerView ingredients_rv;
    private RecyclerView instructions_rv;
    private FloatingActionButton fab_bookmark;
    private FloatingActionButton fab_useontab;
    private FloatingActionButton fab_sendtowatch;

    private boolean like = false;
    private int RecipeAlarmTime;
    private int RecipeID;

    AlarmManager myAlarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        Log.v(TAG, " : OnCreate - starting");
        myAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

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
        //vegeterian = findViewById(R.id.recipe_vegetarian);
        instructions = findViewById(R.id.recipe_instructions);
        fab_bookmark = findViewById(R.id.fab_bookmark);
        fab_useontab = findViewById(R.id.fab_useontab);
        fab_sendtowatch = findViewById(R.id.fab_sendtowatch);

        Log.v(TAG, "OnCreate - try getRecipeInstructions");
        try {
            getRecipeInstructions(recipeId);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "OnCreate - add listeners to firebase");
        mRootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.v(TAG, String.valueOf(dataSnapshot));
                if (dataSnapshot.getValue() != null) {
                    fab_bookmark.setImageResource(R.drawable.bookmarked);
                    like = true;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Log.v(TAG, "OnCreate - add listeners to fab");
        fab_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like = !like;
                mRootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (like) {
                            fab_bookmark.setImageResource(R.drawable.bookmarked);
                            Map favorites = new HashMap();
                            favorites.put("img", intent.getExtras().getString("img"));
                            favorites.put("title", intent.getExtras().getString("title"));
                            mRootRef.setValue(favorites);
                        } else {
                            try {
                                fab_bookmark.setImageResource(R.drawable.bookmark);
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

        Log.v(TAG, "OnCreate - setting onclick to useontab fab");
        fab_useontab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "useontab - onClick");
                //startcountdown to alarm

                StartRecipeAlarm(v);

            }
        });


        fab_sendtowatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send instructions to watch
                if (Constants.Recipe_on_Watch==false){
                    sendRecipetoWatch(recipeId);
                    Toast.makeText(getApplicationContext(), "Instructions sent to watch!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Another recipe running on watch!", Toast.LENGTH_LONG).show();
                }
            }
        });

        Log.v(TAG, "OnCreate - set recycler view ingredient");
        ingredients_rv = findViewById(R.id.recipe_ingredients_rv);
        ingredients_rv.setLayoutManager(new GridLayoutManager(this, 2));

        Log.v(TAG, "OnCreate - set recycler view instructions");
        instructions_rv = findViewById(R.id.recipe_instructions_rv);
        instructions_rv.setLayoutManager(new LinearLayoutManager(this));
    }


    private void getRecipeInstructions(final String recipeId) throws IOException, JSONException {
        //https://api.spoonacular.com/recipes/informationBulk?ids=1&apiKey=e5f41960a96343569669c5435cdc2710
        String URL = " https://api.spoonacular.com/recipes/" + recipeId + "/information?addRecipeInformation=true&apiKey="+Constants.spoonacularApiKey;
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
                            JSONObject result = new JSONObject(myResponse);
                            Log.v(TAG, "getRecipeInstructions - " + result.toString());
                            try {
                                Picasso.get().load((String) result.get("image")).into(img);
                            } catch (Exception e) {
                                img.setImageResource(R.drawable.nopicture);
                            }

                            title.setText((String) result.getString("title"));
                            ready_in.setText(Integer.toString((Integer) result.get("readyInMinutes")));
                            servings.setText(Integer.toString((Integer) result.get("servings")));
                            healthy.setText("Health Score: " + result.get("healthScore"));
                            //RecipeID = (int) result.get("id");
                            RecipeAlarmTime = (int) result.get("readyInMinutes");
                            recipe_sourceUrl = (String) result.get("sourceUrl");

                            if(!setAnalysedInstructions(result)){
                                if(!setInstructions(result)){
                                    setMsgForNoInstructions();
                                }
                            }

                            JSONArray ingredientsArr = (JSONArray) result.get("extendedIngredients");
                            List<Ingredient> ingredientsLst = new ArrayList<Ingredient>();
                            for (int i = 0; i < ingredientsArr.length(); i++) {
                                JSONObject ingredient = ingredientsArr.getJSONObject(i);
                                ingredientsLst.add(new Ingredient(ingredient.optString("originalString"), ingredient.optString("image")));
                            }
                            RecyclerViewAdapterRecipeIngredient myAdapter = new RecyclerViewAdapterRecipeIngredient(getApplicationContext(), ingredientsLst);
                            ingredients_rv.setAdapter(myAdapter);
                            ingredients_rv.setItemAnimator(new DefaultItemAnimator());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void sendRecipetoWatch(String recipeId) {
        if(analysedInstructions == null){
            Toast.makeText(this, "No instructions to send to watch", Toast.LENGTH_LONG).show();
        }
        else {
            Intent intentWear = new Intent(this, WearService.class);
            intentWear.setAction(WearService.ACTION_SEND.INSTRUCTIONS.name());
            intentWear.putExtra(WearService.EXTRA_INSTRUCTIONS, analysedInstructions);
            //intentWear.putExtra(WearService.EXTRA_RECIPEID, RecipeID);
            this.startService(intentWear);
            Constants.Recipe_ID = recipeId;
            Constants.Recipe_on_Watch = true;
        }
    }

    private void StartRecipeAlarm(View view){

        Log.i(TAG, "StartRecipeAlarm");

        Intent i1 = new Intent(this, Alarm.class);
        i1.setAction("com.example.recipeapp.receiver.Message");
        i1.addCategory("android.intent.category.DEFAULT");
        PendingIntent pd = PendingIntent.getBroadcast(this,0,i1,0);
        myAlarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + RecipeAlarmTime*60*1000,pd);

    }


    private void StopRecipeAlarm(View view){
        Intent i1 = new Intent(this, Alarm.class);
        i1.setAction("com.example.recipeapp.receiver.Message");
        i1.addCategory("android.intent.category.DEFAULT");
        PendingIntent pd = PendingIntent.getBroadcast(this,0,i1,0);
        myAlarmManager.cancel(pd);
    }

    private boolean setAnalysedInstructions(JSONObject result) throws JSONException {
        JSONArray analysedInstructionsJSONArray = result.getJSONArray("analyzedInstructions");
        if(analysedInstructionsJSONArray.length() != 0) {
            analysedInstructions = new AnalysedInstructions(analysedInstructionsJSONArray.getJSONObject(0));
            if (analysedInstructions.isInstructionsOk()) {
                final RVAdapterRecipeInstructions adapter = new RVAdapterRecipeInstructions(getApplicationContext(), analysedInstructions);
                Log.v(TAG, "getAnalysedInstructions - setOnItemClickListener");
                adapter.setOnItemClickListener(new RVAdapterRecipeInstructions.ClickListener() {
                    @Override
                    public void onItemClick(int position, View v) {
                        Log.v(TAG, "onItemClick at pos " + position);
                        adapter.itemClicked(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                instructions_rv.setAdapter(adapter);
                instructions_rv.setItemAnimator(new DefaultItemAnimator());
                instructions.setVisibility(View.GONE);
                return true;
            }
        }
        return false;
    }

    private boolean setInstructions(JSONObject result) throws JSONException {
        String instructions_txt = result.getString("instructions");
        Log.i(TAG, "getRecipeInstructions - instructions " + instructions_txt);
        if(instructions_txt.equals("null") || instructions_txt.isEmpty()) {
            return false;
        }
        else {
            instructions.setText(instructions_txt);
            return true;
        }
    }

   // @RequiresApi(api = Build.VERSION_CODES.N)
    private void setMsgForNoInstructions(){
        String msg = "Unfortunately, the instructions you were looking for not found, view the original recipe <a href="+recipe_sourceUrl+">here</a>";
        instructions.setText(Html.fromHtml(msg, Html.FROM_HTML_MODE_COMPACT));
        instructions.setMovementMethod(LinkMovementMethod.getInstance());
    }

}