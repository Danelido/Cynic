package com.danliden.mm.game.racing;

import com.danliden.mm.utils.Vector2;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TrackParser {

    private static final Logger logger = LoggerFactory.getLogger(TrackParser.class);

    List<Checkpoint> getCheckpointsFromFile(String trackName) throws IOException {
        String checkpointsAsJsonString = getJsonCheckpointString(trackName);
        JSONArray checkpointsJsonArray = new JSONObject(checkpointsAsJsonString).getJSONArray("Checkpoints");
        return generateCheckpointsFromJson(checkpointsJsonArray);
    }

    private String getJsonCheckpointString(String trackName) throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("/"+trackName);
        if(inputStream == null){
            logger.info("Could not load resource " + trackName);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String jsonString = reader.readLine();

        reader.close();
        inputStream.close();
        return jsonString;
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
