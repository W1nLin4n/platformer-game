package com.mygdx.platformer.screens.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.platformer.Platformer;
import com.mygdx.platformer.scenes.DialogBox;
import com.mygdx.platformer.scenes.Hud;
import com.mygdx.platformer.sprites.WorldBox;
import com.mygdx.platformer.sprites.enemies.Enemy;
import com.mygdx.platformer.sprites.enemies.Hlyba;
import com.mygdx.platformer.sprites.players.Luigi;
import com.mygdx.platformer.sprites.players.Mario;
import com.mygdx.platformer.sprites.players.Player;
import com.mygdx.platformer.sprites.players.Princess;
import com.mygdx.platformer.tools.B2DWorldCreator;
import com.mygdx.platformer.tools.Constants;
import com.mygdx.platformer.tools.WorldContactListener;

public abstract class Level implements Screen {

    protected Platformer game;
    protected TextureAtlas atlas;
    protected Hud hud;
    protected DialogBox dialogBox;
    protected OrthographicCamera gameCamera;
    protected Viewport gameViewport;

    // Tiled map variables
    protected TiledMap map;
    protected OrthogonalTiledMapRenderer renderer;

    // Box2d variables
    protected World world;
    //protected Box2DDebugRenderer b2ddr;
    protected B2DWorldCreator b2dwc;

    protected Player player1;
    protected Player player2;
    protected Hlyba hlyba;
    protected Princess princess;
    protected WorldBox worldBox;

    protected Music music;

    public Level(Platformer game, String levelName){
        this.game = game;
        atlas = game.assets.get("Sprites.atlas", TextureAtlas.class);
        gameCamera = new OrthographicCamera();
        gameViewport = new FitViewport(Constants.toMeters(Constants.V_WIDTH), Constants.toMeters(Constants.V_HEIGHT), gameCamera);
        gameViewport.apply();

        hud = new Hud(game.batch);
        dialogBox = null;

        map = game.assets.get(levelName);
        renderer = new OrthogonalTiledMapRenderer(map, Constants.toMeters(1));
        gameCamera.position.set(gameViewport.getWorldWidth()/2, gameViewport.getWorldHeight()/2, 0);

        world = new World(new Vector2(0, -10), true);
        //b2ddr = new Box2DDebugRenderer();
        b2dwc = new B2DWorldCreator(this);

        player1 = new Mario(this);
        player2 = new Luigi(this);
        hlyba = null;
        princess = null;

        worldBox = new WorldBox(this, getWorldWidth(), getWorldHeight());

        world.setContactListener(new WorldContactListener());

        music = game.assets.get("audio/music/mario_music.ogg", Music.class);
        music.setLooping(true);
        music.play();
    }

    public abstract void triggerEvent(Object o, String event);

    public TiledMap getMap() {
        return map;
    }

    public World getWorld() {
        return world;
    }

    public float getWorldWidth() {
        return ((Integer) map.getProperties().get("width")) * ((Integer) map.getProperties().get("tilewidth"));
    }

    public float getWorldHeight() {
        return ((Integer) map.getProperties().get("height")) * ((Integer) map.getProperties().get("tileheight"));
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public Hud getHud() {
        return hud;
    }

    public Platformer getGame() {
        return game;
    }

    @Override
    public void show() {

    }

    public void handleInput(float delta) {
        if(player1.currentState != Player.State.DEAD && player2.currentState != Player.State.DEAD) {
            player1.handleInput(delta);
            player2.handleInput(delta);
        }
    }

    public void death() {
        game.assets.get("audio/music/mario_music.ogg", Music.class).stop();
        game.assets.get("audio/sounds/laugh.wav", Sound.class).play();
        player1.kill();
        player2.kill();
    }

    public boolean gameOver() {
        if((player1.currentState == Player.State.DEAD && player1.getStateTimer() > 3)  || (player2.currentState == Player.State.DEAD && player2.getStateTimer() > 3))
            return true;
        return false;
    }

    public void update(float delta) {
        if(dialogBox != null) {
            if(dialogBox.dialogFinished) {
                dialogBox.dispose();
                dialogBox = null;
            } else {
                dialogBox.update(delta);
            }
            return;
        }

        handleInput(delta);

        world.step(1/ Constants.FPS, 6, 2);

        player1.update(delta);
        player2.update(delta);
        for(Enemy enemy : b2dwc.getEnemies()) {
            enemy.update(delta);
            if(enemy.getX() < gameCamera.position.x + Constants.toMeters(240))
                if(enemy.b2dbody != null)
                    enemy.b2dbody.setActive(true);
        }
        if(hlyba != null)
            hlyba.update(delta);

        hud.update(delta);
        if(hud.toSetGameOver) {
            hud.toSetGameOver = false;
            death();
        }

        if(player1.currentState != Player.State.DEAD && player2.currentState != Player.State.DEAD) {
            gameCamera.position.x =
                    Math.min(
                            Math.max(
                                    Math.max(
                                            player1.b2dbody.getPosition().x,
                                            player2.b2dbody.getPosition().x),
                                    Constants.toMeters(Constants.V_WIDTH/2f)),
                            Constants.toMeters(getWorldWidth() - Constants.V_WIDTH/2f));
        }

        gameCamera.update();
        renderer.setView(gameCamera);
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        //b2ddr.render(world, gameCamera.combined);

        game.batch.setProjectionMatrix(gameCamera.combined);
        game.batch.begin();
        player1.draw(game.batch);
        player2.draw(game.batch);

        for(Enemy enemy : b2dwc.getEnemies())
            enemy.draw(game.batch);
        if(hlyba != null)
            hlyba.draw(game.batch);
        if(princess != null)
            princess.draw(game.batch);
        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if(dialogBox != null)
            dialogBox.stage.draw();

        if(gameOver()) {
            game.setGameState(Platformer.GameState.GAME_OVER);
        }
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        //b2ddr.dispose();
        hud.dispose();
    }
}
