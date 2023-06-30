package com.mygdx.platformer.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.platformer.tools.Constants;

public class DialogBox implements Disposable {
    public Stage stage;
    private Viewport viewport;
    private Array<String> phrases;
    private int currentPhrase;
    private Label currentPhraseLabel;
    public boolean dialogFinished;

    public DialogBox(SpriteBatch sb, Array<String> phrases){
        this.phrases = phrases;
        currentPhrase = 0;
        dialogFinished = false;

        viewport = new FitViewport(Constants.V_WIDTH, Constants.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.bottom();
        table.setWidth(Constants.V_WIDTH);
        table.setHeight(Constants.V_HEIGHT * 0.4f);
        currentPhraseLabel = new Label(phrases.get(currentPhrase), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        currentPhraseLabel.setWrap(true);
        Pixmap background = new Pixmap((int)table.getWidth(), (int)table.getHeight(), Pixmap.Format.RGBA8888);
        background.setColor(Color.rgba8888(Color.ORANGE.r, Color.ORANGE.g, Color.ORANGE.b, 0.7f));
        background.fill();
        background.setColor(Color.BROWN);
        background.fillRectangle(0, 0, Constants.V_WIDTH, (int) (Constants.V_HEIGHT * 0.02f));
        table.background(new TextureRegionDrawable(new Texture(background)));
        table.add(currentPhraseLabel).width(Constants.V_WIDTH - 20).padLeft(10).padTop(10).align(Align.top).expandY();

        stage.addActor(table);
    }

    public void update(float delta) {
        if(Gdx.input.justTouched()) {
            currentPhrase++;
            if(currentPhrase < phrases.size) {
                currentPhraseLabel.setText(phrases.get(currentPhrase));
            } else {
                dialogFinished = true;
            }
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
