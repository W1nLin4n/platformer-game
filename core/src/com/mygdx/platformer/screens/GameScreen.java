package com.mygdx.platformer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.platformer.Platformer;
import com.mygdx.platformer.scenes.Hud;
import com.mygdx.platformer.screens.GameOverScreen;
import com.mygdx.platformer.sprites.Interactive;
import com.mygdx.platformer.sprites.WorldBox;
import com.mygdx.platformer.sprites.enemies.Enemy;
import com.mygdx.platformer.sprites.items.Item;
import com.mygdx.platformer.sprites.items.ItemDef;
import com.mygdx.platformer.sprites.items.Mushroom;
import com.mygdx.platformer.sprites.players.Luigi;
import com.mygdx.platformer.sprites.players.Mario;
import com.mygdx.platformer.sprites.players.Player;
import com.mygdx.platformer.tools.B2DWorldCreator;
import com.mygdx.platformer.tools.Constants;
import com.mygdx.platformer.tools.WorldContactListener;

import java.util.concurrent.LinkedBlockingQueue;

public class GameScreen implements Screen {

    private Platformer game;
    private TextureAtlas atlas;
    private Hud hud;
    private OrthographicCamera gameCamera;
    private Viewport gameViewport;

    // Tiled map variables
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    // Box2d variables
    private World world;
    private Box2DDebugRenderer b2ddr;
    private B2DWorldCreator b2dwc;

    private Player player1;
    private Player player2;
    private WorldBox worldBox;

    private Music music;

    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;

    public GameScreen(Platformer game){
        this.game = game;
        atlas = game.assets.get("Sprites.atlas", TextureAtlas.class);
        gameCamera = new OrthographicCamera();
        gameViewport = new FitViewport(Constants.toMeters(Constants.V_WIDTH), Constants.toMeters(Constants.V_HEIGHT), gameCamera);
        gameViewport.apply();

        hud = new Hud(game.batch);

        map = game.assets.get("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, Constants.toMeters(1));
        gameCamera.position.set(gameViewport.getWorldWidth()/2, gameViewport.getWorldHeight()/2, 0);

        world = new World(new Vector2(0, -10), true);
        b2ddr = new Box2DDebugRenderer();
        b2dwc = new B2DWorldCreator(this);

        player1 = new Mario(this);
        player2 = new Luigi(this);

        worldBox = new WorldBox(this, (Integer) map.getProperties().get("width") * (Integer) map.getProperties().get("tilewidth"), (Integer) map.getProperties().get("height") * (Integer) map.getProperties().get("tileheight"));

        world.setContactListener(new WorldContactListener());

        music = game.assets.get("audio/music/mario_music.ogg", Music.class);
        music.setLooping(true);
        music.play();

        items = new Array<>();
        itemsToSpawn = new LinkedBlockingQueue<>();
    }

    public TiledMap getMap() {
        return map;
    }

    public World getWorld() {
        return world;
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

    public void spawnItem(ItemDef itemDef) {
        itemsToSpawn.add(itemDef);
    }

    public void handleSpawningItems() {
        if(!itemsToSpawn.isEmpty()) {
            ItemDef itemDef = itemsToSpawn.poll();
            if(itemDef.type == Mushroom.class) {
                items.add(new Mushroom(this, itemDef.position.x, itemDef.position.y));
            }
        }
    }

    public void handleInput(float delta) {
        if(player1.currentState != Player.State.DEAD && player2.currentState != Player.State.DEAD) {
            player1.handleInput(delta);
            player2.handleInput(delta);
        }
    }

    public void death() {
        game.assets.get("audio/music/mario_music.ogg", Music.class).stop();
        game.assets.get("audio/sounds/mariodie.wav", Sound.class).play();
        player1.kill();
        player2.kill();
    }

    public boolean gameOver() {
        if((player1.currentState == Player.State.DEAD && player1.getStateTimer() > 3)  || (player2.currentState == Player.State.DEAD && player2.getStateTimer() > 3) || hud.timeLeft() == 0)
            return true;
        return false;
    }

    public void update(float delta) {
        handleInput(delta);
        handleSpawningItems();

        world.step(1/ Constants.FPS, 6, 2);

        player1.update(delta);
        player2.update(delta);
        for(Enemy enemy : b2dwc.getEnemies()) {
            enemy.update(delta);
            if(enemy.getX() < gameCamera.position.x + Constants.toMeters(240))
                enemy.b2dbody.setActive(true);
        }

        for(Item item : items)
            item.update(delta);

        hud.update(delta);

        if(player1.currentState != Player.State.DEAD && player2.currentState != Player.State.DEAD) {
            gameCamera.position.x = Math.max(Math.max(player1.b2dbody.getPosition().x, player2.b2dbody.getPosition().x), Constants.toMeters(Constants.V_WIDTH/2f));
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

        b2ddr.render(world, gameCamera.combined);

        game.batch.setProjectionMatrix(gameCamera.combined);
        game.batch.begin();
        player1.draw(game.batch);
        player2.draw(game.batch);
        for(Enemy enemy : b2dwc.getEnemies())
            enemy.draw(game.batch);
        for(Item item : items)
            item.draw(game.batch);
        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if(gameOver()) {
            game.assets.get("audio/music/mario_music.ogg", Music.class).stop();
            game.setScreen(new GameOverScreen(game));
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
        b2ddr.dispose();
        hud.dispose();
    }
}
