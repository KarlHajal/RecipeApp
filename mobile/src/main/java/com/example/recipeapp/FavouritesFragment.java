package com.example.recipeapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class FavouritesFragment extends Fragment {
    private List<Recipe> lstFavorites;
    private RecyclerView myrv;
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    private TextView emptyView;

    public static Fragment newInstance() {
        FavouritesFragment fragment = new FavouritesFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View RootView = inflater.inflate(R.layout.fragment_favourites, container, false);
       // Toolbar mToolbarContact = RootView.findViewById(R.id.toolbar_favorites);
        //((AppCompatActivity) getActivity()).setSupportActionBar(mToolbarContact);
        emptyView = RootView.findViewById(R.id.empty_view);
        //((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(mToolbarContact);
        getFavorites(RootView);
        return RootView;
    }

    private void getFavorites(final View rootView) {
        mAuth = FirebaseAuth.getInstance();
        String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        mRootRef = FirebaseDatabase.getInstance().getReference().child(uid);
        mRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lstFavorites = new ArrayList<>();
                HashMap favorites = (HashMap) dataSnapshot.getValue();
                if (favorites != null) {
                    for (Object recipe : favorites.keySet()) {
                        String title = (String) dataSnapshot.child(recipe.toString()).child("title").getValue();
                        String img = (String) dataSnapshot.child(recipe.toString()).child("img").getValue();
                        lstFavorites.add(new Recipe(recipe.toString(), title, img, 0, 0,0));
                    }
                }
                myrv = rootView.findViewById(R.id.recycleview_favorites);
                if(lstFavorites.isEmpty()){
                    myrv.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }
                else{
                    myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                    RecyclerViewAdapterFavorites myAdapter = new RecyclerViewAdapterFavorites(getContext(), lstFavorites);
                    myrv.setAdapter(myAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
