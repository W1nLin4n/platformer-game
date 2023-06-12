package com.mygdx.platformer.sprite;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.platformer.Platformer;
import com.mygdx.platformer.scene.Hud;
import com.mygdx.platformer.screen.GameScreen;

public class Coin extends InteractiveTileObject{
    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28;

    public Coin(MapObject object, GameScreen screen) {
        super(object, screen);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(Platformer.COIN_BIT);
    }

    @Override
    public void onHeadHit(Player player) {
        if (!getCell().getTile().equals(tileSet.getTile(BLANK_COIN))) {
            getCell().setTile(tileSet.getTile(BLANK_COIN));
            screen.getHud().addScore(200);
            if(object.getProperties().containsKey("mushroom")) {
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + Platformer.toMeters(16)), Mushroom.class));
                screen.getGame().assetManager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();
            } else {
                screen.getGame().assetManager.get("audio/sounds/coin.wav", Sound.class).play();
            }
        } else {
            screen.getGame().assetManager.get("audio/sounds/breakblock.wav", Sound.class).play();
        }
    }
}
