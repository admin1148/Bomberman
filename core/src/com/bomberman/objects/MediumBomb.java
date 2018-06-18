package com.bomberman.objects;
import com.badlogic.gdx.math.Vector2;

/**
 * @author Maciej Parandyk
 */
public class MediumBomb extends Bomb{
    public MediumBomb(Vector2 position, PlayerInstance player) {
        super(position, player, 8.0f);
    }
}
