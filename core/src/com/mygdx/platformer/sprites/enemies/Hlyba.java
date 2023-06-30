package com.mygdx.platformer.sprites.enemies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.platformer.screens.levels.Level;
import com.mygdx.platformer.sprites.players.Player;
import com.mygdx.platformer.tools.Constants;

public class Hlyba extends Sprite {
    private World world;
    private Level screen;
    public Body b2dbody;
    private boolean fromLeft;

    public Hlyba(Level screen, boolean fromLeft) {
        this.world = screen.getWorld();
        this.screen = screen;
        this.fromLeft = fromLeft;

        setBounds(0, 0, Constants.toMeters(screen.getWorldHeight() * 0.75f), Constants.toMeters(screen.getWorldHeight() * 1f));
        if(fromLeft)
            setPosition(-Constants.toMeters(screen.getWorldHeight() * 0.75f), Constants.toMeters(screen.getWorldHeight() * 0.5f));
        else
            setPosition(Constants.toMeters(screen.getWorldWidth()), Constants.toMeters(screen.getWorldHeight() * 0.5f));
        setRegion(new TextureRegion(screen.getAtlas().findRegion("hlyba"), 0, 0, 42, 56));

        defineHlyba();
    }

    public void update(float delta) {
        setPosition(b2dbody.getPosition().x - getWidth()/2, b2dbody.getPosition().y - getHeight()/2);
    }

    private void defineHlyba() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        b2dbody = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(getBoundingRectangle().getWidth()/2, getBoundingRectangle().getHeight()/2);
        fixtureDef.filter.categoryBits = Constants.HLYBA_BIT;
        fixtureDef.filter.maskBits = Constants.PLAYER_BIT;
        fixtureDef.shape = shape;
        b2dbody.createFixture(fixtureDef).setUserData(this);

        b2dbody.setLinearVelocity(new Vector2(fromLeft ? 1 : -1, 0));
    }
}
