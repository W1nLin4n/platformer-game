package com.mygdx.platformer.screens.levels;

import com.badlogic.gdx.utils.Array;
import com.mygdx.platformer.Platformer;
import com.mygdx.platformer.scenes.DialogBox;
import com.mygdx.platformer.sprites.enemies.Hlyba;

public class Level1 extends Level {
    private boolean exitOpen;
    private boolean exitedMario;
    private boolean exitedLuigi;
    private boolean openingDialogShown;
    private boolean endingDialogShown;
    public Level1(Platformer game) {
        super(game, "level1.tmx");
        exitOpen = false;
        exitedMario = false;
        exitedLuigi = false;
        openingDialogShown = false;
        endingDialogShown = false;
        hud.setLevel("1-1");
    }

    @Override
    public void render(float delta) {
        if(hud.timeLeft() < 300 && !openingDialogShown) {
            dialogBox = new DialogBox(game.batch, new Array<>(new String[]{
                    "Slavik\n" +
                    "Wow, Artem, I have a very strange feeling(((",
                    "Artem\n" +
                    "Indeed, it feels like we got into some kind of game...",
                    "Slavik\n" +
                    "Then we should tell how to manage our characters.",
                    "Artem\n" +
                    "It's so simple. I use the WASD keys, and you use the arrows))",
                    "Slavik\n" +
                    "And also, it seems to me that as game characters we should have some skills.",
                    "Artem\n" +
                    "Yes, with the SPACE key, I can launch a cool ball that can be used to destroy enemies, and you can run and jump faster to avoid them.",
                    "Slavik\n" +
                    "Wowzie. Seems to me that we are standing for too long. We have to hand in the work until Mr. Hlyba comes up with some test.",
                    "Artem\n" +
                    "Come on, hurry up. Ms. Pechkurova is waiting for you in the first building.",
                    "Slavik\n" +
                    "Wait, this is Lyokha Kuvyrok, I know him from the dorm. He and his friends drink all the time and they definitely won't let us pass the exam.",
                    "Slavik\n" +
                    "Let's be a bit more careful with him.",
                    "Artem\n" +
                    "He looks sad.",
                    "Kuvirok\n" +
                    "Hey guys, let's drink for courage!!!!",
                    "Artem\n" +
                    "And this is the horror of any student - nezarakh. You need to avoid it, or turn it into a zarakh."}));
            openingDialogShown = true;
        }
        if(!endingDialogShown && exitOpen) {
            dialogBox = new DialogBox(game.batch, new Array<>(new String[]{
                    "Slavik\n" +
                    "Here is the first building)",
                    "Artem\n" +
                    "Are you ready?",
                    "Slavik\n" +
                    "Yes but....",
                    "Hlyba\n" +
                    "Here you are, THERE IS NOWHERE TO RUN!!!!",
                    "Slavik\n" +
                    "Oh noooo!!!! This is Mr. Hlyba, let's run faster!!!!",
                    "Artem\n" +
                    "Let's run through the third building!"}));
            hlyba = new Hlyba(this, false);
            endingDialogShown = true;
        }
        if(exitedMario && exitedLuigi)
            game.setGameState(Platformer.GameState.LEVEL2);
        else
            super.render(delta);
    }

    @Override
    public void triggerEvent(Object o, String event) {
        if(event.equals("open_exit")) {
            exitOpen = true;
        } else if(event.equals("next_level") && exitOpen) {
            if(o.equals(player1))
                exitedMario = true;
            if(o.equals(player2))
                exitedLuigi = true;
        }
    }
}
