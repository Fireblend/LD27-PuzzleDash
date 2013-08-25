package com.fireblend.puzzlerun;

import com.badlogic.gdx.Game;

public class PuzzleRun extends Game {	
	MenuScreen menuScreen;
    EndScreen endScreen;
    GameScreen gameScreen;
    
    public int finalScore;
    
    @Override
     public void create() {
    	menuScreen = new MenuScreen(this);
    	endScreen = new EndScreen(this);
    	gameScreen = new GameScreen(this);
        setScreen(gameScreen);              
     }
    
    /*@Override
	public void create() {		

 
		
		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(1, h/w);
		batch = new SpriteBatch();
		
		texture = new Texture(Gdx.files.internal("data/libgdx.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		TextureRegion region = new TextureRegion(texture, 0, 0, 512, 275);
		
		sprite = new Sprite(region);
		sprite.setSize(0.9f, 0.9f * sprite.getHeight() / sprite.getWidth());
		sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
		sprite.setPosition(-sprite.getWidth()/2, -sprite.getHeight()/2);
		
		
	}*/
}
