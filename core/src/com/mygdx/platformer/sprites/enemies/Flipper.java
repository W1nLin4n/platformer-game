package com.mygdx.platformer.sprites.enemies;

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

public class Flipper extends Enemy {
    public static final int KICK_LEFT_SPEED = -2;
    public static final int KICK_RIGHT_SPEED = 2;
    public enum State {WALKING, STANDING_SHELL, MOVING_SHELL, DEAD}
    public State currentState;
    public State previousState;
    private float stateTime;
    private Animation<TextureRegion> walkAnimation;
    private TextureRegion shell;
    private Array<TextureRegion> frames;
    private float deadRotationDegrees;
    private boolean destroyed;

    public Flipper(GameScreen screen, float x, float y) {
        super(screen, x, y);

        frames = new Array<>();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("flipper"), 0, 0, 64, 96));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("flipper"), 64, 0, 64, 96));
        shell = new TextureRegion(screen.getAtlas().findRegion("flipper"), 128, 0, 64, 96);
        walkAnimation = new Animation<>(0.2f, frames);
        currentState = previousState = State.WALKING;
        destroyed = false;
        deadRotationDegrees = 0;

        setBounds(getX(), getY(), Constants.toMeters(16), Constants.toMeters(24));
    }

    public TextureRegion getFrame(float delta) {
        TextureRegion region;

        switch (currentState) {
            case STANDING_SHELL:
            case MOVING_SHELL:
                region = shell;
                break;
            case WALKING:
            default:
                region = walkAnimation.getKeyFrame(stateTime, true);
                break;
        }

        if(velocity.x > 0 && !region.isFlipX()) {
            region.flip(true, false);
        }
        if(velocity.x < 0 && region.isFlipX()) {
            region.flip(true, false);
        }
        stateTime = currentState == previousState ? stateTime + delta : 0;
        previousState = currentState;
        return region;
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
    public void draw(Batch batch) {
        if(!destroyed)
            super.draw(batch);
    }

    @Override
    public void update(float delta) {
        setRegion(getFrame(delta));
        if(currentState == State.STANDING_SHELL && stateTime > 5) {
            currentState = State.WALKING;
            velocity.x = 1;
        }

        setPosition(b2dbody.getPosition().x - getWidth() / 2, b2dbody.getPosition().y - Constants.toMeters(8));
        if(currentState == State.DEAD) {
            deadRotationDegrees += 3;
            rotate(deadRotationDegrees);
            if(stateTime > 5 && !destroyed) {
                world.destroyBody(b2dbody);
                destroyed = true;
            }
        } else {
            b2dbody.setLinearVelocity(velocity);
        }
    }

    public void kick(int speed) {
        velocity.x = speed;
        currentState = State.MOVING_SHELL;
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
            if(o instanceof Flipper) {
                if(((Flipper) o).currentState == State.MOVING_SHELL && currentState != State.MOVING_SHELL) {
                    kill();
                }
                else if(currentState == State.MOVING_SHELL && ((Flipper) o).currentState == State.WALKING )
                    return;
                else
                    reverseVelocity(true, false);
            }
            else if (currentState != State.MOVING_SHELL)
                reverseVelocity(true, false);
        }
        else if(o instanceof Player) {
            Player player = (Player) o;
            Body playerBody = player.getBody();
            if(playerBody.getPosition().y > b2dbody.getPosition().y && Math.abs(playerBody.getPosition().x - b2dbody.getPosition().x) < Constants.toMeters(13)) {
                if (currentState != State.STANDING_SHELL) {
                    currentState = State.STANDING_SHELL;
                    velocity.x = 0;
                } else {
                    kick(player.getX() <= this.getX() ? KICK_RIGHT_SPEED : KICK_LEFT_SPEED);
                }
            }
        }
        else if(o instanceof Ball) {
            kill();
        }
    }

    public State getCurrentState() {
        return currentState;
    }

    public void kill() {
        currentState = State.DEAD;
        Filter filter = new Filter();
        filter.maskBits = Constants.NOTHING_BIT;
        for(Fixture fixture : b2dbody.getFixtureList())
            fixture.setFilterData(filter);
        b2dbody.applyLinearImpulse(new Vector2(0, 5f), b2dbody.getWorldCenter(), true);
    }
}
