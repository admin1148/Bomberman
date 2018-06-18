package com.bomberman.server;

import com.badlogic.gdx.utils.Array;
import com.bomberman.objects.PlayerInstance;

/**
 * @author Maciej Parandyk
 * @author Mateusz Kloc
 */


public interface ServerController {
    void closeConnection();
    void update();
    void setPlayers(Array<PlayerInstance> players);
    String getBlocks();
    Integer getPosition();
}
