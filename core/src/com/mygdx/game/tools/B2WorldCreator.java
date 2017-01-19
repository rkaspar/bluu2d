package com.mygdx.game.tools;

import box2dLight.ConeLight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.PlayScreen;
import com.mygdx.game.entities.Player;

public class B2WorldCreator implements Disposable {
	static int ppt = 16;

	public B2WorldCreator(PlayScreen screen) {
		World world = screen.getWorld();
		TiledMap map = screen.getMap();
		// create body and fixture variables
		BodyDef bdef = new BodyDef();

		FixtureDef fdef = new FixtureDef();
		Body body;

		/**
		 * 0 - fill bg 1 - mid bg 2 3 - level collision tiles 4 - spawn object
		 * layer
		 */

		// create ground bodies/fixtures
		for (MapObject object : map.getLayers().get("collision").getObjects()) {
			Shape shape;

			bdef = new BodyDef();
			shape = createShapeFromObject(object);

			bdef.type = BodyDef.BodyType.StaticBody;

			body = world.createBody(bdef);
			fdef.shape = shape;
			fdef.density = 1;
			fdef.filter.categoryBits = Constants.BOUNDARY_BITS;
			fdef.filter.maskBits = Constants.PLAYER_BITS | Constants.LIGHT_BITS;
			body.createFixture(fdef);
			shape.dispose();

		}

		for (MapObject object : map.getLayers().get("events").getObjects()) {
			Shape shape;

			shape = createShapeFromObject(object);
			fdef.shape = shape;
			bdef.type = BodyDef.BodyType.StaticBody;
			body = world.createBody(bdef);
			fdef.isSensor = true;
			fdef.density = 0;
			fdef.filter.categoryBits = Constants.WIN_BITS;
			fdef.filter.maskBits = Constants.PLAYER_BITS;
			body.createFixture(fdef);
			shape.dispose();

		}

		for (MapObject object : map.getLayers().get("lights").getObjects()) {
			Shape shape;

			shape = createShapeFromObject(object);

			bdef.type = BodyDef.BodyType.StaticBody;
			bdef.position.set(((PolygonMapObject) object).getPolygon().getX()
					/ screen.WORLD_SCALE, ((PolygonMapObject) object)
					.getPolygon().getY() / screen.WORLD_SCALE);
			body = screen.getWorld().createBody(bdef);
			body.setActive(false);

			//fdef.shape = shape;
			fdef.density = 1;
			fdef.filter.categoryBits = Constants.LIGHT_BITS;
			fdef.filter.maskBits = Constants.PLAYER_BITS
					| Constants.BOUNDARY_BITS | Constants.WIN_BITS
					| Constants.ITEM_BITS;
			// body.createFixture(fdef);

			ConeLight light = new ConeLight(screen.getRayHandler(), 125,
					new Color(1, 1, 1, .6f), 500f / screen.WORLD_SCALE,
					body.getPosition().x, body.getPosition().y, -90, 15);

			light.setContactFilter(fdef.filter);
			light.setSoft(true);

		}

		// create pipe bodies/fixtures
		for (MapObject object : map.getLayers().get("spawn").getObjects()
				.getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			System.out.println(rect.x + ", " + rect.y);
			screen.setPlayer(new Player(screen, rect.x, rect.y));
			screen.setLastSpawn(new Vector2(rect.x, rect.y));
		}

	}

	private static ChainShape createShapeFromObject(MapObject object) {
		MapObject polyline;
		float[] vertices;
		if (object instanceof PolygonMapObject) {
			polyline = ((PolygonMapObject) object);
			vertices = ((PolygonMapObject) polyline).getPolygon()
					.getTransformedVertices();
		} else if (object instanceof PolylineMapObject) {
			polyline = ((PolylineMapObject) object);
			vertices = ((PolylineMapObject) polyline).getPolyline()
					.getTransformedVertices();
		} else {
			vertices = null;
		}

		Vector2[] worldVertices = new Vector2[vertices.length / 2];

		for (int i = 0; i < worldVertices.length; i++) {
			worldVertices[i] = new Vector2(vertices[i * 2] / Constants.PPM,
					vertices[i * 2 + 1] / Constants.PPM);
		}

		ChainShape cs = new ChainShape();
		cs.createChain(worldVertices);

		return cs;
	}

	public void dispose() {

		this.dispose();

	}

}