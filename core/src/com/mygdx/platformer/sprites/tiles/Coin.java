package com.mygdx.platformer.sprites.tiles;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.mygdx.platformer.screens.GameScreen;
import com.mygdx.platformer.sprites.players.Player;
import com.mygdx.platformer.tools.Constants;

public class Coin extends InteractiveTileObject {
    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28;

    public Coin(MapObject object, GameScreen screen) {
        super(object, screen);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        for(Fixture fixture : body.getFixtureList()) {
            fixture.setUserData(this);
        }
        setCategoryFilter(Constants.COIN_BIT);
    }

    @Override
    public void hit(Object o, Vector2 normal) {
        if(o == null)
            return;
        if(o instanceof Player) {
            Player player = (Player) o;
            Body playerBody = player.getBody();

            // If player's head hit coin block
            if(playerBody.getPosition().y < body.getPosition().y && Math.abs(playerBody.getPosition().x - body.getPosition().x) < Constants.toMeters(bounds.getWidth()/2)) {
                if (!getCell().getTile().equals(tileSet.getTile(BLANK_COIN))) {
                    getCell().setTile(tileSet.getTile(BLANK_COIN));
                    screen.getHud().addScore(200);
                    screen.getGame().assets.get("audio/sounds/coin.wav", Sound.class).play();
                } else {
                    screen.getGame().assets.get("audio/sounds/breakblock.wav", Sound.class).play();
                }
            }
        }
    }
}
