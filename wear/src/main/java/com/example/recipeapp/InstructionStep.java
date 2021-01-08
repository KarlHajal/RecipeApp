package com.example.recipeapp;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.wearable.DataMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InstructionStep implements Parcelable {
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

    @Override
    public String toString(){
        String s = "{ number " + number + ", step " + step + "}";
        return s;
    }

    // needed for dataMap -> sending through wear api

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

    public InstructionStep(DataMap instructionStepDataMap) {
        this.number = instructionStepDataMap.getString("number");
        this.step = instructionStepDataMap.getString("step");
        this.duration = instructionStepDataMap.getInt("duration");

        ArrayList<DataMap> ingredientsDataMap = instructionStepDataMap.getDataMapArrayList("ingredients");
        for (DataMap ingredientDataMap: ingredientsDataMap) {
            this.ingredients.add(new Ingredient(ingredientDataMap));
        }

        ArrayList<DataMap> equipmentsDataMap = instructionStepDataMap.getDataMapArrayList("equipments");
        for (DataMap equipmentDataMap: equipmentsDataMap) {
            this.equipments.add(new Equipment(equipmentDataMap));
        }
    }

    // needed for parcel -> sending through intent

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(number);
        dest.writeString(step);
        dest.writeInt(duration);
        dest.writeList(ingredients);
        dest.writeList(equipments);
    }

    private InstructionStep(Parcel in) {
        number = in.readString();
        step = in.readString();
        duration = in.readInt();
        in.readList(ingredients, Ingredient.class.getClassLoader());
        in.readList(equipments, Equipment.class.getClassLoader());
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<InstructionStep> CREATOR = new Parcelable.Creator<InstructionStep>() {
        public InstructionStep createFromParcel(Parcel in) {
            return new InstructionStep(in);
        }

        public InstructionStep[] newArray(int size) {
            return new InstructionStep[size];
        }
    };

}
