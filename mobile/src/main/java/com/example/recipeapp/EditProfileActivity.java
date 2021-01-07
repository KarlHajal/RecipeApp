package com.example.recipeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashSet;

public class EditProfileActivity extends AppCompatActivity {

    private HashSet<String> checkedIntolerances = new HashSet<String>();
    private String diet = "";

    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private static final DatabaseReference profileRef = database.getReference("profiles/" + user.getUid());

    public static final String EXTRA_ENTERED_NAME = "ENTERED_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initializeName();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_validate) {
            String enteredName = updateUserFullName();
            saveDietPreferencesInDb();

            final Intent data = new Intent();

            // Add the required data to be returned to the MainActivity
            data.putExtra(EXTRA_ENTERED_NAME, enteredName);

            // Set the resultCode to Activity.RESULT_OK to
            // indicate a success and attach the Intent
            // which contains our result data
            setResult(Activity.RESULT_OK, data);

            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeName(){
        EditText nameEditText = (EditText)findViewById(R.id.nameEditText);
        nameEditText.setText(user.getDisplayName());
    }

    private String updateUserFullName(){
        EditText nameEditText = (EditText)findViewById(R.id.nameEditText);
        String enteredName = nameEditText.getText().toString();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(enteredName).build();
        user.updateProfile(profileUpdates);
        return enteredName;
    }

    private String titleToDbFormat(CharSequence title){
        return title.toString().toLowerCase().replace(' ', '_');
    }

    private void saveDietPreferencesInDb() {
        String commaSeparatedIntolerances = checkedIntolerances.toString().replace("[", "").replace("]", "");
        profileRef.child("intolerances").setValue(commaSeparatedIntolerances);
        profileRef.child("diet").setValue(diet);
    }

    public void onIntoleranceCheckboxClicked(View view) {
        CheckBox checkbox = (CheckBox) view;

        if(checkbox.isChecked()) {
            checkedIntolerances.add(titleToDbFormat(checkbox.getText()));
        }
        else {
            checkedIntolerances.remove(titleToDbFormat(checkbox.getText()));
        }
    }

    public void onDietRadioButtonClicked(View view) {

        RadioButton radioButton = (RadioButton) view;
        String newDiet = titleToDbFormat(radioButton.getText());
        if(diet.equals(newDiet)) {
            diet = "";
            if(radioButton.getParent() instanceof RadioGroup) {
                RadioGroup radioGroup = (RadioGroup) radioButton.getParent();
                radioGroup.clearCheck();
            }
        }
        else{
            diet = newDiet;
        }

    }

    @Override
    public void onBackPressed() {
        // When the user hits the back button set the resultCode
        // to Activity.RESULT_CANCELED to indicate a failure
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }
}