package com.mygdx.platformer.tool;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.platformer.Platformer;
import com.mygdx.platformer.screen.GameScreen;
import com.mygdx.platformer.sprite.*;

public class B2DWorldCreator {
    private Array<Goomba> goombas;
    private Array<Turtle> turtles;

    public B2DWorldCreator(GameScreen screen) {
        TiledMap map = screen.getMap();
        World world = screen.getWorld();
        BodyDef bodyDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();
        Body body;

        // Ground creation
        for(MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(Platformer.toMeters(rect.getX() + rect.getWidth()/2), Platformer.toMeters(rect.getY() + rect.getHeight()/2));

            body = world.createBody(bodyDef);

            shape.setAsBox(Platformer.toMeters(rect.getWidth()/2), Platformer.toMeters(rect.getHeight()/2));
            fixtureDef.shape = shape;
            fixtureDef.filter.categoryBits = Platformer.GROUND_BIT;
            body.createFixture(fixtureDef);
        }

        // Pipes creation
        for(MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(Platformer.toMeters(rect.getX() + rect.getWidth()/2), Platformer.toMeters(rect.getY() + rect.getHeight()/2));

            body = world.createBody(bodyDef);

            shape.setAsBox(Platformer.toMeters(rect.getWidth()/2), Platformer.toMeters(rect.getHeight()/2));
            fixtureDef.shape = shape;
            fixtureDef.filter.categoryBits = Platformer.OBJECT_BIT;
            body.createFixture(fixtureDef);
        }

        // Coins creation
        for(MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){
            new Coin(object, screen);
        }

        // Bricks creation
        for(MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){
            new Brick(object, screen);
        }

        // Goombas creation
        goombas = new Array<>();
        for(MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            goombas.add(new Goomba(screen, Platformer.toMeters(rect.getX()), Platformer.toMeters(rect.getY())));
        }

        // Turtles creation
        turtles = new Array<>();
        for(MapObject object : map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            turtles.add(new Turtle(screen, Platformer.toMeters(rect.getX()), Platformer.toMeters(rect.getY())));
        }
    }

    public Array<Enemy> getEnemies() {
        Array<Enemy> enemies = new Array<>();
        enemies.addAll(goombas);
        enemies.addAll(turtles);
        return enemies;
    }
}
