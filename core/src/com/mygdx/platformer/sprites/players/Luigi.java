package com.mygdx.platformer.sprites.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.platformer.screens.levels.Level;
import com.mygdx.platformer.tools.Constants;

public class Luigi extends Player {
    public Luigi(Level screen) {
        super(screen, "luigi", new Vector2(Constants.toMeters(64), Constants.toMeters(128)));
    }

    @Override
    public void handleInput(float delta) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP) && jump())
            b2dbody.applyLinearImpulse(new Vector2(0, 5f), b2dbody.getWorldCenter(), true);
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && b2dbody.getLinearVelocity().x <= 2)
            b2dbody.applyLinearImpulse(new Vector2(0.15f, 0), b2dbody.getWorldCenter(), true);
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && b2dbody.getLinearVelocity().x >= -2)
            b2dbody.applyLinearImpulse(new Vector2(-0.15f, 0), b2dbody.getWorldCenter(), true);
    }
}
