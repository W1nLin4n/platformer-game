package com.mygdx.platformer.sprites.tiles;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.mygdx.platformer.screens.GameScreen;
import com.mygdx.platformer.sprites.players.Player;
import com.mygdx.platformer.tools.Constants;

public class Brick extends InteractiveTileObject {
    public Brick(MapObject object, GameScreen screen) {
        super(object, screen);
        fixture.setUserData(this);
        setCategoryFilter(Constants.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Player player) {
        if(player.isBig()) {
            setCategoryFilter(Constants.DESTROYED_BIT);
            getCell().setTile(null);
            screen.getHud().addScore(100);
            screen.getGame().assets.get("audio/sounds/breakblock.wav", Sound.class).play();
        } else {
            screen.getGame().assets.get("audio/sounds/bump.wav", Sound.class).play();
        }
    }
}
