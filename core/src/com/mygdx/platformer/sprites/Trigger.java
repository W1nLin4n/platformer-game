package com.mygdx.platformer.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.platformer.screens.levels.Level;
import com.mygdx.platformer.tools.Constants;

public class Trigger extends Sprite {
    private World world;
    private TiledMap map;
    private MapObject object;
    private Rectangle bounds;
    private Body body;
    private Level screen;
    public String triggerEvent;

    public Trigger(MapObject object, Level screen) {
        this.world = screen.getWorld();
        this.map = screen.getMap();
        this.object = object;
        this.bounds = ((RectangleMapObject) object).getRectangle();
        this.screen = screen;
        this.triggerEvent = (String) object.getProperties().get("trigger");

        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(Constants.toMeters(bounds.getX() + bounds.getWidth()/2), Constants.toMeters(bounds.getY() + bounds.getHeight()/2));

        body = world.createBody(bodyDef);

        shape.setAsBox(Constants.toMeters(bounds.getWidth()/2), Constants.toMeters(bounds.getHeight()/2));
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = Constants.TRIGGER_BIT;
        fixtureDef.filter.maskBits = Constants.PLAYER_BIT;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData(this);
    }

}
