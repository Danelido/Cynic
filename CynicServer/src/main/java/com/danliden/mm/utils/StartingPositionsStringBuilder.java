package com.danliden.mm.utils;

import com.danliden.mm.game.racing.StartingPoint;
import com.danliden.mm.game.racing.TrackManager;
import com.danliden.mm.game.session.PlayerClient;
import com.danliden.mm.game.session.SessionPlayers;

import java.util.List;

public class StartingPositionsStringBuilder {
    private StartingPositionsStringBuilder(){}

    public static String buildStartingPositionsString(List<PlayerClient> players, List<StartingPoint> startingPoints) {
        // id-xPos,yPos; --|--
        String positionString = "";
        for(int i = 0; i < players.size(); i++){
            PlayerClient player = players.get(i);
            positionString += player.id + "-" + startingPoints.get(i).getPosition().x + "," + startingPoints.get(i).getPosition().y;

            if(i < players.size() - 1){
                positionString += ";";
            }

        }
        return positionString;
    }
}
