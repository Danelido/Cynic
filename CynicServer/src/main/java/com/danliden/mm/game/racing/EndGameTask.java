package com.danliden.mm.game.racing;

import com.danliden.mm.game.packet.PacketKeys;
import com.danliden.mm.game.packet.PacketType;
import com.danliden.mm.game.packet.logic.Properties;
import com.danliden.mm.utils.Execution;
import com.danliden.mm.utils.GameState;
import org.json.JSONObject;

public class EndGameTask extends Execution {

    private final Properties properties;

    public EndGameTask(Properties properties) {
        this.properties = properties;
    }

    @Override
    public void run() {
        sendBackToLobbyPackage();
        properties.gameState.setGameState(GameState.GameStateEnum.LOBBY);
        properties.sessionPlayers.resetAllPlayers();
    }

    private void sendBackToLobbyPackage() {
        JSONObject packet = new JSONObject();
        packet.put(PacketKeys.PacketId, PacketType.Outgoing.BACK_TO_LOBBY);
        properties.sender.sendToMultipleWithAck(
                properties.ackHandler,
                packet,
                properties.sessionPlayers.getPlayers(),
                5,
                150);
    }

}
