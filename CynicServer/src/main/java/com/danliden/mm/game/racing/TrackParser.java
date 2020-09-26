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

    private static class Metadata {
        final int nrOfCheckpoints;
        final int nrOfStartingPoints;

        private Metadata(int nrOfCheckpoints, int nrOfStartingPoints) {
            this.nrOfCheckpoints = nrOfCheckpoints;
            this.nrOfStartingPoints = nrOfStartingPoints;
        }
    }

    private static class TrackKeys {
        static final String META_NR_CHECKPOINTS = "NrCheckpoints";
        static final String META_NR_STARTINGPOINTS = "NrStartingPoints";
        static final String PIVOT_X = "PivotX";
        static final String PIVOT_Y = "PivotY";
        static final String WIDTH = "Width";
        static final String HEIGHT = "Height";
        static final String ROTATION = "Rotation";
        static final String START_FINISH = "StartFinish";
        static final String INDEX = "Index";
        static final String STARTING_POINT_X = "xPos";
        static final String STARTING_POINT_Y = "yPos";
    }

    TrackConfig parseTrack(String trackName) throws IOException {
        String trackConfigAsString = getTrackConfigAsString(trackName);
        Metadata meta = extractMetadata(trackConfigAsString);
        List<Checkpoint> checkpoints = generateCheckpointsFromJson(trackConfigAsString);
        List<StartingPoint> startingPoints = generateStartingPointsFromJson(trackConfigAsString);
        return new TrackConfig(meta.nrOfCheckpoints, meta.nrOfStartingPoints, checkpoints, startingPoints);
    }

    private Metadata extractMetadata(String trackConfigAsString) {
        JSONObject trackConfigJson = new JSONObject(trackConfigAsString);
        return new Metadata(
                trackConfigJson.getInt(TrackKeys.META_NR_CHECKPOINTS),
                trackConfigJson.getInt(TrackKeys.META_NR_STARTINGPOINTS)
        );

    }

    private List<StartingPoint> generateStartingPointsFromJson(String trackConfigAsString) {
        JSONArray checkpointsJsonArray = new JSONObject(trackConfigAsString).getJSONArray("StartingPoints");

        List<StartingPoint> startingPoints = new ArrayList<>();
        for (int i = 0; i < checkpointsJsonArray.length(); i++) {
            JSONObject jsonObject = checkpointsJsonArray.getJSONObject(i);

            Vector2 position = new Vector2(
                    jsonObject.getFloat(TrackKeys.STARTING_POINT_X),
                    jsonObject.getFloat(TrackKeys.STARTING_POINT_Y));
            StartingPoint startingPoint = new StartingPoint(position);
            startingPoints.add(startingPoint);
        }

        return startingPoints;

    }

    private String getTrackConfigAsString(String trackName) throws IOException {
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

    private List<Checkpoint> generateCheckpointsFromJson(String trackConfigAsString) {
        JSONArray checkpointsJsonArray = new JSONObject(trackConfigAsString).getJSONArray("Checkpoints");

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
