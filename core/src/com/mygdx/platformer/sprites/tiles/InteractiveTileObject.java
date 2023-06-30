package com.mygdx.platformer.sprites.tiles;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.platformer.screens.GameScreen;
import com.mygdx.platformer.sprites.Interactive;
import com.mygdx.platformer.sprites.players.Player;
import com.mygdx.platformer.tools.Constants;

public abstract class InteractiveTileObject implements Interactive {
    protected World world;
    protected TiledMap map;
    protected MapObject object;
    protected Rectangle bounds;
    protected Body body;
    protected GameScreen screen;
    public InteractiveTileObject(MapObject object, GameScreen screen) {
        this.world = screen.getWorld();
        this.map = screen.getMap();
        this.object = object;
        this.bounds = ((RectangleMapObject) object).getRectangle();
        this.screen = screen;

        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        EdgeShape shape = new EdgeShape();

        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(Constants.toMeters(bounds.getX() + bounds.getWidth()/2), Constants.toMeters(bounds.getY() + bounds.getHeight()/2));

        body = world.createBody(bodyDef);

        shape.set(new Vector2(Constants.toMeters(-bounds.getWidth()/2), Constants.toMeters(bounds.getHeight()/2)), new Vector2(Constants.toMeters(bounds.getWidth()/2), Constants.toMeters(bounds.getHeight()/2)));
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);
        shape.set(new Vector2(Constants.toMeters(-bounds.getWidth()/2), Constants.toMeters(-bounds.getHeight()/2)), new Vector2(Constants.toMeters(bounds.getWidth()/2), Constants.toMeters(-bounds.getHeight()/2)));
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);
        shape.set(new Vector2(Constants.toMeters(-bounds.getWidth()/2), Constants.toMeters(bounds.getHeight()/4)), new Vector2(Constants.toMeters(-bounds.getWidth()/2), Constants.toMeters(-bounds.getHeight()/4)));
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);
        shape.set(new Vector2(Constants.toMeters(bounds.getWidth()/2), Constants.toMeters(bounds.getHeight()/4)), new Vector2(Constants.toMeters(bounds.getWidth()/2), Constants.toMeters(-bounds.getHeight()/4)));
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);
    }

    public abstract void hit(Object o, Vector2 normal);

    public void setCategoryFilter(short filterBit) {
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        for(Fixture fixture : body.getFixtureList()) {
            fixture.setFilterData(filter);
        }
    }

    public TiledMapTileLayer.Cell getCell() {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("Graphic Layer");
        return layer.getCell((int)(Constants.toPixels(body.getPosition().x / 16)), (int)(Constants.toPixels(body.getPosition().y / 16)));
    }
}
