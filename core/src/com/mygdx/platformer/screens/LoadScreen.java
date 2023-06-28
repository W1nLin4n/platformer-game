package com.mygdx.platformer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.platformer.Platformer;
import com.mygdx.platformer.tools.Constants;

public class LoadScreen implements Screen {
    private Viewport viewport;
    private OrthographicCamera camera;
    private Stage stage;
    private Platformer game;
    private boolean loaded;
    private float afterLoadDelay;
    private Label loadingLabel;
    private ProgressBar progressBar;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    public LoadScreen(Platformer game) {
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new FitViewport(Constants.V_WIDTH, Constants.V_HEIGHT, camera);
        viewport.apply();
        stage = new Stage(viewport, game.batch);
        loaded = false;
        afterLoadDelay = 0.5f;

        map = game.assets.get("load_and_menu_bg.tmx", TiledMap.class);
        renderer = new OrthogonalTiledMapRenderer(map, 1);

        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        loadingLabel = new Label("LOADING GAME", font);
        progressBar = new ProgressBar(0, 1, 0.01f, false, new ProgressBar.ProgressBarStyle());

        table.add(loadingLabel).expandX();
        table.row();
        table.add(progressBar).expandX().padTop(10);
        styleProgressBar(progressBar);

        stage.addActor(table);
    }

    private void styleProgressBar(ProgressBar progressBar) {
        progressBar.setHeight(10);
        Pixmap background = new Pixmap((int)progressBar.getWidth(), (int)progressBar.getHeight(), Pixmap.Format.RGBA8888);
        background.setColor(Color.WHITE);
        background.fill();
        progressBar.getStyle().background = new TextureRegionDrawable(new TextureRegion(new Texture(background)));

        Pixmap knobAfter = new Pixmap((int)progressBar.getWidth(), (int)(progressBar.getHeight() - progressBar.getHeight() * 0.2f), Pixmap.Format.RGBA8888);
        knobAfter.setColor(Color.BLACK);
        knobAfter.fill();
        progressBar.getStyle().knobAfter = new TextureRegionDrawable(new TextureRegion(new Texture(knobAfter)));

        Pixmap knob = new Pixmap(0, (int)(progressBar.getHeight() - progressBar.getHeight() * 0.2f), Pixmap.Format.RGBA8888);
        knob.setColor(Color.WHITE);
        knob.fill();
        progressBar.getStyle().knob = new TextureRegionDrawable(new TextureRegion(new Texture(knob)));

        background.dispose();
        knobAfter.dispose();
        knob.dispose();

        progressBar.setAnimateDuration(0.2f);
    }

    public void update(float delta) {
        if(loaded) {
            afterLoadDelay -= delta;
        }
        if(afterLoadDelay <= 0) {
            game.setGameState(Platformer.GameState.MENU);
        }
        if(game.assets.update()) {
            loaded = true;
        }
        progressBar.setValue(game.assets.getProgress());

        renderer.setView(camera);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.render();

        game.batch.setProjectionMatrix(stage.getCamera().combined);

        stage.draw();
        stage.act(delta);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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
        stage.dispose();
    }
}
