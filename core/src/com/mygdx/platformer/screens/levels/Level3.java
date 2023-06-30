package com.mygdx.platformer.screens.levels;

import com.badlogic.gdx.utils.Array;
import com.mygdx.platformer.Platformer;
import com.mygdx.platformer.scenes.DialogBox;
import com.mygdx.platformer.sprites.enemies.Hlyba;
import com.mygdx.platformer.sprites.players.Princess;
import com.mygdx.platformer.tools.Constants;

public class Level3 extends Level {
    private boolean endGameMario;
    private boolean endGameLuigi;
    private boolean openingDialogShown;
    private boolean endingDialogShown;
    public Level3(Platformer game) {
        super(game, "level3.tmx");
        endGameMario = false;
        endGameLuigi = false;
        openingDialogShown = false;
        endingDialogShown = false;
        princess = new Princess(this);
        hud.setLevel("1-3");
    }

    @Override
    public void render(float delta) {
        if(hud.timeLeft() < 300 && !openingDialogShown) {
            dialogBox = new DialogBox(game.batch, new Array<>(new String[]{
                    "Artem\n" +
                    "Here we are in the first building, but don't stop, we need to flee quickly."}));
            hlyba = new Hlyba(this, true);
            openingDialogShown = true;
        }
        if(endGameMario && endGameLuigi && !endingDialogShown) {
            dialogBox = new DialogBox(game.batch, new Array<>(new String[]{
                    "Together\n" +
                    "Olena Mykolaivna!!!! SAVE!!!! We don't want a nezarakh!!!!!\n" +
                    "Pechka\n" +
                    "Hey guys, I understand everything, but the deadline was already 3 weeks ago ((",
                    "Hlyba\n" +
                    "Olena Mykolaivna, it's good that you caught them, now I will conduct an oral exam...",
                    "Pechka\n" +
                    "Andrii Mykolayovych, calm down, the boys are trying, studying, let's show a little mercy)",
                    "Hlyba\n" +
                    "Ehhh, do as you wish, it's not interesting with you...",
                    "Together\n" +
                    "Thank you, you saved us from a terrible situation...",
                    "Pechka\n" +
                    "This was the last time, guys!!! Now let's go to the class."}));
            endingDialogShown = true;
        }
        super.render(delta);
        if(endGameMario && endGameLuigi && dialogBox == null)
            game.setGameState(Platformer.GameState.MENU);
    }

    @Override
    public void triggerEvent(Object o, String event) {
        if(event.equals("end_game")) {
            if(o.equals(player1))
                endGameMario = true;
            if(o.equals(player2))
                endGameLuigi = true;
        }
    }
}
