package com.example.recipeapp;
import com.google.android.gms.wearable.DataMap;

import java.io.Serializable;

class Profile implements Serializable {

    String diet;
    String intolerances;

    Profile(String diet, String intolerances) {
        // When you create a new Profile, it's good to build it based on username and password
        this.diet = diet;
        this.intolerances = intolerances;
    }

    DataMap toDataMap() {
        DataMap dataMap = new DataMap();
        dataMap.putString("diet", diet);
        dataMap.putString("intolerances", intolerances);

        return dataMap;
    }
}
