package com.mygdx.platformer.sprites.enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.platformer.screens.GameScreen;
import com.mygdx.platformer.sprites.WorldBox;
import com.mygdx.platformer.sprites.players.Ball;
import com.mygdx.platformer.sprites.players.Player;
import com.mygdx.platformer.tools.Constants;

public class Nezarakh extends Enemy {
    private float stateTime;
    private Animation<TextureRegion> walkAnimation;
    private Array<TextureRegion> frames;

    public Nezarakh(GameScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<>();
        for(int i = 0; i < 2; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("nezarakh"), i*64, 0, 64, 64));
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
            setRegion(new TextureRegion(screen.getAtlas().findRegion("nezarakh"), 128, 0, 64, 64));
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
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(Constants.toMeters(7), Constants.toMeters(7));
        fixtureDef.filter.categoryBits = Constants.ENEMY_BIT;
        fixtureDef.filter.maskBits =
                Constants.GROUND_BIT |
                Constants.PLAYER_BIT |
                Constants.BRICK_BIT |
                Constants.COIN_BIT |
                Constants.ENEMY_BIT |
                Constants.OBJECT_BIT |
                Constants.WORLD_BOX_BIT |
                Constants.BALL_BIT;
        fixtureDef.shape = shape;
        b2dbody.createFixture(fixtureDef).setUserData(this);
    }

    @Override
    public void hit(Object o, Vector2 normal) {
        if(o == null)
            return;
        else if(o instanceof WorldBox) {
            reverseVelocity(true, false);
        }
        else if(o instanceof String && o.equals("GROUND")) {
            if(normal.y == 0) {
                reverseVelocity(true, false);
            }
        }
        else if(o instanceof String && o.equals("PIPE")) {
            if(normal.y == 0) {
                reverseVelocity(true, false);
            }
        }
        else if(o instanceof Enemy) {
            if(o instanceof Flipper && ((Flipper) o).currentState == Flipper.State.MOVING_SHELL)
                toDestroy = true;
            else
                reverseVelocity(true, false);
        }
        else if(o instanceof Player) {
            Player player = (Player) o;
            Body playerBody = player.getBody();
            if(playerBody.getPosition().y > b2dbody.getPosition().y && Math.abs(playerBody.getPosition().x - b2dbody.getPosition().x) < Constants.toMeters(13)) {
                toDestroy = true;
                screen.getGame().assets.get("audio/sounds/stomp.wav", Sound.class).play();
            }
        } else if(o instanceof Ball) {
            toDestroy = true;
        }
    }

    public void hitOnHead(Player player) {
        toDestroy = true;
        screen.getGame().assets.get("audio/sounds/stomp.wav", Sound.class).play();
    }

    public void onEnemyHit(Enemy enemy) {
        if(enemy instanceof Flipper && ((Flipper) enemy).currentState == Flipper.State.MOVING_SHELL)
            toDestroy = true;
        else
            reverseVelocity(true, false);
    }
}
