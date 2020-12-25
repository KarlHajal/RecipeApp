package com.example.recipeapp;

import org.json.JSONException;
import org.json.JSONObject;

public class InstructionStep {
    private int number;
    private String step;

    public InstructionStep(JSONObject stepJSON){
        try {
            this.number = stepJSON.getInt("number");
        } catch (JSONException e) {
            this.number = -1;
        }
        try {
            this.step = stepJSON.getString("step");
        } catch (JSONException e) {
            this.step = "error : empty step";
        }
    }
}
