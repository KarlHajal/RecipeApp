package com.example.recipeapp;

import com.google.android.gms.wearable.DataMap;

public class Equipment {
    private String name;
    private String thumbnail;
    private boolean selected;

    Equipment(String name, String thumbnail) {
        this.name = name;
        this.thumbnail = "https://spoonacular.com/cdn/equipment_100x100/" + thumbnail;
        selected = false;
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

    public DataMap toDataMap() {
        // current ingredient into dataMap
        DataMap map = new DataMap();
        map.putString("name", name);
        map.putString("thumbnail", thumbnail);
        map.putBoolean("selected", selected);
        return map;
    }
}
