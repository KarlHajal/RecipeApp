package com.example.recipeapp;

import com.google.android.gms.wearable.DataMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class AnalysedInstructions implements Serializable {

    private static final String TAG = "AnalysedInstructions";

    private String name;
    private List<InstructionStep> instructionSteps = new ArrayList<InstructionStep>();
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
                instructionSteps.add(new InstructionStep(stepJSON));
            }
        } catch (JSONException e) {
            this.instructionSteps.clear();
        }
        instructionsOk = !(this.name.equals("") && this.instructionSteps.isEmpty());
    }

    public int size(){
        return this.instructionSteps.size();
    }

    public InstructionStep get(int index){
        return this.instructionSteps.get(index);
    }

    public DataMap toDataMap() {
        // list of instructions steps to dataMap array
        ArrayList<DataMap> instructionStepsDataMap = new ArrayList<DataMap>();
        for (InstructionStep instructionStep: instructionSteps) {
            instructionStepsDataMap.add(instructionStep.toDataMap());
        }

        // current analysedInstructions into dataMap
        DataMap map = new DataMap();
        map.putString("name", name);
        map.putDataMapArrayList("instructionSteps", instructionStepsDataMap);
        return map;
    }
}
