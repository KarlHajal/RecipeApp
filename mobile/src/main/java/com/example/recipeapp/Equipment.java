package com.example.recipeapp;

public class Equipment {
    private String name;
    private String Thumbnail;
    private boolean selected;

    Equipment(String name, String thumbnail) {
        this.name = name;
        Thumbnail = "https://spoonacular.com/cdn/equipment_100x100/" + thumbnail;
        selected = false;
    }

    public String getName() {
        return name;
    }

    public String getThumbnail() {
        return Thumbnail;
    }

    public boolean isSelected(){
        return selected;
    }

    public void setSelected() {
        selected = !selected;
    }
}
