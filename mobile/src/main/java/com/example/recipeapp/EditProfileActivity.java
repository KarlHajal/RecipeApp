package com.example.recipeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

import java.util.HashSet;

public class EditProfileActivity extends AppCompatActivity {

    private HashSet<String> checkedFoodPreferences = new HashSet<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        addDietaryPreferencesChoices();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_validate) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private String titleToDbFormat(CharSequence title){
        return title.toString().toLowerCase().replace(' ', '_');
    }

    private void addDietaryPreferencesChoices() {
        Resources res = getResources();
        String[] dietaryPreferences = res.getStringArray(R.array.dietary_preferences);
        LinearLayout linearLayout = findViewById(R.id.dietaryPreferencesLinearLayout);

        TypedValue value = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.listChoiceIndicatorSingle, value, true);
        int checkMarkDrawableResId = value.resourceId;

        for(String dietaryPreference : dietaryPreferences){

            final CheckedTextView checkedTextView = new CheckedTextView(this);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMarginStart(10);
            layoutParams.setMarginEnd(10);
            checkedTextView.setLayoutParams(layoutParams);

            int imgId = res.getIdentifier(dietaryPreference, "drawable", getPackageName());
            Drawable img = ResourcesCompat.getDrawable(res, imgId, null);
            checkedTextView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);

            checkedTextView.setCheckMarkDrawable(checkMarkDrawableResId);
            checkedTextView.setCheckMarkTintList(ColorStateList.valueOf(Color.rgb(225, 170, 4)));

            checkedTextView.setChecked(false);
            checkedTextView.setText(dietaryPreference.replace('_', ' ').toUpperCase());
            checkedTextView.setTextAppearance(R.style.DietaryPreferencesCheckboxes);
            checkedTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            checkedTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkedTextView.toggle();
                    if(checkedTextView.isChecked()) {
                        checkedFoodPreferences.add(titleToDbFormat(checkedTextView.getText()));
                    }
                    else {
                        checkedFoodPreferences.remove(titleToDbFormat(checkedTextView.getText()));
                    }
                }
            });

            linearLayout.addView(checkedTextView);
        }
    }
}