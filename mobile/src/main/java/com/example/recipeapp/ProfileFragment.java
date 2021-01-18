package com.example.recipeapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class ProfileFragment extends Fragment {

    private View fragmentView;
    private Profile userProfile;
    private Menu optionsMenu;
    private ValueEventListener profileDataChangesListener;

    private static final int RC_EDIT_PROFILE = 444;

    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private static final DatabaseReference profileRef = database.getReference("profiles/" + user.getUid());

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile, menu);

        MenuItem editProfileButton = menu.findItem(R.id.edit_profile_button);
        Drawable drawable = editProfileButton.getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(getActivity(), R.color.colorAccent));
        editProfileButton.setIcon(drawable);
        editProfileButton.setEnabled(false);

        optionsMenu = menu;

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        readUserProfile();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(profileDataChangesListener != null) {
            profileRef.removeEventListener(profileDataChangesListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.edit_profile_button) {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);

            Bundle b = new Bundle();
            b.putSerializable("userProfile", userProfile);

            intent.putExtras(b); //Put your id to your next Intent
            startActivityForResult(intent, RC_EDIT_PROFILE);
        }
        else if (item.getItemId() == R.id.sign_out_button){
            AuthUI.getInstance().signOut(getContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
                public void onComplete(@NonNull Task<Void> task) {
                    // user is now signed out
                    startActivity(new Intent(getActivity(), LoginActivity.class));

                    getActivity().finish();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_EDIT_PROFILE){

            if(resultCode == Activity.RESULT_OK) {

                final String enteredName = data.getStringExtra(EditProfileActivity.EXTRA_ENTERED_NAME);

                setProfileInfo(enteredName, false);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_profile, container, false);


        return fragmentView;
    }

    private void readUserProfile() {
        profileDataChangesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String diet = dataSnapshot.child("diet").getValue(String.class);
                String intolerances = dataSnapshot.child("intolerances").getValue(String.class);

                userProfile = new Profile(diet, intolerances);

                optionsMenu.findItem(R.id.edit_profile_button).setEnabled(true);
                setProfileInfo("", true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Empty
            }
        };

        profileRef.addValueEventListener(profileDataChangesListener);
    }

    private static String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    private static String capitalizeLetterAfterSequencer(String original, String sequence) {
        int position = original.indexOf(sequence);

        if (position != -1) {
            original = original.substring(0, position+1) + Character.toUpperCase(original.charAt(position+1)) + original.substring(position + 2);
        }

        return original;
    }

    private void setProfileInfo(String enteredName, boolean nameFromDB) {
        TextView fullnameTextView = fragmentView.findViewById(R.id.profileFullName);
        String fullname;
        if(!nameFromDB) {
            fullname = enteredName;
        }
        else {
            fullname = user.getDisplayName();
        }

        if (fullname == null || fullname.isEmpty()) {
            fullname = "Amazing Chef";
        }
        fullnameTextView.setText(fullname);

        TextView dietTextView = fragmentView.findViewById(R.id.dietValue);
        String dietText = userProfile.diet;

        dietText = dietText.replace('_', ' ');
        dietText = capitalizeFirstLetter(dietText);
        dietText = capitalizeLetterAfterSequencer(dietText, "-");
        dietText = capitalizeLetterAfterSequencer(dietText, " ");

        if(dietText.isEmpty()) {
            dietText = getString(R.string.diet_non_restrictive);
        }

        dietTextView.setText(dietText);

        TextView intolerancesTextView = fragmentView.findViewById(R.id.intolerancesValue);
        String intolerancesText = userProfile.intolerances;
        List<String> intolerancesList = Arrays.asList(intolerancesText.split("\\s*,\\s*"));
        Collections.sort(intolerancesList);

        final ListIterator<String> li = intolerancesList.listIterator();
        while (li.hasNext()) {
            li.set(capitalizeFirstLetter(li.next().replace('_', ' ')));
        }

        intolerancesText = join(", ", intolerancesList);

        if(intolerancesText.isEmpty()){
            intolerancesText = "None";
        }
        intolerancesTextView.setText(intolerancesText);
    }

    private static String join(String separator, List<String> input) {

        if (input == null || input.size() <= 0) return "";

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < input.size(); i++) {

            sb.append(input.get(i));

            // if not the last item
            if (i != input.size() - 1) {
                sb.append(separator);
            }

        }

        return sb.toString();

    }
}