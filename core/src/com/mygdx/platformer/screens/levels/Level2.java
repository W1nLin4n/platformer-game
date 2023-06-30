package com.mygdx.platformer.screens.levels;

import com.badlogic.gdx.utils.Array;
import com.mygdx.platformer.Platformer;
import com.mygdx.platformer.scenes.DialogBox;
import com.mygdx.platformer.sprites.enemies.Hlyba;

public class Level2 extends Level {
    private boolean exitedMario;
    private boolean exitedLuigi;
    private boolean openingDialogShown;
    public Level2(Platformer game) {
        super(game, "level2.tmx");
        exitedMario = false;
        exitedLuigi = false;
        openingDialogShown = false;
        hud.setLevel("1-2");
    }
    @Override
    public void render(float delta) {
        if(hud.timeLeft() < 300 && !openingDialogShown) {
            dialogBox = new DialogBox(game.batch, new Array<>(new String[]{
                    "Slavik\n" +
                    "Huh, seems like we escaped...",
                    "Hlyba\n" +
                    "Where are you running? Everybody gonna get nezarakh!!!",
                    "Slavik\n" +
                    "Don't look back, just run!!!"}));
            hlyba = new Hlyba(this, true);
            openingDialogShown = true;
        }
        super.render(delta);
        if(exitedMario && exitedLuigi)
            game.setGameState(Platformer.GameState.LEVEL3);
    }

    @Override
    public void triggerEvent(Object o, String event) {
        if(event.equals("next_level")) {
            if(o.equals(player1))
                exitedMario = true;
            if(o.equals(player2))
                exitedLuigi = true;
        }
    }
}
