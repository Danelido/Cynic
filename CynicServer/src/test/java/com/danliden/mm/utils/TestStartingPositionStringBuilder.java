package com.danliden.mm.utils;

import com.danliden.mm.game.racing.StartingPoint;
import com.danliden.mm.game.session.PlayerClient;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.danliden.mm.utils.StartingPositionsStringBuilder.*;

public class TestStartingPositionStringBuilder {

    @Test
    public void testBuildingString(){
        List<StartingPoint> startingPoints = new ArrayList<>();
        List<PlayerClient> players = new ArrayList<>();

        for(int i = 0; i < 4; i++){
            startingPoints.add(new StartingPoint(new Vector2(i, i)));
        }

        for(int i = 0; i < 4; i++){
            players.add(new PlayerClient(null, null, 0, i, 0));
        }

        String startingPointString = buildStartingPositionsString(players, startingPoints);

        verifyString(startingPointString, players, startingPoints);

    }

    private void verifyString(String startingPointString, List<PlayerClient> players, List<StartingPoint> startingPoints) {
        String[] subs = startingPointString.split(";");
        assert subs.length == players.size();

        for(int i = 0; i < subs.length; i++){
            String[] attribs = subs[i].split("-");
            int id = Integer.parseInt(attribs[0]);
            assert id == players.get(i).id;

            String[] positionString = attribs[1].split(",");
            float xPos = Float.parseFloat(positionString[0]);
            float yPos = Float.parseFloat(positionString[1]);

            assert xPos == startingPoints.get(i).getPosition().x;
            assert yPos == startingPoints.get(i).getPosition().y;
        }


    }
}
