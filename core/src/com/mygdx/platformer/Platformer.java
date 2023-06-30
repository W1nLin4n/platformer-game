package com.mygdx.platformer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.platformer.screens.GameOverScreen;
import com.mygdx.platformer.screens.LoadScreen;
import com.mygdx.platformer.screens.MenuScreen;
import com.mygdx.platformer.screens.levels.Level1;
import com.mygdx.platformer.screens.levels.Level2;
import com.mygdx.platformer.screens.levels.Level3;
import com.mygdx.platformer.tools.Assets;

public class Platformer extends Game {
	public enum GameState {LOAD, MENU, LEVEL1, LEVEL2, LEVEL3, GAME_OVER}
	private GameState currentState;
	private GameState previousState;
    public SpriteBatch batch;
	public Assets assets;
    @Override
	public void create () {
		batch = new SpriteBatch();
		assets = new Assets();
		previousState = GameState.LOAD;
		currentState = GameState.LOAD;
		setScreen(new LoadScreen(this));
	}

	public void update() {
		if(currentState == GameState.LOAD) {
		}
	}

	public void setGameState(GameState gameState) {
		switch (gameState) {
			case MENU:
				setScreen(new MenuScreen(this));
				previousState = currentState;
				currentState = GameState.MENU;
				break;
			case GAME_OVER:
				setScreen(new GameOverScreen(this));
				previousState = currentState;
				currentState = GameState.GAME_OVER;
				break;
			case LEVEL1:
				assets.getLevel(1);
				setScreen(new Level1(this));
				previousState = currentState;
				currentState = GameState.LEVEL1;
				break;
			case LEVEL2:
				assets.getLevel(2);
				setScreen(new Level2(this));
				previousState = currentState;
				currentState = GameState.LEVEL2;
				break;
			case LEVEL3:
				assets.getLevel(3);
				setScreen(new Level3(this));
				previousState = currentState;
				currentState = GameState.LEVEL3;
				break;
		}
	}

	public GameState getCurrentState() {
		return currentState;
	}

	public GameState getPreviousState() {
		return previousState;
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		super.dispose();
		batch.dispose();
		assets.dispose();
	}
}
