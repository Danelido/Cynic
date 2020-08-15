package com.danliden.mm.game.racing;

import com.danliden.mm.utils.Vector2;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TrackParser {

    List<Checkpoint> getCheckpointsFromFile(String trackName) throws IOException {

        File file = getFile(trackName);
        String checkpointsAsJsonString = getJsonCheckpointString(file);
        JSONArray checkpointsJsonArray = new JSONObject(checkpointsAsJsonString).getJSONArray("Checkpoints");
        return generateCheckpointsFromJson(checkpointsJsonArray);
    }

    @NotNull
    private File getFile(String trackName) {
        String path = "gameplay/tracks/" + trackName;
        ClassLoader classLoader = getClass().getClassLoader();
        URL resources = classLoader.getResource(path);
        if (resources == null) {
            throw new IllegalArgumentException(("Can not find track config file: " + path));
        }

        return new File(resources.getFile());
    }

    private String getJsonCheckpointString(File file) throws IOException {
        try (FileReader reader = new FileReader(file);
         BufferedReader br = new BufferedReader(reader)) {
            br.readLine();// Timestamp, is just ignored
            return br.readLine();
        }
    }

    private List<Checkpoint> generateCheckpointsFromJson(JSONArray checkpointsJsonArray){
        List<Checkpoint> checkpointList = new ArrayList<>();
        for (int i = 0; i < checkpointsJsonArray.length(); i++){
            JSONObject jsonObject = checkpointsJsonArray.getJSONObject(i);

            Vector2 pivot = new Vector2(
                    jsonObject.getFloat("PivotX"),
                    jsonObject.getFloat("PivotY"));
            Vector2 size = new Vector2(
                    jsonObject.getFloat("Width"),
                    jsonObject.getFloat("Height"));
            float rotation = jsonObject.getFloat("Rotation");
            boolean startFinish = jsonObject.getBoolean("StartFinish");
            int index = jsonObject.getInt("Index");
            Checkpoint checkpoint = new Checkpoint(pivot, index, startFinish, size, rotation);
            checkpointList.add(index, checkpoint);
        }

        return checkpointList;
    }

}
