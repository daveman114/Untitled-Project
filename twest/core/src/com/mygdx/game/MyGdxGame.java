package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class MyGdxGame extends ApplicationAdapter {
	 private OrthographicCamera camera;
	private player Player;
	   private TiledMap map;
	    private AssetManager manager;
	    private int tileWidth, tileHeight,
        mapWidthInTiles, mapHeightInTiles,
        mapWidthInPixels, mapHeightInPixels;
	    private OrthogonalTiledMapRenderer renderer;

	
	
	@Override
	public void create () {
		manager = new AssetManager();
		manager.setLoader(TiledMap.class, new TmxMapLoader());
		manager.load("tiletest.tmx", TiledMap.class);
		manager.finishLoading();
		map = manager.get("tiletest.tmx", TiledMap.class);
		MapProperties properties = map.getProperties();
	        tileWidth         = properties.get("tilewidth", Integer.class);
	        tileHeight        = properties.get("tileheight", Integer.class);
	        mapWidthInTiles   = properties.get("width", Integer.class);
	        mapHeightInTiles  = properties.get("height", Integer.class);
	        mapWidthInPixels  = mapWidthInTiles  * tileWidth;
	        mapHeightInPixels = mapHeightInTiles * tileHeight;
	    camera = new OrthographicCamera(320.f, 180.f);
	    camera.position.x = mapWidthInPixels * .5f;
        camera.position.y = mapHeightInPixels * .35f;
        renderer = new OrthogonalTiledMapRenderer(map);
        Player = new player(new Sprite(new Texture("pixil-frame-0.png")), (TiledMapTileLayer) map.getLayers().get(0));
        Gdx.input.setInputProcessor(Player);
	}
    @Override
	public void render () {
		 Gdx.gl.glClearColor(.5f, .7f, .9f, 1);
	        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

	        renderer.setView(camera);
	        renderer.render();
	        renderer.getBatch().begin();
	        Player.draw(renderer.getBatch());
	        renderer.getBatch().end();
	        camera.update();
	        
	}
	
	@Override
	public void dispose () {
		manager.dispose();
	}
}
