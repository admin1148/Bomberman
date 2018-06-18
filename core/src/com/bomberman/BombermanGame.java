package com.bomberman;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bomberman.screens.TestScreen;

/**
 * @author Maciej Parandyk
 * @author Mateusz Kloc
 */

public class BombermanGame extends Game {
	private Screen screen;
	private SpriteBatch batch;

	private Music music;

	public BombermanGame() {

	}

	@Override
	public void create() {
		this.batch=new SpriteBatch();
		this.screen = new TestScreen(this);
		music = Gdx.audio.newMusic(Gdx.files.internal("./sounds/music.mp3"));
		music.setLooping(true);
		music.setVolume(0.4f);
		music.play();
		setScreen(this.screen);
	}

	@Override
	public void dispose() {
		this.music.dispose();
		this.screen.dispose();
		this.batch.dispose();
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
	this.screen.resize(width, height);
	}

	@Override
	public void setScreen(Screen screen) {
		super.setScreen(screen);
	}

	@Override
	public Screen getScreen() {
		return super.getScreen();
	}

	public SpriteBatch getBatch() {
		return batch;
	}

}
