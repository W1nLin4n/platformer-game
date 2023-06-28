package com.mygdx.platformer.sprites.enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.platformer.screens.GameScreen;
import com.mygdx.platformer.sprites.players.Player;
import com.mygdx.platformer.tools.Constants;

public class Goomba extends Enemy {
    private float stateTime;
    private Animation<TextureRegion> walkAnimation;
    private Array<TextureRegion> frames;

    public Goomba(GameScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<>();
        for(int i = 0; i < 2; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("goomba"), i*16, 0, 16, 16));
        }
        walkAnimation = new Animation<>(0.4f, frames);
        frames.clear();
        stateTime = 0;
        setBounds(getX(), getY(), Constants.toMeters(16), Constants.toMeters(16));
    }

    public void update(float delta) {
        stateTime += delta;
        if(toDestroy && !destroyed) {
            world.destroyBody(b2dbody);
            destroyed = true;
            setRegion(screen.getAtlas().findRegion("goomba"), 32, 0, 16, 16);
            screen.getHud().addScore(200);
            stateTime = 0;
        } else if (!destroyed) {
            b2dbody.setLinearVelocity(velocity);
            setPosition(b2dbody.getPosition().x - getWidth()/2, b2dbody.getPosition().y - getHeight()/2);
            setRegion(walkAnimation.getKeyFrame(stateTime, true));
        }
    }

    @Override
    public void draw(Batch batch) {
        if(!destroyed || stateTime < 1) {
            super.draw(batch);
        }
    }

    @Override
    protected void defineEnemy() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2dbody = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(Constants.toMeters(7));
        fixtureDef.filter.categoryBits = Constants.ENEMY_BIT;
        fixtureDef.filter.maskBits =
                Constants.GROUND_BIT |
                        Constants.PLAYER_BIT |
                        Constants.BRICK_BIT |
                        Constants.COIN_BIT |
                        Constants.ENEMY_BIT |
                        Constants.OBJECT_BIT;
        fixtureDef.shape = shape;
        b2dbody.createFixture(fixtureDef).setUserData(this);

        PolygonShape head = new PolygonShape();
        Vector2[] vertices = new Vector2[4];
        vertices[0] = new Vector2(Constants.toMeters(-3), Constants.toMeters(7));
        vertices[1] = new Vector2(Constants.toMeters(-6), Constants.toMeters(12));
        vertices[2] = new Vector2(Constants.toMeters(6), Constants.toMeters(12));
        vertices[3] = new Vector2(Constants.toMeters(3), Constants.toMeters(7));
        head.set(vertices);
        fixtureDef.shape = head;
        fixtureDef.restitution = 1.5f;
        fixtureDef.filter.categoryBits = Constants.ENEMY_HEAD_BIT;
        b2dbody.createFixture(fixtureDef).setUserData(this);
    }

    @Override
    public void hitOnHead(Player player) {
        toDestroy = true;
        screen.getGame().assets.get("audio/sounds/stomp.wav", Sound.class).play();
    }

    @Override
    public void onEnemyHit(Enemy enemy) {
        if(enemy instanceof Turtle && ((Turtle) enemy).currentState == Turtle.State.MOVING_SHELL)
            toDestroy = true;
        else
            reverseVelocity(true, false);
    }
}
