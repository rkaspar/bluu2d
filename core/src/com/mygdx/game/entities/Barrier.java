package com.mygdx.game.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.mygdx.game.PlayScreen;

public class Barrier extends Entity {

	public Barrier(PlayScreen screen, float x, float y) {
		super(screen, x, y);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void defineEntity() {
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.StaticBody;
		bdef.position.set(getX(), getY());
		bod = world.createBody(bdef);

		FixtureDef f = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(20 / screen.WORLD_SCALE, 100 / screen.WORLD_SCALE,
				bod.getPosition(), 360 - (MathUtils.random(4) * 90));
		f.shape = shape;
		Fixture fix = bod.createFixture(f);
		shape.dispose();

	}

	@Override
	public void update(float dt) {

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void takeDamage(float damage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void gainHealth(float health) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setToDestroy(boolean isDead) {
		// TODO Auto-generated method stub

	}

}
