package com.mygdx.platformer.sprites.players;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.platformer.screens.levels.Level;
import com.mygdx.platformer.tools.Constants;

public class Princess extends Sprite {
    private World world;
    private Level screen;

    public Princess(Level screen) {
        this.world = screen.getWorld();
        this.screen = screen;
        setBounds(Constants.toMeters(2968), Constants.toMeters(48), Constants.toMeters(16), Constants.toMeters(32));
        setRegion(new TextureRegion(screen.getAtlas().findRegion("princess"), 0, 0, 34, 64));
    }
}
