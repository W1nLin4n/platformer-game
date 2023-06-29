package com.mygdx.platformer.tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.platformer.screens.GameScreen;
import com.mygdx.platformer.sprites.enemies.Enemy;
import com.mygdx.platformer.sprites.enemies.Nezarakh;
import com.mygdx.platformer.sprites.enemies.Flipper;
import com.mygdx.platformer.sprites.tiles.Brick;
import com.mygdx.platformer.sprites.tiles.Coin;

public class B2DWorldCreator {
    private Array<Nezarakh> goombas;
    private Array<Flipper> turtles;

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
            bodyDef.position.set(Constants.toMeters(rect.getX() + rect.getWidth()/2), Constants.toMeters(rect.getY() + rect.getHeight()/2));

            body = world.createBody(bodyDef);

            shape.setAsBox(Constants.toMeters(rect.getWidth()/2), Constants.toMeters(rect.getHeight()/2));
            fixtureDef.shape = shape;
            fixtureDef.filter.categoryBits = Constants.GROUND_BIT;
            body.createFixture(fixtureDef).setUserData("GROUND");
        }

        // Pipes creation
        for(MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
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
            goombas.add(new Nezarakh(screen, Constants.toMeters(rect.getX()), Constants.toMeters(rect.getY())));
        }

        // Turtles creation
        turtles = new Array<>();
        for(MapObject object : map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            turtles.add(new Flipper(screen, Constants.toMeters(rect.getX()), Constants.toMeters(rect.getY())));
        }
    }

    public Array<Enemy> getEnemies() {
        Array<Enemy> enemies = new Array<>();
        enemies.addAll(goombas);
        enemies.addAll(turtles);
        return enemies;
    }
}
