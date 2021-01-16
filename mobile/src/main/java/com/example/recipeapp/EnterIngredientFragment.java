package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EnterIngredientFragment extends Fragment {

    EditText mingredients;
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private static final DatabaseReference profileRef = database.getReference("profiles/" + user.getUid());
    private Profile userProfile = new Profile("", "");

    public static EnterIngredientFragment newInstance() {
        EnterIngredientFragment fragment = new EnterIngredientFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readUserProfile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enter_ingredient, container, false);
        mingredients = view.findViewById(R.id.ingredient);

        ImageButton rButton = view.findViewById(R.id.searchButton);

        rButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ingredient = mingredients.getText().toString();
                Intent intent = new Intent(getActivity(), SearchResultsActivity.class);
                intent.putExtra("ingredient_value", ingredient);
                intent.putExtra("user_profile", userProfile);
                startActivity(intent);
            }
        });

        return view;
    }

    private void readUserProfile() {
        profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String diet = dataSnapshot.child("diet").getValue(String.class);
                String intolerances = dataSnapshot.child("intolerances").getValue(String.class);

                userProfile = new Profile(diet, intolerances);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Empty
            }
        });
    }
}