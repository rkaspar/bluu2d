package com.mygdx.game;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.entities.Barrier;
import com.mygdx.game.entities.Entity;
import com.mygdx.game.entities.Player;
import com.mygdx.game.tools.B2WorldCreator;
import com.mygdx.game.tools.Constants;

public class PlayScreen implements Screen {

	World world;
	SpriteBatch batch;
	OrthographicCamera camera;

	Box2DDebugRenderer b2dr;
	MyGdxGame game;
	RayHandler rayHandler;
	Array<Entity> Entities;
	private Hud hud;
	TextureAtlas atlas;

	// map loader
	private OrthogonalTiledMapRenderer tileRenderer;
	private TiledMap map;
	private TmxMapLoader maploader;

	private Player player;
	private B2WorldCreator b2worldcreator;
	private Vector2 lastSpawn;

	public static float WORLD_SCALE = Constants.PPM;

	public PlayScreen(MyGdxGame myGdxGame) {
		Entities = new Array<Entity>();
		this.game = myGdxGame;
		batch = new SpriteBatch();
		world = new World(new Vector2(0f, Constants.GRAVITY), true);
		b2dr = new Box2DDebugRenderer();
		rayHandler = new RayHandler(world);
		rayHandler.setAmbientLight(new Color(.1f, .1f, .1f, .2f));

		atlas = new TextureAtlas("bluupack.pack");

		camera = new OrthographicCamera(320 / WORLD_SCALE, 160 / WORLD_SCALE);
		hud = new Hud(batch);

		this.world.setContactListener(new ContactHandler());

		// load level1

		maploader = new TmxMapLoader();
		map = maploader.load("lvl11.tmx");
		tileRenderer = new OrthogonalTiledMapRenderer(map, 1 / WORLD_SCALE);

		b2worldcreator = new B2WorldCreator(this);

		// create barriers
		// for (int i = 0; i < 8; i++) {
		// Entities.add(new Barrier(this, MathUtils.random(-100, 100)
		// / WORLD_SCALE, MathUtils.random(-100, 100) / WORLD_SCALE));
		// }

		// add player to entities
		// Entities.add(getPlayer());

	}

	public TextureAtlas getAtlas() {
		return atlas;
	}

	@Override
	public void show() {

	}

	@Override
	public void render(float dt) {

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// update game logic
		update(dt);

		tileRenderer.render();

		rayHandler.setCombinedMatrix(camera);
		rayHandler.updateAndRender();

		// b2dr.render(world, camera.combined);
		batch.setProjectionMatrix(camera.combined);

		// draw sprites
		batch.begin();

		for (Entity e : Entities) {
			if (e.getTexture() != null) {
				e.draw(batch);
			}
		}

		getPlayer().draw(batch);
		batch.end();

		// draw hud
		batch.setProjectionMatrix(hud.stage.getCamera().combined);
		hud.stage.draw();
	}

	private void update(float dt) {
		camera.update();

		world.step(1 / 60f, 6, 2);

		if (getPlayer().destroyed && getPlayer().gone) {
			setPlayer(new Player(this, lastSpawn.x, lastSpawn.y));
		}

		for (Entity e : Entities) {
			e.update(dt);
		}

		getPlayer().update(dt);

		if (Gdx.input.isKeyPressed(Keys.Q)) {

			if (!(camera.zoom > 1f)) {
				camera.zoom += 0.02f;
			}

		}
		if (Gdx.input.isKeyPressed(Keys.E)) {
			if (!(camera.zoom < .5f)) {
				camera.zoom += -0.02f;
			} else {
				camera.zoom = 0.5f;
			}

		}

		if (Gdx.input.isKeyPressed(Keys.A)) {

			getPlayer().bod.applyLinearImpulse(-getPlayer().PLAYER_SPEED, 0f,
					getPlayer().bod.getLocalCenter().x,
					getPlayer().bod.getLocalCenter().y, true);
		}
		if (Gdx.input.isKeyPressed(Keys.D)) {
			getPlayer().bod.applyLinearImpulse(getPlayer().PLAYER_SPEED, 0f,
					getPlayer().bod.getLocalCenter().x,
					getPlayer().bod.getLocalCenter().y, true);
		}
		if (Gdx.input.isKeyPressed(Keys.W)) {
			getPlayer().bod.applyLinearImpulse(0, getPlayer().PLAYER_SPEED,
					getPlayer().bod.getLocalCenter().x,
					getPlayer().bod.getLocalCenter().y, true);
		}
		if (Gdx.input.isKeyPressed(Keys.S)) {
			getPlayer().bod.applyLinearImpulse(0, -getPlayer().PLAYER_SPEED,
					getPlayer().bod.getLocalCenter().x,
					getPlayer().bod.getLocalCenter().y, true);
		}

		float lerp = 0.8f;
		Vector3 position = this.camera.position;

		if (!player.destroyed) {

			position.x += (getPlayer().bod.getPosition().x - position.x) * lerp
					* dt;
			position.y += (getPlayer().bod.getPosition().y - position.y) * lerp
					* dt;

			camera.position.set(position);
		}
		hud.update(dt);
		tileRenderer.setView(camera);

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		batch.dispose();
		rayHandler.dispose();
		world.dispose();
		map.dispose();
		b2worldcreator.dispose();
		Gdx.app.exit();

	}

	public World getWorld() {

		return this.world;
	}

	public RayHandler getRayHandler() {

		return this.rayHandler;
	}

	public OrthographicCamera getCamera() {

		return this.camera;
	}

	public TiledMap getMap() {

		return map;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {

		this.player = player;

	}

	public void setLastSpawn(Vector2 spawn) {

		this.lastSpawn = spawn;

	}

	public Vector2 getLastSpawn() {
		return lastSpawn;
	}

}
