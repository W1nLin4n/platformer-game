package com.mygdx.platformer.sprites;

import com.badlogic.gdx.math.Vector2;

public interface Interactive {
    void hit(Object o, Vector2 normal);
}
