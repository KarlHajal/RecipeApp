package com.example.recipeapp;

import com.google.android.gms.wearable.DataMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InstructionStep {
    private String number;
    private String step;
    private int duration; // in minutes
    private List<Ingredient> ingredients = new ArrayList<>();
    private List<Equipment> equipments = new ArrayList<>();

    public InstructionStep(JSONObject stepJSON){
        try {
            this.number = Integer.toString(stepJSON.getInt("number"));
        } catch (JSONException e) {
            this.number = "-1";
            // TODO: 25/12/2020 add log to say error
        }
        try {
            this.step = stepJSON.getString("step");
        } catch (JSONException e) {
            this.step = "error : empty step";
            // TODO: 25/12/2020 add log to say error
        }
        try {
            JSONArray ingredientsJSONArray = stepJSON.getJSONArray("ingredients");
            for (int i = 0; i < ingredientsJSONArray.length(); i++) {
                JSONObject ingredientJSONObject = ingredientsJSONArray.getJSONObject(i);
                ingredients.add(new Ingredient(ingredientJSONObject.getString("name"), ingredientJSONObject.getString("image")));
            }
        } catch (JSONException e) {
            this.ingredients.clear();
            // TODO: 25/12/2020 add log to say error
        }
        try {
            JSONArray equipmentsJSONArray = stepJSON.getJSONArray("equipment");
            for (int i = 0; i < equipmentsJSONArray.length(); i++) {
                JSONObject equipmentJSONObject = equipmentsJSONArray.getJSONObject(i);
                equipments.add(new Equipment(equipmentJSONObject.getString("name"), equipmentJSONObject.getString("image")));
            }
        } catch (JSONException e) {
            this.equipments.clear();
            // TODO: 25/12/2020 add log to say error
        }
        try {
            JSONObject lengthJSONObject = stepJSON.getJSONObject("length");
            try {
                this.duration = lengthJSONObject.getInt("number");
            } catch (JSONException e) {
                this.duration = -1;
                // TODO: 25/12/2020 add log to say error
            }
        } catch (JSONException e) {
            this.duration = 0;
            // no log as length is not given for every step
        }
    }

    public String getStepText(){
        return this.number + ": " + this.step;
    }

    public List<Ingredient> getIngredients(){
        return this.ingredients;
    }

    public List<Equipment> getEquipments(){
        return this.equipments;
    }

    public DataMap toDataMap() {
        // list of instructions steps to dataMap array
        ArrayList<DataMap> ingredientsDataMap = new ArrayList<DataMap>();
        for (Ingredient ingredient: ingredients) {
            ingredientsDataMap.add(ingredient.toDataMap());
        }

        // list of instructions steps to dataMap array
        ArrayList<DataMap> equipmentsDataMap = new ArrayList<DataMap>();
        for (Equipment equipment: equipments) {
            equipmentsDataMap.add(equipment.toDataMap());
        }

        // current InstructionStep into dataMap
        DataMap map = new DataMap();
        map.putString("number", number);
        map.putString("step", step);
        map.putInt("duration", duration);
        map.putDataMapArrayList("ingredients", ingredientsDataMap);
        map.putDataMapArrayList("equipments", equipmentsDataMap);
        return map;
    }
}
