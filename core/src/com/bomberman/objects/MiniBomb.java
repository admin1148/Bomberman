package com.bomberman.objects;
import com.badlogic.gdx.math.Vector2;

/**
 * @author Mateusz Kloc
 */

public class MiniBomb extends Bomb{
    public MiniBomb(Vector2 position, PlayerInstance player) {
        super(position, player, 8.0f);
    }
}
