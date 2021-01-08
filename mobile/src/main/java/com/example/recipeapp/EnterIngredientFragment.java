package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

public class EnterIngredientFragment extends Fragment {

    EditText mingredients;

    public static EnterIngredientFragment newInstance() {
        EnterIngredientFragment fragment = new EnterIngredientFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enter_ingredient, container, false);
        mingredients = view.findViewById(R.id.ingredient);

        Button rButton = view.findViewById(R.id.searchButton);

        rButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ingredient = mingredients.getText().toString();
                Intent intent = new Intent(getActivity(), SearchResultsActivity.class);
                intent.putExtra("ingredient_value", ingredient);
                startActivity(intent);
            }
        });

        return view;
    }
}