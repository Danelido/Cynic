package com.danliden.mm.game.racing;

import com.danliden.mm.utils.Vector2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TrackParser {
    private static final Logger logger = LoggerFactory.getLogger(TrackParser.class);

    private static class TrackKeys {
        static final String PIVOT_X = "PivotX";
        static final String PIVOT_Y = "PivotY";
        static final String WIDTH = "Width";
        static final String HEIGHT = "Height";
        static final String ROTATION = "Rotation";
        static final String START_FINISH = "StartFinish";
        static final String INDEX = "Index";
    }

    List<Checkpoint> getCheckpointsFromFile(String trackName) throws IOException {
        String checkpointsAsJsonString = getJsonCheckpointString(trackName);
        JSONArray checkpointsJsonArray = new JSONObject(checkpointsAsJsonString).getJSONArray("Checkpoints");
        return generateCheckpointsFromJson(checkpointsJsonArray);
    }

    private String getJsonCheckpointString(String trackName) throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("/gameplay/tracks/" + trackName);
        if (inputStream == null) {
            logger.info("Could not load resource " + trackName);
            throw new NullPointerException();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String jsonString = reader.readLine();

        reader.close();
        inputStream.close();
        return jsonString;
    }

    private List<Checkpoint> generateCheckpointsFromJson(JSONArray checkpointsJsonArray) {
        List<Checkpoint> checkpointList = new ArrayList<>();
        for (int i = 0; i < checkpointsJsonArray.length(); i++) {
            JSONObject jsonObject = checkpointsJsonArray.getJSONObject(i);

            Vector2 pivot = new Vector2(
                    jsonObject.getFloat(TrackKeys.PIVOT_X),
                    jsonObject.getFloat(TrackKeys.PIVOT_Y));
            Vector2 size = new Vector2(
                    jsonObject.getFloat(TrackKeys.WIDTH),
                    jsonObject.getFloat(TrackKeys.HEIGHT));
            float rotation = jsonObject.getFloat(TrackKeys.ROTATION);
            boolean startFinish = jsonObject.getBoolean(TrackKeys.START_FINISH);
            int index = jsonObject.getInt(TrackKeys.INDEX);
            Checkpoint checkpoint = new Checkpoint(pivot, index, startFinish, size, rotation);
            checkpointList.add(index, checkpoint);
        }

        return checkpointList;
    }

}
