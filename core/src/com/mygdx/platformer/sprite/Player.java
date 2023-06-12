package com.mygdx.platformer.sprite;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.platformer.Platformer;
import com.mygdx.platformer.screen.GameScreen;

public class Player extends Sprite {
    public enum State {FALLING, JUMPING, STANDING, RUNNING, GROWING, DEAD};
    public State currentState;
    public State previousState;

    public World world;
    public Body b2dbody;
    private GameScreen screen;
    private TextureRegion playerStand;
    private Animation<TextureRegion> playerRun;
    private TextureRegion playerJump;
    private TextureRegion bigPlayerStand;
    private Animation<TextureRegion> bigPlayerRun;
    private TextureRegion bigPlayerJump;
    private Animation<TextureRegion> playerGrow;
    private TextureRegion playerDead;
    private float stateTimer;
    private boolean runningRight;
    private boolean playerIsBig;
    private boolean runGrowAnimation;
    private boolean timeToDefineBigPlayer;
    private boolean timeToDefineLittlePlayer;
    private boolean playerIsDead;

    public Player(GameScreen screen) {
        this.screen = screen;
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;
        playerIsBig = false;
        runGrowAnimation = false;
        timeToDefineBigPlayer = false;
        timeToDefineLittlePlayer = false;
        playerIsDead = false;

        Array<TextureRegion> frames = new Array<>();
        for(int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i*16, 0, 16, 16));
        playerRun = new Animation<>(0.1f, frames);
        frames.clear();

        for(int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), i*16, 0, 16, 32));
        bigPlayerRun = new Animation<>(0.1f, frames);
        frames.clear();

        playerJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 80, 0, 16, 16);
        bigPlayerJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 80, 0, 16, 32);

        playerStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0, 0, 16, 16);
        bigPlayerStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32);

        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        playerGrow = new Animation<TextureRegion>(0.2f, frames);

        playerDead = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 96, 0, 16, 16);

        definePlayer();
        setBounds(0, 0, Platformer.toMeters(16), Platformer.toMeters(16));
        setRegion(playerStand);
    }

    public float getStateTimer() {
        return stateTimer;
    }

    public boolean isDead() {
        return playerIsDead;
    }

    public boolean isBig() {
        return playerIsBig;
    }

    public void hit(Enemy enemy) {
        if(enemy instanceof Turtle && ((Turtle) enemy).getCurrentState() == Turtle.State.STANDING_SHELL) {
            ((Turtle) enemy).kick(this.getX() <= enemy.getX() ? Turtle.KICK_RIGHT_SPEED : Turtle.KICK_LEFT_SPEED);
        }
        if(playerIsBig) {
            playerIsBig = false;
            timeToDefineLittlePlayer = true;
            setBounds(getX(), getY(), getWidth(), getHeight() / 2);
            screen.getGame().assetManager.get("audio/sounds/powerdown.wav", Sound.class).play();
        } else {
            screen.getGame().assetManager.get("audio/music/mario_music.ogg", Music.class).stop();
            screen.getGame().assetManager.get("audio/sounds/mariodie.wav", Sound.class).play();
            playerIsDead = true;
            Filter filter = new Filter();
            filter.maskBits = Platformer.NOTHING_BIT;
            for(Fixture fixture : b2dbody.getFixtureList())
                fixture.setFilterData(filter);
            b2dbody.applyLinearImpulse(new Vector2(0, 4f), b2dbody.getWorldCenter(), true);
        }
    }
    public void grow() {
        runGrowAnimation = true;
        playerIsBig = true;
        timeToDefineBigPlayer = true;
        setBounds(getX(), getY(), getWidth(), getHeight() * 2);
        screen.getGame().assetManager.get("audio/sounds/powerup.wav", Sound.class).play();
    }

    public void update(float delta) {
        if(playerIsBig)
            setPosition(b2dbody.getPosition().x - getWidth() / 2, b2dbody.getPosition().y - getHeight() / 2 - Platformer.toMeters(7));
        else
            setPosition(b2dbody.getPosition().x - getWidth() / 2, b2dbody.getPosition().y - getHeight() / 2);
        setRegion(getFrame(delta));
        if(timeToDefineBigPlayer) {
            defineBigPlayer();
        }
        if(timeToDefineLittlePlayer) {
            defineLittlePlayer();
        }
    }

    public TextureRegion getFrame(float delta) {
        currentState = getState();

        TextureRegion region;
        switch (currentState) {
            case DEAD:
                region = playerDead;
                break;
            case GROWING:
                region = playerGrow.getKeyFrame(stateTimer);
                if(playerGrow.isAnimationFinished(stateTimer))
                    runGrowAnimation = false;
                break;
            case JUMPING:
                region = playerIsBig ? bigPlayerJump : playerJump;
                break;
            case RUNNING:
                region = playerIsBig ? bigPlayerRun.getKeyFrame(stateTimer, true) : playerRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = playerIsBig ? bigPlayerStand : playerStand;
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
            return  State.DEAD;
        else if(runGrowAnimation)
            return State.GROWING;
        else if(b2dbody.getLinearVelocity().y > 0 || (b2dbody.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            return State.JUMPING;
        else if(b2dbody.getLinearVelocity().y < 0)
            return State.FALLING;
        else if(b2dbody.getLinearVelocity().x != 0)
            return State.RUNNING;
        else
            return State.STANDING;
    }

    public void defineBigPlayer() {
        Vector2 currentPosition = b2dbody.getPosition();
        world.destroyBody(b2dbody);

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(currentPosition.add(0, Platformer.toMeters(10)));
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2dbody = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(Platformer.toMeters(7));
        fixtureDef.filter.categoryBits = Platformer.PLAYER_BIT;
        fixtureDef.filter.maskBits =
                Platformer.GROUND_BIT |
                        Platformer.BRICK_BIT |
                        Platformer.COIN_BIT |
                        Platformer.OBJECT_BIT |
                        Platformer.ENEMY_BIT |
                        Platformer.ENEMY_HEAD_BIT |
                        Platformer.ITEM_BIT;
        fixtureDef.shape = shape;
        b2dbody.createFixture(fixtureDef).setUserData(this);
        shape.setPosition(new Vector2(0, Platformer.toMeters(-14)));
        fixtureDef.shape = shape;
        b2dbody.createFixture(fixtureDef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(Platformer.toMeters(-2), Platformer.toMeters(7)), new Vector2(Platformer.toMeters(2), Platformer.toMeters(7)));
        fixtureDef.filter.categoryBits = Platformer.PLAYER_HEAD_BIT;
        fixtureDef.shape = head;
        fixtureDef.isSensor = true;
        b2dbody.createFixture(fixtureDef).setUserData(this);
        timeToDefineBigPlayer = false;
    }

    public void defineLittlePlayer() {
        Vector2 currentPosition = b2dbody.getPosition();
        world.destroyBody(b2dbody);

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(currentPosition);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2dbody = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(Platformer.toMeters(7));
        fixtureDef.filter.categoryBits = Platformer.PLAYER_BIT;
        fixtureDef.filter.maskBits =
                Platformer.GROUND_BIT |
                        Platformer.BRICK_BIT |
                        Platformer.COIN_BIT |
                        Platformer.OBJECT_BIT |
                        Platformer.ENEMY_BIT |
                        Platformer.ENEMY_HEAD_BIT |
                        Platformer.ITEM_BIT;
        fixtureDef.shape = shape;
        b2dbody.createFixture(fixtureDef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(Platformer.toMeters(-2), Platformer.toMeters(7)), new Vector2(Platformer.toMeters(2), Platformer.toMeters(7)));
        fixtureDef.filter.categoryBits = Platformer.PLAYER_HEAD_BIT;
        fixtureDef.shape = head;
        fixtureDef.isSensor = true;
        b2dbody.createFixture(fixtureDef).setUserData(this);
        timeToDefineLittlePlayer = false;
    }

    public void definePlayer() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(Platformer.toMeters(32), Platformer.toMeters(128));
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2dbody = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(Platformer.toMeters(7));
        fixtureDef.filter.categoryBits = Platformer.PLAYER_BIT;
        fixtureDef.filter.maskBits =
                Platformer.GROUND_BIT |
                Platformer.BRICK_BIT |
                Platformer.COIN_BIT |
                Platformer.OBJECT_BIT |
                Platformer.ENEMY_BIT |
                Platformer.ENEMY_HEAD_BIT |
                Platformer.ITEM_BIT;
        fixtureDef.shape = shape;
        b2dbody.createFixture(fixtureDef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(Platformer.toMeters(-2), Platformer.toMeters(7)), new Vector2(Platformer.toMeters(2), Platformer.toMeters(7)));
        fixtureDef.filter.categoryBits = Platformer.PLAYER_HEAD_BIT;
        fixtureDef.shape = head;
        fixtureDef.isSensor = true;
        b2dbody.createFixture(fixtureDef).setUserData(this);
    }
}
