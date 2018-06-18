package com.bomberman.objects;
import com.badlogic.gdx.math.Vector2;

/**
 * @author Maciej Parandyk
 */

public class MaxBomb extends Bomb{
    public MaxBomb(Vector2 position, PlayerInstance player) {
        super(position, player, 12.0f);
    }
}
