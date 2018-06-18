package com.bomberman.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.bomberman.BombermanGame;
import com.bomberman.utils.GameDefinitions;

/**
 * Klasa uruchamiaj±ca wersjê Desktop gry Bomberman.
 *
 * @author Mateusz Kloc
 *
 */

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width= (int)GameDefinitions.VIEWPORT_WIDTH;
		config.height=(int)GameDefinitions.VIEWPORT_HEIGHT;
		new LwjglApplication(new BombermanGame(), config);
	}
}
