package com.mygdx.platformer.sprite;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.platformer.Platformer;
import com.mygdx.platformer.screen.GameScreen;

public abstract class InteractiveTileObject {
    protected World world;
    protected TiledMap map;
    protected MapObject object;
    protected Rectangle bounds;
    protected Body body;
    protected Fixture fixture;
    protected GameScreen screen;
    public InteractiveTileObject(MapObject object, GameScreen screen) {
        this.world = screen.getWorld();
        this.map = screen.getMap();
        this.object = object;
        this.bounds = ((RectangleMapObject) object).getRectangle();
        this.screen = screen;

        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(Platformer.toMeters(bounds.getX() + bounds.getWidth()/2), Platformer.toMeters(bounds.getY() + bounds.getHeight()/2));

        body = world.createBody(bodyDef);

        shape.setAsBox(Platformer.toMeters(bounds.getWidth()/2), Platformer.toMeters(bounds.getHeight()/2));
        fixtureDef.shape = shape;
        fixture = body.createFixture(fixtureDef);
    }

    public abstract void onHeadHit(Player player);

    public void setCategoryFilter(short filterBit) {
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }

    public TiledMapTileLayer.Cell getCell() {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);
        return layer.getCell((int)(Platformer.toPixels(body.getPosition().x / 16)), (int)(Platformer.toPixels(body.getPosition().y / 16)));
    }
}
