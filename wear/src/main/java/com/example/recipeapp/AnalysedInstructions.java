package com.example.recipeapp;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.wearable.DataMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AnalysedInstructions implements Parcelable {

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

    // needed for dataMap -> sending through wear api

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
        map.putBoolean("instructionsOk", instructionsOk);
        return map;
    }

    public AnalysedInstructions(DataMap dataMap){
        this.name = dataMap.getString("name");
        ArrayList<DataMap> instructionStepsDataMap = dataMap.getDataMapArrayList("instructionSteps");
        for (DataMap instructionStepDataMap: instructionStepsDataMap) {
            this.instructionSteps.add(new InstructionStep(instructionStepDataMap));
        }
        this.instructionsOk = dataMap.getBoolean("instructionsOk");
    }

    // needed for parcel -> sending through intent

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeList(instructionSteps);
        dest.writeString(Boolean.toString(instructionsOk));
    }

    private AnalysedInstructions(Parcel in) {
        name = in.readString();
        in.readList(instructionSteps, InstructionStep.class.getClassLoader());
        instructionsOk = Boolean.parseBoolean(in.readString());
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<AnalysedInstructions> CREATOR = new Parcelable.Creator<AnalysedInstructions>() {
        public AnalysedInstructions createFromParcel(Parcel in) {
            return new AnalysedInstructions(in);
        }

        public AnalysedInstructions[] newArray(int size) {
            return new AnalysedInstructions[size];
        }
    };

}
