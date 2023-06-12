package com.mygdx.platformer.sprite;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.platformer.Platformer;
import com.mygdx.platformer.screen.GameScreen;

public class Turtle extends Enemy {
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

    public Turtle(GameScreen screen, float x, float y) {
        super(screen, x, y);

        frames = new Array<>();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), 0, 0, 16, 24));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), 16, 0, 16, 24));
        shell = new TextureRegion(screen.getAtlas().findRegion("turtle"), 64, 0, 16, 24);
        walkAnimation = new Animation<>(0.2f, frames);
        currentState = previousState = State.WALKING;
        destroyed = false;
        deadRotationDegrees = 0;

        setBounds(getX(), getY(), Platformer.toMeters(16), Platformer.toMeters(24));
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
        CircleShape shape = new CircleShape();
        shape.setRadius(Platformer.toMeters(7));
        fixtureDef.filter.categoryBits = Platformer.ENEMY_BIT;
        fixtureDef.filter.maskBits =
                Platformer.GROUND_BIT |
                        Platformer.PLAYER_BIT |
                        Platformer.BRICK_BIT |
                        Platformer.COIN_BIT |
                        Platformer.ENEMY_BIT |
                        Platformer.OBJECT_BIT;
        fixtureDef.shape = shape;
        b2dbody.createFixture(fixtureDef).setUserData(this);

        PolygonShape head = new PolygonShape();
        Vector2[] vertices = new Vector2[4];
        vertices[0] = new Vector2(Platformer.toMeters(-3), Platformer.toMeters(7));
        vertices[1] = new Vector2(Platformer.toMeters(-6), Platformer.toMeters(12));
        vertices[2] = new Vector2(Platformer.toMeters(6), Platformer.toMeters(12));
        vertices[3] = new Vector2(Platformer.toMeters(3), Platformer.toMeters(7));
        head.set(vertices);
        fixtureDef.shape = head;
        fixtureDef.restitution = 1.5f;
        fixtureDef.filter.categoryBits = Platformer.ENEMY_HEAD_BIT;
        b2dbody.createFixture(fixtureDef).setUserData(this);
    }

    @Override
    public void hitOnHead(Player player) {
        if (currentState != State.STANDING_SHELL) {
            currentState = State.STANDING_SHELL;
            velocity.x = 0;
        } else {
            kick(player.getX() <= this.getX() ? KICK_RIGHT_SPEED : KICK_LEFT_SPEED);
        }
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

        setPosition(b2dbody.getPosition().x - getWidth() / 2, b2dbody.getPosition().y - Platformer.toMeters(8));
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

    public State getCurrentState() {
        return currentState;
    }

    @Override
    public void onEnemyHit(Enemy enemy) {
        if(enemy instanceof Turtle) {
            if(((Turtle) enemy).currentState == State.MOVING_SHELL && currentState != State.MOVING_SHELL) {
                kill();
            }
            else if(currentState == State.MOVING_SHELL && ((Turtle) enemy).currentState == State.WALKING )
                return;
            else
                reverseVelocity(true, false);
        }
        else if (currentState != State.MOVING_SHELL)
            reverseVelocity(true, false);
    }

    public void kill() {
        currentState = State.DEAD;
        Filter filter = new Filter();
        filter.maskBits = Platformer.NOTHING_BIT;
        for(Fixture fixture : b2dbody.getFixtureList())
            fixture.setFilterData(filter);
        b2dbody.applyLinearImpulse(new Vector2(0, 5f), b2dbody.getWorldCenter(), true);
    }
}
