package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.PlayScreen;

public abstract class Entity extends Sprite {
	protected World world;
	protected PlayScreen screen;
	public Body bod;
	public Vector2 velocity;
	public boolean destroyed, setToDestroy, dead;

	public Entity(PlayScreen screen, float x, float y) {
		this.world = screen.getWorld();
		this.screen = screen;
		setPosition(x, y);
		defineEntity();
		destroyed = setToDestroy = dead = false;

	}

	public abstract void destroy();

	public abstract void setToDestroy(boolean isDead);

	public abstract void takeDamage(float damage);

	public abstract void gainHealth(float health);

	protected abstract void defineEntity();

	public abstract void update(float dt);

	public void reverseVelocity(boolean x, boolean y) {
		if (x)
			velocity.x = -velocity.x;
		if (y)
			velocity.y = -velocity.y;
	}

	public Vector2 getPosition() {
		return new Vector2(getX(), getY());
	}

}