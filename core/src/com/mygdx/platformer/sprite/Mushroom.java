package com.mygdx.platformer.sprite;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.mygdx.platformer.Platformer;
import com.mygdx.platformer.screen.GameScreen;

public class Mushroom extends Item {

    public Mushroom(GameScreen screen, float x, float y) {
        super(screen, x, y);
        setRegion(screen.getAtlas().findRegion("mushroom"), 0, 0, 16, 16);
        velocity = new Vector2(0.7f, 0);
    }

    @Override
    public void defineItem() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2dbody = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(Platformer.toMeters(7));
        fixtureDef.filter.categoryBits = Platformer.ITEM_BIT;
        fixtureDef.filter.maskBits =
                Platformer.PLAYER_BIT |
                Platformer.OBJECT_BIT |
                Platformer.GROUND_BIT |
                Platformer.COIN_BIT |
                Platformer.BRICK_BIT;

        fixtureDef.shape = shape;
        b2dbody.createFixture(fixtureDef).setUserData(this);
    }

    @Override
    public void use(Player player) {
        destroy();
        player.grow();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        setPosition(b2dbody.getPosition().x - getWidth()/2, b2dbody.getPosition().y - getHeight()/2);
        velocity.y = b2dbody.getLinearVelocity().y;
        b2dbody.setLinearVelocity(velocity);
    }
}
