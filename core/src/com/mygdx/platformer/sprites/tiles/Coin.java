package com.mygdx.platformer.sprites.tiles;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.platformer.screens.GameScreen;
import com.mygdx.platformer.sprites.items.ItemDef;
import com.mygdx.platformer.sprites.items.Mushroom;
import com.mygdx.platformer.sprites.players.Player;
import com.mygdx.platformer.tools.Constants;

public class Coin extends InteractiveTileObject {
    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28;

    public Coin(MapObject object, GameScreen screen) {
        super(object, screen);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(Constants.COIN_BIT);
    }

    @Override
    public void onHeadHit(Player player) {
        if (!getCell().getTile().equals(tileSet.getTile(BLANK_COIN))) {
            getCell().setTile(tileSet.getTile(BLANK_COIN));
            screen.getHud().addScore(200);
            if(object.getProperties().containsKey("mushroom")) {
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + Constants.toMeters(16)), Mushroom.class));
                screen.getGame().assets.get("audio/sounds/powerup_spawn.wav", Sound.class).play();
            } else {
                screen.getGame().assets.get("audio/sounds/coin.wav", Sound.class).play();
            }
        } else {
            screen.getGame().assets.get("audio/sounds/breakblock.wav", Sound.class).play();
        }
    }
}
