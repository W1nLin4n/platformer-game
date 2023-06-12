package com.mygdx.platformer.tool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.platformer.Platformer;
import com.mygdx.platformer.sprite.Enemy;
import com.mygdx.platformer.sprite.InteractiveTileObject;
import com.mygdx.platformer.sprite.Item;
import com.mygdx.platformer.sprite.Player;

public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        int cDef = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;

        switch (cDef) {
            case Platformer.PLAYER_HEAD_BIT | Platformer.BRICK_BIT:
            case Platformer.PLAYER_HEAD_BIT | Platformer.COIN_BIT:
                if(fixtureA.getFilterData().categoryBits == Platformer.PLAYER_HEAD_BIT)
                    ((InteractiveTileObject) fixtureB.getUserData()).onHeadHit((Player) fixtureA.getUserData());
                else
                    ((InteractiveTileObject) fixtureA.getUserData()).onHeadHit((Player) fixtureB.getUserData());
                break;
            case Platformer.ENEMY_HEAD_BIT | Platformer.PLAYER_BIT:
                if(fixtureA.getFilterData().categoryBits == Platformer.ENEMY_HEAD_BIT)
                    ((Enemy) fixtureA.getUserData()).hitOnHead((Player) fixtureB.getUserData());
                else
                    ((Enemy) fixtureB.getUserData()).hitOnHead((Player) fixtureA.getUserData());
                break;
            case Platformer.ENEMY_BIT | Platformer.OBJECT_BIT:
                if(fixtureA.getFilterData().categoryBits == Platformer.ENEMY_BIT)
                    ((Enemy) fixtureA.getUserData()).reverseVelocity(true, false);
                else
                    ((Enemy) fixtureB.getUserData()).reverseVelocity(true, false);
                break;
            case Platformer.PLAYER_BIT | Platformer.ENEMY_BIT:
                if(fixtureA.getFilterData().categoryBits == Platformer.PLAYER_BIT)
                    ((Player) fixtureA.getUserData()).hit((Enemy) fixtureB.getUserData());
                else
                    ((Player) fixtureB.getUserData()).hit((Enemy) fixtureA.getUserData());
                break;
            case Platformer.ENEMY_BIT | Platformer.ENEMY_BIT:
                ((Enemy) fixtureA.getUserData()).onEnemyHit((Enemy) fixtureB.getUserData());
                ((Enemy) fixtureB.getUserData()).onEnemyHit((Enemy) fixtureA.getUserData());
                break;
            case Platformer.ITEM_BIT | Platformer.OBJECT_BIT:
                if(fixtureA.getFilterData().categoryBits == Platformer.ITEM_BIT)
                    ((Item) fixtureA.getUserData()).reverseVelocity(true, false);
                else
                    ((Item) fixtureB.getUserData()).reverseVelocity(true, false);
                break;
            case Platformer.ITEM_BIT | Platformer.PLAYER_BIT:
                if(fixtureA.getFilterData().categoryBits == Platformer.ITEM_BIT)
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
