package com.danliden.mm.utils;

public class GameState{

    public enum GameStateEnum {
        LOBBY,
        IN_SESSION
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