package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class enter_ingredient extends AppCompatActivity {

    EditText mingredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_ingredient);
        mingredients = findViewById(R.id.ingredient);


        Button rButton = findViewById(R.id.searchButton);
        rButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ingredient = mingredients.getText().toString();
                Intent intent = new Intent(enter_ingredient.this, SearchResultsActivity.class);
                intent.putExtra("ingredient_value", ingredient);
                startActivity(intent);
            }
        });
    }
}