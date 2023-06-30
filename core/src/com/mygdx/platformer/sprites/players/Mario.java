package com.mygdx.platformer.sprites.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.platformer.screens.levels.Level;
import com.mygdx.platformer.tools.Constants;

public class Mario extends Player {
    private Array<Ball> balls;
    private float lastSpawnedBallTime;

    public Mario(Level screen) {
        super(screen, "mario", new Vector2(Constants.toMeters(32), Constants.toMeters(128)));
        balls = new Array<>();
        lastSpawnedBallTime = 0.5f;
    }

    @Override
    public void handleInput(float delta) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.W) && jump())
            b2dbody.applyLinearImpulse(new Vector2(0, 4f), b2dbody.getWorldCenter(), true);
        if(Gdx.input.isKeyPressed(Input.Keys.D) && b2dbody.getLinearVelocity().x <= 2)
            b2dbody.applyLinearImpulse(new Vector2(0.1f, 0), b2dbody.getWorldCenter(), true);
        if(Gdx.input.isKeyPressed(Input.Keys.A) && b2dbody.getLinearVelocity().x >= -2)
            b2dbody.applyLinearImpulse(new Vector2(-0.1f, 0), b2dbody.getWorldCenter(), true);
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && lastSpawnedBallTime >= 0.5f) {
            balls.add(new Ball(screen, b2dbody.getPosition(), runningRight));
            lastSpawnedBallTime = 0;
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        lastSpawnedBallTime += delta;

        for(Ball ball : balls) {
            ball.update(delta);
        }
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
        for(Ball ball : balls) {
            ball.draw(batch);
        }
    }
}
