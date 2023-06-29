package com.mygdx.platformer.sprites.players;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.platformer.screens.GameScreen;
import com.mygdx.platformer.sprites.Interactive;
import com.mygdx.platformer.sprites.enemies.Enemy;
import com.mygdx.platformer.tools.Constants;

public class Ball extends Sprite implements Interactive {
    private GameScreen screen;
    private World world;
    public Body body;
    private Vector2 spawnPosition;
    private float timeAlive;
    private Animation<TextureRegion> spin;
    private Array<TextureRegion> frames;
    private boolean toDestroy;
    private boolean destroyed;

    public Ball(GameScreen screen, Vector2 position, boolean right) {
        this.screen = screen;
        this.world = screen.getWorld();
        spawnPosition = position;
        timeAlive = 0;
        toDestroy = false;
        destroyed = false;

        frames = new Array<>();
        for (int i = 0; i < 4; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("ball"), i * 8, 0, 8, 8));
        }
        spin = new Animation<>(0.1f, frames);
        frames.clear();

        defineBall();
        setBounds(0, 0, Constants.toMeters(8), Constants.toMeters(8));
        setPosition(position.x, position.y);

        System.out.println(right);
        body.applyLinearImpulse(new Vector2(right ? 2f : -2f, 2f), body.getWorldCenter(), true);
    }

    private TextureRegion getFrame() {
        return spin.getKeyFrame(timeAlive, true);
    }

    public void update(float delta) {
        timeAlive += delta;
        if (timeAlive > 3f) {
            toDestroy = true;
        }
        if (toDestroy && !destroyed) {
            world.destroyBody(body);
            destroyed = true;
        } else if (!destroyed) {
            setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
            setRegion(getFrame());
        }
    }

    @Override
    public void draw(Batch batch) {
        if (!destroyed)
            super.draw(batch);
    }

    private void defineBall() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(spawnPosition);
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.restitution = 1f;
        fixtureDef.friction = 0;
        CircleShape shape = new CircleShape();
        shape.setRadius(Constants.toMeters(4));
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = Constants.BALL_BIT;
        fixtureDef.filter.maskBits =
                Constants.GROUND_BIT |
                        Constants.BRICK_BIT |
                        Constants.COIN_BIT |
                        Constants.DESTROYED_BIT |
                        Constants.OBJECT_BIT |
                        Constants.ENEMY_BIT;
        body.createFixture(fixtureDef).setUserData(this);
    }

    @Override
    public void hit(Object o, Vector2 normal) {
        if (o == null)
            return;
        else if (o instanceof Enemy)
            toDestroy = true;
    }
}
