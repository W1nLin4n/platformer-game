package com.mygdx.platformer.tools;

import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.platformer.sprites.Interactive;

public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        if(fixtureA.getUserData() != null && fixtureA.getUserData() instanceof Interactive) {
            ((Interactive) fixtureA.getUserData()).hit(fixtureB.getUserData(), contact.getWorldManifold().getNormal());
        }

        if(fixtureB.getUserData() != null && fixtureB.getUserData() instanceof Interactive) {
            ((Interactive) fixtureB.getUserData()).hit(fixtureA.getUserData(), contact.getWorldManifold().getNormal());
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
