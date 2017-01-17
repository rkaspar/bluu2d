package com.mygdx.game.entities;

import box2dLight.PointLight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.PlayScreen;
import com.mygdx.game.tools.Constants;

public class Player extends Entity {

	public enum State {
		FALLING, JUMPING, STANDING, RUNNING, GROWING, DEAD
	};

	public State currentState;
	public State previousState;

	public float PLAYER_SPEED = .5f;

	PointLight pLight;
	Animation breathAnimation, deathAnimation;
	private float stateTimer;
	public boolean gone;

	public Player(PlayScreen screen, float x, float y) {
		super(screen, x, y);

		currentState = State.STANDING;
		previousState = State.STANDING;

		Array<TextureRegion> frames = new Array<TextureRegion>();

		for (int i = 0; i <= 4; i++) {
			frames.add(new TextureRegion(screen.getAtlas().findRegion(
					"bluu_breath"), i * 32, 0, 32, 32));
		}
		breathAnimation = new Animation(0.8f, frames);
		frames.clear();

		for (int i = 0; i <= 5; i++) {
			frames.add(new TextureRegion(screen.getAtlas().findRegion(
					"bluu_death"), i * 32, 0, 32, 32));
		}
		deathAnimation = new Animation(0.3f, frames);
		frames.clear();

		setBounds(getX(), getY(), 16 / Constants.PPM, 16 / Constants.PPM);

		stateTimer = 0;

		destroyed = false;
		setToDestroy = false;

	}

	@Override
	protected void defineEntity() {
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.DynamicBody;
		bdef.position.set(getX() / PlayScreen.WORLD_SCALE, getY()
				/ PlayScreen.WORLD_SCALE);

		bod = world.createBody(bdef);

		FixtureDef f = new FixtureDef();
		CircleShape circle = new CircleShape();
		circle.setRadius(4 / PlayScreen.WORLD_SCALE);

		f.filter.categoryBits = Constants.PLAYER_BITS;
		f.filter.maskBits = Constants.BOUNDARY_BITS | Constants.ENEMY_BITS
				| Constants.BULLET_BITS | Constants.ITEM_BITS
				| Constants.LIGHT_BITS;

		f.shape = circle;
		bod.createFixture(f).setUserData(this);

		circle.dispose();

		pLight = new PointLight(screen.getRayHandler(), 500, new Color(1, 1, 1,
				.5f), 500f / screen.WORLD_SCALE, 0f, 0f);

		pLight.attachToBody(bod);

		pLight.setSoft(true);

	}

	@Override
	public void update(float dt) {
		stateTimer += dt;

		if (setToDestroy && !destroyed) {
			currentState = State.DEAD;
			destroy();

		}

		if (!destroyed) {

			if (this.bod.getLinearVelocity().y < 1) {
				currentState = State.FALLING;
			} else {
				currentState = State.STANDING;
			}

			this.setPosition(bod.getPosition().x - getWidth() / 2,
					bod.getPosition().y - getHeight() / 2);

			// pulse light

		}

		this.setRegion(getFrame(dt));

	}

	@Override
	public void destroy() {

		world.destroyBody(bod);
		this.destroyed = true;

	}

	@Override
	public void takeDamage(float damage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void gainHealth(float health) {
		// TODO Auto-generated method stub

	}

	public TextureRegion getFrame(float dt) {

		currentState = getState();

		TextureRegion region = null;

		// depending on the state, get corresponding animation keyFrame.
		switch (currentState) {
		case DEAD:
			region = deathAnimation.getKeyFrame(stateTimer);
			break;
		case GROWING:

			break;
		case JUMPING:

			break;
		case RUNNING:

			break;
		case FALLING:
		case STANDING:
		default:
			region = breathAnimation.getKeyFrame(stateTimer, true);
			break;
		}

		// if the current state is the same as the previous state increase the
		// state timer.
		// otherwise the state has changed and we need to reset timer.
		stateTimer = currentState == previousState ? stateTimer + dt : 0;
		// update previous state
		previousState = currentState;
		// return our final adjusted frame

		return region;

	}

	private State getState() {

		return this.currentState;
	}

	public void draw(Batch batch) {
		if (!destroyed || stateTimer < 2) {
			super.draw(batch);
		} else {
			pLight.setActive(false);
			pLight.dispose();
			gone = true;
		}
	}

	@Override
	public void setToDestroy(boolean isDead) {

		if (isDead) {
			setToDestroy = isDead;
		}

	}
}
