package com.mygdx.platformer.tools;

import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.platformer.sprites.enemies.Enemy;
import com.mygdx.platformer.sprites.tiles.InteractiveTileObject;
import com.mygdx.platformer.sprites.items.Item;
import com.mygdx.platformer.sprites.players.Player;

public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        int cDef = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;

        switch (cDef) {
            case Constants.PLAYER_HEAD_BIT | Constants.BRICK_BIT:
            case Constants.PLAYER_HEAD_BIT | Constants.COIN_BIT:
                if(fixtureA.getFilterData().categoryBits == Constants.PLAYER_HEAD_BIT)
                    ((InteractiveTileObject) fixtureB.getUserData()).onHeadHit((Player) fixtureA.getUserData());
                else
                    ((InteractiveTileObject) fixtureA.getUserData()).onHeadHit((Player) fixtureB.getUserData());
                break;
            case Constants.ENEMY_HEAD_BIT | Constants.PLAYER_BIT:
                if(fixtureA.getFilterData().categoryBits == Constants.ENEMY_HEAD_BIT)
                    ((Enemy) fixtureA.getUserData()).hitOnHead((Player) fixtureB.getUserData());
                else
                    ((Enemy) fixtureB.getUserData()).hitOnHead((Player) fixtureA.getUserData());
                break;
            case Constants.ENEMY_BIT | Constants.OBJECT_BIT:
                if(fixtureA.getFilterData().categoryBits == Constants.ENEMY_BIT)
                    ((Enemy) fixtureA.getUserData()).reverseVelocity(true, false);
                else
                    ((Enemy) fixtureB.getUserData()).reverseVelocity(true, false);
                break;
            case Constants.PLAYER_BIT | Constants.ENEMY_BIT:
                if(fixtureA.getFilterData().categoryBits == Constants.PLAYER_BIT)
                    ((Player) fixtureA.getUserData()).hit((Enemy) fixtureB.getUserData());
                else
                    ((Player) fixtureB.getUserData()).hit((Enemy) fixtureA.getUserData());
                break;
            case Constants.ENEMY_BIT | Constants.ENEMY_BIT:
                ((Enemy) fixtureA.getUserData()).onEnemyHit((Enemy) fixtureB.getUserData());
                ((Enemy) fixtureB.getUserData()).onEnemyHit((Enemy) fixtureA.getUserData());
                break;
            case Constants.ITEM_BIT | Constants.OBJECT_BIT:
                if(fixtureA.getFilterData().categoryBits == Constants.ITEM_BIT)
                    ((Item) fixtureA.getUserData()).reverseVelocity(true, false);
                else
                    ((Item) fixtureB.getUserData()).reverseVelocity(true, false);
                break;
            case Constants.ITEM_BIT | Constants.PLAYER_BIT:
                if(fixtureA.getFilterData().categoryBits == Constants.ITEM_BIT)
                    ((Item) fixtureA.getUserData()).use((Player) fixtureB.getUserData());
                else
                    ((Item) fixtureB.getUserData()).use((Player) fixtureA.getUserData());
                break;
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
