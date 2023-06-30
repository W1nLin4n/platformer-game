package com.mygdx.platformer.tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.platformer.screens.levels.Level;
import com.mygdx.platformer.sprites.Trigger;
import com.mygdx.platformer.sprites.enemies.Enemy;
import com.mygdx.platformer.sprites.enemies.Nezarakh;
import com.mygdx.platformer.sprites.enemies.Flipper;
import com.mygdx.platformer.sprites.tiles.Brick;
import com.mygdx.platformer.sprites.tiles.Coin;

public class B2DWorldCreator {
    private Array<Nezarakh> nezarakhs;
    private Array<Flipper> flippers;

    public B2DWorldCreator(Level screen) {
        TiledMap map = screen.getMap();
        World world = screen.getWorld();
        BodyDef bodyDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();
        Body body;

        // Ground creation
        for(MapObject object : map.getLayers().get("Ground").getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(Constants.toMeters(rect.getX() + rect.getWidth()/2), Constants.toMeters(rect.getY() + rect.getHeight()/2));

            body = world.createBody(bodyDef);

            shape.setAsBox(Constants.toMeters(rect.getWidth()/2), Constants.toMeters(rect.getHeight()/2));
            fixtureDef.shape = shape;
            fixtureDef.filter.categoryBits = Constants.GROUND_BIT;
            body.createFixture(fixtureDef).setUserData("GROUND");
        }

        // Pipes creation
        for(MapObject object : map.getLayers().get("Pipes").getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(Constants.toMeters(rect.getX() + rect.getWidth()/2), Constants.toMeters(rect.getY() + rect.getHeight()/2));

            body = world.createBody(bodyDef);

            shape.setAsBox(Constants.toMeters(rect.getWidth()/2), Constants.toMeters(rect.getHeight()/2));
            fixtureDef.shape = shape;
            fixtureDef.filter.categoryBits = Constants.OBJECT_BIT;
            body.createFixture(fixtureDef).setUserData("PIPE");
        }

        // Coins creation
        for(MapObject object : map.getLayers().get("Coins").getObjects().getByType(RectangleMapObject.class)){
            new Coin(object, screen);
        }

        // Bricks creation
        for(MapObject object : map.getLayers().get("Bricks").getObjects().getByType(RectangleMapObject.class)){
            new Brick(object, screen);
        }

        // Nezarakhs creation
        nezarakhs = new Array<>();
        for(MapObject object : map.getLayers().get("Goombas").getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            nezarakhs.add(new Nezarakh(screen, Constants.toMeters(rect.getX()), Constants.toMeters(rect.getY())));
        }

        // Flippers creation
        flippers = new Array<>();
        for(MapObject object : map.getLayers().get("Turtles").getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            flippers.add(new Flipper(screen, Constants.toMeters(rect.getX()), Constants.toMeters(rect.getY())));
        }

        // Triggers creation
        for(MapObject object : map.getLayers().get("Triggers").getObjects().getByType(RectangleMapObject.class)){
            new Trigger(object, screen);
        }
    }

    public Array<Enemy> getEnemies() {
        Array<Enemy> enemies = new Array<>();
        enemies.addAll(nezarakhs);
        enemies.addAll(flippers);
        return enemies;
    }
}
