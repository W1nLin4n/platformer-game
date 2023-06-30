package com.mygdx.platformer.sprites.enemies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.platformer.screens.levels.Level;
import com.mygdx.platformer.sprites.Interactive;

public abstract class Enemy extends Sprite implements Interactive {
    protected World world;
    protected Level screen;
    public Body b2dbody;
    public Vector2 velocity;
    protected boolean toDestroy;
    protected boolean destroyed;

    public Enemy(Level screen, float x, float y) {
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x, y);
        defineEnemy();
        velocity = new Vector2(-1, -2);
        b2dbody.setActive(false);

        toDestroy = false;
        destroyed = false;
    }

    protected abstract void defineEnemy();
    public abstract void update(float delta);
    public abstract void hit(Object o, Vector2 normal);

    public void reverseVelocity(boolean x, boolean y) {
        if(x)
            velocity.x = -velocity.x;
        if(y)
            velocity.y = -velocity.y;
    }
}
