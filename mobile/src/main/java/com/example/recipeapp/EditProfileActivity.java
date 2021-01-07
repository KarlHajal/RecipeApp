package com.example.recipeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashSet;

public class EditProfileActivity extends AppCompatActivity {

    private HashSet<String> checkedIntolerances = new HashSet<String>();
    private String diet = "";

    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private static final DatabaseReference profileRef = database.getReference("profiles/" + user.getUid());



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_validate) {
            saveDietPreferencesInDb();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private String titleToDbFormat(CharSequence title){
        return title.toString().toLowerCase().replace(' ', '_');
    }

    private void saveDietPreferencesInDb() {
        String commaSeparatedIntolerances = checkedIntolerances.toString().replace("[", "").replace("]", "");
        profileRef.child("intolerances").setValue(commaSeparatedIntolerances);
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
}