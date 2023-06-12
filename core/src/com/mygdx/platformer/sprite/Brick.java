package com.mygdx.platformer.sprite;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.platformer.Platformer;
import com.mygdx.platformer.scene.Hud;
import com.mygdx.platformer.screen.GameScreen;

public class Brick extends InteractiveTileObject {
    public Brick(MapObject object, GameScreen screen) {
        super(object, screen);
        fixture.setUserData(this);
        setCategoryFilter(Platformer.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Player player) {
        if(player.isBig()) {
            setCategoryFilter(Platformer.DESTROYED_BIT);
            getCell().setTile(null);
            screen.getHud().addScore(100);
            screen.getGame().assetManager.get("audio/sounds/breakblock.wav", Sound.class).play();
        } else {
            screen.getGame().assetManager.get("audio/sounds/bump.wav", Sound.class).play();
        }
    }
}
