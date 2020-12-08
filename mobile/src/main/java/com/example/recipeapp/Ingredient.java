package com.example.recipeapp;

public class Ingredient {
    private String name;
    private String Thumbnail;
    private boolean selected;

    Ingredient(String name, String thumbnail) {
        this.name = name;
        Thumbnail = "https://spoonacular.com/cdn/ingredients_100x100/" + thumbnail;
        selected = false;
    }
}
