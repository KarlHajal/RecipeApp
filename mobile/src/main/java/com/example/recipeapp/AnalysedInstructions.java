package com.example.recipeapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AnalysedInstructions {
    private String name;
    private List<InstructionStep> instructionStepList = new ArrayList<InstructionStep>();
    private boolean instructionsOk;

    public AnalysedInstructions(JSONObject instructionJSON){
        try {
            this.name = instructionJSON.getString("name");
        } catch (JSONException e) {
            this.name = "";
        }
        try {
            JSONArray stepsJSONArray = instructionJSON.getJSONArray("steps");
            for (int i = 0; i < stepsJSONArray.length(); i++) {
                JSONObject stepJSON = stepsJSONArray.getJSONObject(i);
                instructionStepList.add(new InstructionStep(stepJSON));
            }
        } catch (JSONException e) {
            this.instructionStepList.clear();
        }
        instructionsOk = !(this.name.equals("") && this.instructionStepList.isEmpty());
    }
}
