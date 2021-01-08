package com.example.recipeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {

    private HashSet<String> checkedIntolerances = new HashSet<String>();
    private String diet = "";

    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private static final DatabaseReference profileRef = database.getReference("profiles/" + user.getUid());

    public static final String EXTRA_ENTERED_NAME = "ENTERED_NAME";

    public final HashMap<String, Integer> dietNameToRadioButton = new HashMap<>();
    public final HashMap<String, Integer> intoleranceToCheckbox = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initializeName();
        initializeDietaryPreferences();
    }

    private void initializeDietaryPreferences() {
        initializeDietHashMap();
        initializeIntoleranceHashMap();

        Bundle b = getIntent().getExtras();
        if(b != null){
            Profile userProfile = (Profile) b.getSerializable("userProfile");

            String dietText = userProfile.diet;
            if(dietText != null && !dietText.isEmpty()){
                int radioButtonId = dietNameToRadioButton.get(dietText);
                RadioButton radioButton = (RadioButton) findViewById(radioButtonId);
                if(radioButton != null){
                    radioButton.setChecked(true);
                    diet = dietText;
                }
            }

            String intolerancesText = userProfile.intolerances;
            if(intolerancesText != null && !intolerancesText.isEmpty()){
                List<String> intolerancesList = Arrays.asList(intolerancesText.split("\\s*,\\s*"));

                for(String intoleranceText : intolerancesList){
                    int checkboxId = intoleranceToCheckbox.get(intoleranceText);
                    CheckBox checkBox = (CheckBox) findViewById(checkboxId);
                    if(checkBox != null){
                        checkBox.setChecked(true);
                        checkedIntolerances.add(intoleranceText);
                    }
                }
            }
        }
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

            Intent intent = new Intent(this, HomepageActivity.class);
            startActivity(intent);

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

    private void initializeDietHashMap() {
        dietNameToRadioButton.put(titleToDbFormat(getString(R.string.diet_gluten_free)), R.id.radio_gluten_free);
        dietNameToRadioButton.put(titleToDbFormat(getString(R.string.diet_ketogenic)), R.id.radio_ketogenic);
        dietNameToRadioButton.put(titleToDbFormat(getString(R.string.diet_vegetarian)), R.id.radio_vegetarian);
        dietNameToRadioButton.put(titleToDbFormat(getString(R.string.diet_lacto_vegetarian)), R.id.radio_lacto_vegetarian);
        dietNameToRadioButton.put(titleToDbFormat(getString(R.string.diet_ovo_vegetarian)), R.id.radio_ovo_vegetarian);
        dietNameToRadioButton.put(titleToDbFormat(getString(R.string.diet_vegan)), R.id.radio_vegan);
        dietNameToRadioButton.put(titleToDbFormat(getString(R.string.diet_pescetarian)), R.id.radio_pescetarian);
        dietNameToRadioButton.put(titleToDbFormat(getString(R.string.diet_paleo)), R.id.radio_paleo);
        dietNameToRadioButton.put(titleToDbFormat(getString(R.string.diet_primal)), R.id.radio_primal);
        dietNameToRadioButton.put(titleToDbFormat(getString(R.string.diet_whole30)), R.id.radio_whole30);
    }

    private void initializeIntoleranceHashMap() {
        intoleranceToCheckbox.put(titleToDbFormat(getString(R.string.intolerance_dairy)), R.id.checkbox_dairy);
        intoleranceToCheckbox.put(titleToDbFormat(getString(R.string.intolerance_egg)), R.id.checkbox_egg);
        intoleranceToCheckbox.put(titleToDbFormat(getString(R.string.intolerance_gluten)), R.id.checkbox_gluten);
        intoleranceToCheckbox.put(titleToDbFormat(getString(R.string.intolerance_grain)), R.id.checkbox_grain);
        intoleranceToCheckbox.put(titleToDbFormat(getString(R.string.intolerance_peanut)), R.id.checkbox_peanut);
        intoleranceToCheckbox.put(titleToDbFormat(getString(R.string.intolerance_seafood)), R.id.checkbox_seafood);
        intoleranceToCheckbox.put(titleToDbFormat(getString(R.string.intolerance_sesame)), R.id.checkbox_sesame);
        intoleranceToCheckbox.put(titleToDbFormat(getString(R.string.intolerance_shellfish)), R.id.checkbox_shellfish);
        intoleranceToCheckbox.put(titleToDbFormat(getString(R.string.intolerance_soy)), R.id.checkbox_soy);
        intoleranceToCheckbox.put(titleToDbFormat(getString(R.string.intolerance_sulfite)), R.id.checkbox_sulfite);
        intoleranceToCheckbox.put(titleToDbFormat(getString(R.string.intolerance_tree_nut)), R.id.checkbox_tree_nut);
        intoleranceToCheckbox.put(titleToDbFormat(getString(R.string.intolerance_wheat)), R.id.checkbox_wheat);
    }

}