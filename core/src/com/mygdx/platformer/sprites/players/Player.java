package com.mygdx.platformer.sprites.players;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.platformer.screens.levels.Level;
import com.mygdx.platformer.sprites.Interactive;
import com.mygdx.platformer.sprites.Trigger;
import com.mygdx.platformer.sprites.WorldBox;
import com.mygdx.platformer.sprites.enemies.Enemy;
import com.mygdx.platformer.sprites.enemies.Flipper;
import com.mygdx.platformer.sprites.enemies.Hlyba;
import com.mygdx.platformer.sprites.tiles.Brick;
import com.mygdx.platformer.sprites.tiles.Coin;
import com.mygdx.platformer.tools.Constants;

public abstract class Player extends Sprite implements Interactive {
    public enum State {FALLING, JUMPING, STANDING, RUNNING, DEAD}
    public State currentState;
    public State previousState;

    public World world;
    public Body b2dbody;
    protected Vector2 spawnPoint;
    protected Level screen;
    protected String spriteRegion;
    protected TextureRegion playerStand;
    protected Animation<TextureRegion> playerRun;
    protected TextureRegion playerJump;
    protected TextureRegion playerDead;
    protected float stateTimer;
    protected boolean runningRight;
    protected boolean playerIsDead;
    protected boolean canJump;

    public Player(Level screen, String spriteRegion, Vector2 spawnPoint) {
        this.spriteRegion = spriteRegion;
        this.screen = screen;
        this.world = screen.getWorld();
        this.spawnPoint = spawnPoint;
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;
        playerIsDead = false;
        canJump = false;

        Array<TextureRegion> frames = new Array<>();
        for(int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion(spriteRegion), i*32, 0, 32, 64));
        playerRun = new Animation<>(0.1f, frames);
        frames.clear();

        playerJump = new TextureRegion(screen.getAtlas().findRegion(spriteRegion), 160, 0, 32, 64);

        playerStand = new TextureRegion(screen.getAtlas().findRegion(spriteRegion), 0, 0, 32, 64);

        playerDead = new TextureRegion(screen.getAtlas().findRegion(spriteRegion), 480, 0, 32, 64);

        definePlayer();
        setBounds(0, 0, Constants.toMeters(16), Constants.toMeters(32));
        setRegion(playerStand);
    }

    public abstract void handleInput(float delta);

    public Body getBody() {
        return b2dbody;
    }

    public float getStateTimer() {
        return stateTimer;
    }

    public boolean isDead() {
        return playerIsDead;
    }

    public boolean jump() {
        if(canJump) {
            canJump = false;
            return true;
        } else {
            return false;
        }
    }

    public void kill() {
        playerIsDead = true;
        Filter filter = new Filter();
        filter.maskBits = Constants.NOTHING_BIT;
        for(Fixture fixture : b2dbody.getFixtureList())
            fixture.setFilterData(filter);
        b2dbody.applyLinearImpulse(new Vector2(0, 4f), b2dbody.getWorldCenter(), true);
    }

    @Override
    public void hit(Object o, Vector2 normal) {
        if(o == null)
            return;
        else if(o instanceof WorldBox && normal.y > 0) {
            screen.death();
        }
        else if(o instanceof String && (o.equals("GROUND") || o.equals("PIPE")) && normal.y > 0) {
            canJump = true;
        }
        else if(o instanceof Brick) {
            if(normal.y > 0)
                canJump = true;
        }
        else if(o instanceof Coin) {
            if(normal.y > 0)
                canJump = true;
        }
        else if(o instanceof Player) {
            if(normal.y != 0)
                canJump = true;
        }
        else if(o instanceof Enemy) {
            if(b2dbody.getPosition().y > ((Enemy) o).b2dbody.getPosition().y && Math.abs(b2dbody.getPosition().x - ((Enemy) o).b2dbody.getPosition().x) < Constants.toMeters(13)) {
                b2dbody.setLinearVelocity(b2dbody.getLinearVelocity().scl(1, -0.9f));
            }
            else if(o instanceof Flipper && ((Flipper) o).currentState == Flipper.State.STANDING_SHELL) {
                return;
            }
            else {
                screen.death();
            }
        }
        else if(o instanceof Trigger) {
            screen.triggerEvent(this, ((Trigger) o).triggerEvent);
        }
        else if(o instanceof Hlyba) {
            screen.death();
        }
    }

    public void update(float delta) {
        setPosition(b2dbody.getPosition().x - getWidth() / 2, b2dbody.getPosition().y - getHeight() / 2);
        setRegion(getFrame(delta));
    }

    public TextureRegion getFrame(float delta) {
        currentState = getState();

        TextureRegion region;
        switch (currentState) {
            case DEAD:
                region = playerDead;
                break;
            case JUMPING:
                region = playerJump;
                break;
            case RUNNING:
                region = playerRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = playerStand;
                break;
        }

        if((b2dbody.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        } else if ((b2dbody.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }

        stateTimer = currentState == previousState ? stateTimer + delta : 0;
        previousState = currentState;
        return region;
    }

    public State getState() {
        if(playerIsDead)
            return State.DEAD;
        else if(b2dbody.getLinearVelocity().y > 0 || (b2dbody.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            return State.JUMPING;
        else if(b2dbody.getLinearVelocity().y < 0)
            return State.FALLING;
        else if(b2dbody.getLinearVelocity().x != 0)
            return State.RUNNING;
        else
            return State.STANDING;
    }

    public void definePlayer() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(spawnPoint);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2dbody = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(Constants.toMeters(7), Constants.toMeters(14));
        fixtureDef.filter.categoryBits = Constants.PLAYER_BIT;
        fixtureDef.filter.maskBits =
                Constants.GROUND_BIT |
                Constants.BRICK_BIT |
                Constants.COIN_BIT |
                Constants.OBJECT_BIT |
                Constants.ENEMY_BIT |
                Constants.ITEM_BIT |
                Constants.PLAYER_BIT |
                Constants.WORLD_BOX_BIT |
                Constants.WORLD_BOX_KILL_BIT |
                Constants.TRIGGER_BIT |
                Constants.HLYBA_BIT;
        fixtureDef.shape = shape;
        b2dbody.createFixture(fixtureDef).setUserData(this);
    }
}
