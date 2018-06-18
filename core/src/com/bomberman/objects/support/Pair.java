package com.bomberman.objects.support;

/**
 * @author Maciej Parandyk
 * @author Mateusz Kloc
 */


public class Pair {
    private float posX;
    private float posY;

    public Pair() {
    }

    public Pair(float posX, float posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "posX=" + posX +
                ", posY=" + posY +
                '}';
    }
}
