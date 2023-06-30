package com.mygdx.platformer.sprites.tiles;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.mygdx.platformer.screens.levels.Level;
import com.mygdx.platformer.sprites.players.Player;
import com.mygdx.platformer.tools.Constants;

public class Brick extends InteractiveTileObject {
    public Brick(MapObject object, Level screen) {
        super(object, screen);
        for(Fixture fixture : body.getFixtureList()) {
            fixture.setUserData(this);
        }
        setCategoryFilter(Constants.BRICK_BIT);
    }

    @Override
    public void hit(Object o, Vector2 normal) {
        if(o == null)
            return;
        if(o instanceof Player) {
            Player player = (Player) o;
            Body playerBody = player.getBody();

            if(playerBody.getPosition().y < body.getPosition().y && Math.abs(playerBody.getPosition().x - body.getPosition().x) < Constants.toMeters(bounds.getWidth()/2)) {
                setCategoryFilter(Constants.DESTROYED_BIT);
                getCell().setTile(null);
                screen.getHud().addScore(100);
                screen.getGame().assets.get("audio/sounds/bump.wav", Sound.class).play();
            }
        }
    }
}
