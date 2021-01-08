package com.example.recipeapp;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.wearable.DataMap;

public class Ingredient implements Parcelable {
    private String name;
    private String thumbnail;
    private boolean selected;

    Ingredient(String name, String thumbnail) {
        this.name = name;
        this.thumbnail = "https://spoonacular.com/cdn/ingredients_100x100/" + thumbnail;
        this.selected = false;
    }

    public String getName() {
        return name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public boolean isSelected(){
        return selected;
    }

    public void setSelected() {
        selected = !selected;
    }

    // needed for dataMap -> sending through wear api

    public Ingredient(DataMap ingredientDataMap) {
        this.name = ingredientDataMap.getString("name");
        this.thumbnail = ingredientDataMap.getString("thumbnail");
        this.selected = ingredientDataMap.getBoolean("selected");
    }

    public DataMap toDataMap() {
        // current ingredient into dataMap
        DataMap map = new DataMap();
        map.putString("name", name);
        map.putString("thumbnail", thumbnail);
        map.putBoolean("selected", selected);
        return map;
    }

    // needed for parcel -> sending through intent

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(thumbnail);
        dest.writeString(Boolean.toString(selected));
    }

    private Ingredient(Parcel in) {
        name = in.readString();
        thumbnail = in.readString();
        selected = Boolean.parseBoolean(in.readString());
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Ingredient> CREATOR = new Parcelable.Creator<Ingredient>() {
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };

}
