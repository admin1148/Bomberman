package com.bomberman.server;

/**
 * @author Maciej Parandyk
 * @author Mateusz Kloc
 */


public class BombermanServerException extends Exception{

    public BombermanServerException(String msg){
        super(msg);
    }

    @Override
    public String getMessage() {
        return String.format("Server has encoutered: %s",super.getMessage());
    }
}
