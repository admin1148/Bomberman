package com.bomberman.objects.support;
import com.badlogic.gdx.utils.Array;
import com.bomberman.objects.Bomb;
import com.bomberman.utils.Directions;

/**
 * @author Maciej Parandyk
 */


public class PlayerStateData {

    private boolean bMoving;
    private Directions direction;
    private float posX,posY;

    public PlayerStateData() {
    }

    //getters

    public boolean isMoving() {
        return bMoving;
    }

    public Directions getDirection() {
        return direction;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }


    //setters


    public void setMoving(boolean bMoving) {
        this.bMoving = bMoving;
    }

    public void setDirection(Directions direction) {
        this.direction = direction;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

}
