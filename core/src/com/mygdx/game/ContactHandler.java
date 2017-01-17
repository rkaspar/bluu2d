package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.game.entities.Player;
import com.mygdx.game.tools.Constants;

public class ContactHandler implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		Fixture fixA = contact.getFixtureA();
		Fixture fixB = contact.getFixtureB();

		int cDef = fixA.getFilterData().categoryBits
				| fixB.getFilterData().categoryBits;

		switch (cDef) {

		case Constants.BOUNDARY_BITS | Constants.PLAYER_BITS: // if collision
																// between
																// player and
																// wall.
			System.out.println("death");

			if (fixA.getUserData() instanceof Player) {
				((Player) fixA.getUserData()).setToDestroy(true);
			} else {
				((Player) fixB.getUserData()).setToDestroy(true);
			}

			break;
		default:
			break;

		}

	}

	@Override
	public void endContact(Contact contact) {
		System.out.println("contact End");
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub

	}

}
