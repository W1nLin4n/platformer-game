package com.mygdx.platformer.sprites;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.platformer.screens.GameScreen;
import com.mygdx.platformer.tools.Constants;

public class WorldBox {
    private World world;
    private Body body;
    private GameScreen screen;
    public WorldBox(GameScreen screen, float width, float height) {
        this.screen = screen;
        this.world = screen.getWorld();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(Constants.toMeters(0), 0);

        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        EdgeShape shape = new EdgeShape();

        shape.set(new Vector2(0, 0), new Vector2(0, Constants.toMeters(height)));
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = Constants.WORLD_BOX_BIT;
        body.createFixture(fixtureDef).setUserData(this);

        shape.set(new Vector2(0, Constants.toMeters(height)), new Vector2(Constants.toMeters(width), Constants.toMeters(height)));
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = Constants.WORLD_BOX_BIT;
        body.createFixture(fixtureDef).setUserData(this);

        shape.set(new Vector2(Constants.toMeters(width), Constants.toMeters(height)), new Vector2(Constants.toMeters(width), 0));
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = Constants.WORLD_BOX_BIT;
        body.createFixture(fixtureDef).setUserData(this);

        shape.set(new Vector2(Constants.toMeters(width), 0), new Vector2(0, 0));
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = Constants.WORLD_BOX_KILL_BIT;
        body.createFixture(fixtureDef).setUserData(this);
    }
}
