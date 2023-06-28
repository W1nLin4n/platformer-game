package com.mygdx.platformer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.platformer.Platformer;
import com.mygdx.platformer.tools.Constants;

public class MenuScreen implements Screen {
    private Viewport viewport;
    private OrthographicCamera camera;
    private Stage stage;
    private Platformer game;
    private Label menuLabel;
    private TextButton startGame;
    private TextButton exitGame;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    public MenuScreen(Platformer game) {
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new FitViewport(Constants.V_WIDTH, Constants.V_HEIGHT, camera);
        viewport.apply();
        stage = new Stage(viewport, game.batch);
        Gdx.input.setInputProcessor(stage);

        map = game.assets.get("load_and_menu_bg.tmx", TiledMap.class);
        renderer = new OrthogonalTiledMapRenderer(map, 1);

        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = new BitmapFont();
        buttonStyle.fontColor = Color.WHITE;

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        menuLabel = new Label("MENU", font);
        startGame = new TextButton("START GAME", buttonStyle);
        startGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setGameState(Platformer.GameState.LEVEL1);
            }
        });
        exitGame = new TextButton("EXIT", buttonStyle);
        exitGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        styleTextButton(startGame);
        styleTextButton(exitGame);

        table.add(menuLabel).expandX();
        table.row();
        table.add(startGame).padTop(45).width(120).height(25);
        table.row();
        table.add(exitGame).padTop(20).width(120).height(25);

        stage.addActor(table);
    }

    public void styleTextButton(TextButton button) {
        button.setWidth(120);
        button.setHeight(25);
        Pixmap up = new Pixmap((int) button.getWidth(), (int) button.getHeight(), Pixmap.Format.RGBA8888);
        up.setColor(Color.BROWN);
        up.fill();
        up.setColor(Color.ORANGE);
        up.fillRectangle(2, 2, (int) (button.getWidth() - 4), (int) (button.getHeight() - 4));

        button.getStyle().up = new TextureRegionDrawable(new TextureRegion(new Texture(up)));

        up.dispose();
    }

    public void update(float delta) {
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
