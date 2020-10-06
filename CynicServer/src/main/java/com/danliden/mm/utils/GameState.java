package com.danliden.mm.utils;

public class GameState{

    public enum GameStateEnum {
        LOBBY,
        IN_SESSION,
        IN_SESSION_COUNTDOWN_TO_START,
        IN_SESSION_DOOM_TIMER,
        IN_SESSION_END
    }


    private GameStateEnum gameState;

    public GameState(){
        gameState = GameStateEnum.LOBBY;
    }
    
    public void setGameState(GameStateEnum newGameState){
        this.gameState = newGameState;
    }

    public GameStateEnum getGameState(){
        return this.gameState;
    }
}