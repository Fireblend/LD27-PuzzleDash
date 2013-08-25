package com.fireblend.puzzlerun;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class GameScreen  implements Screen, InputProcessor{

	PuzzleRun game;
	
	int buttonHeight = 80;
	int buttonWidth = 80;
	
	//Screen variables
	float widthCorrect;
	float heightCorrect;
	
	private int squareHeight;
	private int squareWidth;
	
	//General variables
	private int[][] gameArea;
	private int gameAreaHeight = 200;
	private int gameAreaWidth = 200;
	private long time = 1;
	private long life = 100;
	private int score = 0;

	//Level variables
	private int level = 0;
	private int blockCount = 0;
	private int blockRequirement = 10;
	
	//Combo variables
	private ArrayList<int[]> currentlySelectedCoords = new ArrayList<int[]>();
	private Integer currentlySelectedColor = null;
	
	//Navigation variables
	private int currentOriginX = 0;
	private int currentOriginY = 0;
    
	private Integer dragX = null;
	private Integer dragY = null;
	
	private Random rand;
	
	//Camera and textures
	OrthographicCamera camera;
	SpriteBatch batch;
	
	Texture yBlock;
	Texture gBlock;
	Texture bBlock;
	Texture pBlock;
	Texture vBlock;
	Texture bombBlock;
	Texture button;
	Texture greyBlock;
	Texture transBlock;
	Texture plusBlock;
	Texture gameOverTexture;
	Texture menuTexture;
	Texture timerTexture;
	Texture scoreTexture;

	Sound comboSnd;
	Sound comboSnd2;
	Sound readySnd;
	Sound buttonSnd;
	Sound clockTickSnd;
	Sound endSnd;
	
	Music menuMusic;
	Music gameMusic;
	
	BitmapFont font;
	BitmapFont fontWhite;
	CharSequence scoreTxt = "0";
	CharSequence timeTxt = "10";
	CharSequence needTxt = "0/10";
			
	boolean areaRenderable = false;

	private float h;

	private float secondAccum = 0;

	private boolean end;
	private boolean menu = true;
	private boolean released = true;
	
	//Initializes the matrix for a new level
	public void initGameArea(){
		if(gameArea == null){
			gameArea = new int[gameAreaWidth][gameAreaHeight];
		}
		for(int i = 0; i<gameAreaWidth; i++){
			for(int j = 0; j<gameAreaHeight; j++){
				gameArea[i][j] = rand.nextInt(5);
			}
		}

		for(int i = 0;i<25;i++){
			int a = rand.nextInt(gameAreaWidth);
			int b = rand.nextInt(gameAreaHeight);
			gameArea[a][b] = 12;
		}
		for(int i = 0;i<25;i++){
			int a = rand.nextInt(gameAreaWidth);
			int b = rand.nextInt(gameAreaHeight);
			gameArea[a][b] = 13;
		}
		
		gameArea[gameAreaWidth/2][gameAreaHeight/2] = 8;
		gameArea[gameAreaWidth/2+1][gameAreaHeight/2] = 8;
		gameArea[gameAreaWidth/2+1][gameAreaHeight/2+1] = 8;
		gameArea[gameAreaWidth/2][gameAreaHeight/2+1] = 9;
	}
	
	
	//Initializes new level parameters
	public void initLevel(){

		gameAreaHeight = 200;
		gameAreaWidth = 200;
		time = 10;
		life = 100;
		score = 0;

		//Level variables
		level = 0;
		blockCount = 0;
		blockRequirement = 10;

		currentOriginY = (int)(squareHeight*gameAreaHeight/2 - (Gdx.graphics.getHeight()/2));
		currentOriginX = (int)(squareWidth*gameAreaWidth  /2 - (Gdx.graphics.getWidth() /2));
		
		areaRenderable = false;
		if(rand == null){
			rand = new Random();
		}
		/*
		int nextChallenge = rand.nextInt(2);
		switch(nextChallenge){
			case 0:
				break;
			case 1:
				blockRequirement *= 1.2;
		}
		level++;
		*/
		initGameArea();
		areaRenderable = true;
	}
	
	public GameScreen(PuzzleRun game){
		this.game = game;
		
		yBlock = new Texture(Gdx.files.internal("data/yellow.png"));
		gBlock = new Texture(Gdx.files.internal("data/green.png"));
		bBlock = new Texture(Gdx.files.internal("data/blue.png"));
		pBlock = new Texture(Gdx.files.internal("data/pink.png"));
		vBlock = new Texture(Gdx.files.internal("data/purple.png"));
		transBlock = new Texture(Gdx.files.internal("data/trans.png"));
		greyBlock = new Texture(Gdx.files.internal("data/grey.png"));
		button = new Texture(Gdx.files.internal("data/button.png"));
		bombBlock = new Texture(Gdx.files.internal("data/bomb.png"));
		timerTexture = new Texture(Gdx.files.internal("data/timer.png"));
		scoreTexture = new Texture(Gdx.files.internal("data/score.png"));
		plusBlock = new Texture(Gdx.files.internal("data/plus.png"));
		gameOverTexture = new Texture(Gdx.files.internal("data/end.png"));
		menuTexture = new Texture(Gdx.files.internal("data/main.png"));
		
		buttonSnd = Gdx.audio.newSound(Gdx.files.internal("data/Blip_Select7.mp3"));
		comboSnd = Gdx.audio.newSound(Gdx.files.internal("data/Pickup_Coin8.mp3"));
		comboSnd2 = Gdx.audio.newSound(Gdx.files.internal("data/Pickup_Coin6.mp3"));
		readySnd = Gdx.audio.newSound(Gdx.files.internal("data/Powerup9.mp3"));
		clockTickSnd = Gdx.audio.newSound(Gdx.files.internal("data/Blip_Select10.mp3"));
		endSnd = Gdx.audio.newSound(Gdx.files.internal("data/Blip_Select12.mp3"));

		menuMusic = Gdx.audio.newMusic(Gdx.files.internal("data/menuMusic.mp3"));
		gameMusic = Gdx.audio.newMusic(Gdx.files.internal("data/gameMusic.mp3"));
		
		 font = new BitmapFont(Gdx.files.internal("data/font.fnt"),
		         Gdx.files.internal("data/font.png"), false);
		 fontWhite = new BitmapFont(Gdx.files.internal("data/fontWhite.fnt"),
		         Gdx.files.internal("data/fontWhite.png"), false);
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		released = true;
		if(end || menu){return false;}
		if(pointer == 0 || pointer == 1){
			dragX = null;
			dragY = null;
		}
		if(currentlySelectedCoords.size() > 2){
			endCombo();
		}else{
			currentlySelectedColor = null;
			currentlySelectedCoords.clear();
		}
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		int heightCorrect2 = (int) (screenY*800.0/Gdx.graphics.getHeight());
		Gdx.app.log("Adj", screenX/widthCorrect+","+heightCorrect2);
		if(menu && released){
			Rectangle startButton;
			Rectangle quitButton;
			if(Gdx.app.getType() == ApplicationType.Android){
				startButton = new Rectangle(
						86,
						307,
						310,100);

				quitButton = new Rectangle(
						110,
						450,
						280,100);
			}
			else{
				startButton = new Rectangle(
					86,
					307,
					310,100);

				quitButton = new Rectangle(
					110,
					450,
					280,100);
			}
			if(startButton.contains(screenX/widthCorrect, heightCorrect2)){
				buttonSnd.play((float) 0.6);
				menu = false;
				initLevel();
				currentOriginY = (int)(squareHeight*gameAreaHeight/2 - (Gdx.graphics.getHeight()/2));
				currentOriginX = (int)(squareWidth*gameAreaWidth  /2 - (Gdx.graphics.getWidth() /2));
				menuMusic.stop();
				gameMusic.setLooping(true);
				gameMusic.play();
			}
			if(quitButton.contains(screenX/widthCorrect, heightCorrect2)){
				buttonSnd.play((float) 0.6);
				Gdx.app.exit();
			}
			released = false;
		}
		else if(end && released){	
			Rectangle retryButton;
			Rectangle quitButton;
			if(Gdx.app.getType() == ApplicationType.Android){
				retryButton = new Rectangle(
						85,
						490,
						120,60);
				
				quitButton = new Rectangle(
						283,505,100,35);
			}
			else{
				retryButton = new Rectangle(
						85,
						490,
						120,60);
				
				quitButton = new Rectangle(
						283,505,100,35);
			}
			if(quitButton.contains(screenX/widthCorrect, heightCorrect2)){
				buttonSnd.play((float) 0.6);
				end = false;
				menu = true;
				gameMusic.stop();
				menuMusic.setLooping(true);
				menuMusic.play();
			}
			else if(retryButton.contains(screenX/widthCorrect, heightCorrect2)){
				buttonSnd.play((float) 0.6);
				initLevel();
				end = false;
				currentOriginY = (int)(squareHeight*gameAreaHeight/2 - (Gdx.graphics.getHeight()/2));
				currentOriginX = (int)(squareWidth*gameAreaWidth  /2 - (Gdx.graphics.getWidth() /2));
			}
			released = false;
			return false;
		}

		int blockX = (currentOriginX+screenX)/squareWidth;
		int blockY = (currentOriginY+screenY)/squareHeight;
		int selectedColor = gameArea[blockX][blockY];

		if(selectedColor == 8 || selectedColor == 9){
			if(blockCount >= blockRequirement ){
				time = 10;
				secondAccum = 0;
				blockCount = 0;
				blockRequirement += 1;
			}
		}
		if(selectedColor == 13){
			time += 3;
			if(time > 10){
				time = 10;
			}
			gameArea[blockX][blockY] = 5;
		}
		return false; 
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(end|| menu){return false;}
		
		if(screenX < 0 || screenX > Gdx.graphics.getWidth() || screenY < 0 || screenY > Gdx.graphics.getHeight()){
			if(currentlySelectedCoords.size() > 2){
				endCombo();
			}
			return false;
		}
		
		if(pointer == 1){
			if(dragX == null){
				
				dragX = screenX;
				dragY = screenY;
			}
			else{
				//Gdx.app.log("Adj", (dragX-screenX)+","+(dragY-screenY));
				currentOriginX += dragX-screenX;
				currentOriginY += dragY-screenY;

				if(currentOriginX < 0){
					currentOriginX = 0;
				}
				else if(currentOriginX > (squareWidth*gameAreaWidth)-Gdx.graphics.getWidth()){
					currentOriginX = (squareWidth*gameAreaWidth)-Gdx.graphics.getWidth()-1;
				}
				if(currentOriginY < 0){
					currentOriginY = 0;
				}
				else if(currentOriginY > (squareHeight*gameAreaHeight)-Gdx.graphics.getHeight()){
					currentOriginY = (squareHeight*gameAreaHeight)-Gdx.graphics.getHeight()-1;
				}
				
				dragX = screenX;
				dragY = screenY;
				
			}
		}
		
		else if(pointer == 0){
			int blockX = (currentOriginX+screenX)/squareWidth;
			int blockY = (currentOriginY+screenY)/squareHeight;
			int selectedColor = gameArea[blockX][blockY];
			
			if(selectedColor == 12){
				initGameArea();
				currentOriginY = (int)(squareHeight*gameAreaHeight/2 - (Gdx.graphics.getHeight()/2));
				currentOriginX = (int)(squareWidth*gameAreaWidth  /2 - (Gdx.graphics.getWidth() /2));
			}
			
			if(currentlySelectedColor == null){

				if(selectedColor == 5 || selectedColor >=8){
					return false;
				}
				currentlySelectedColor = selectedColor;
				int[] coords = {blockX,blockY};
				currentlySelectedCoords.add(coords);
				if(currentlySelectedColor==0)Gdx.app.log("Adj","amarillo");
				if(currentlySelectedColor==1)Gdx.app.log("Adj","rosado");
				if(currentlySelectedColor==2)Gdx.app.log("Adj","verde");
				if(currentlySelectedColor==3)Gdx.app.log("Adj","azul");
				if(currentlySelectedColor==4)Gdx.app.log("Adj","morado");
			}
			else{
				if(selectedColor != currentlySelectedColor){
					Gdx.app.log("Adj","Combo broken");
					endCombo();
				}
				else{
					boolean alreadyIncluded = false;
					for(int i = 0; i<currentlySelectedCoords.size(); i++){
						int[] lastCoords = currentlySelectedCoords.get(i);
						if(lastCoords[0] == blockX && lastCoords[1] == blockY){
							alreadyIncluded = true;
						}
					}
					if(!alreadyIncluded){
						int[] coords = {blockX,blockY};
						currentlySelectedCoords.add(coords);
						Gdx.app.log("Adj","Added to combo: "+blockX+","+blockY);
						if(currentlySelectedCoords.size()<4){
							comboSnd.play(0.5f);
						}
						else{
							comboSnd2.play(0.5f);
						}
					}
					else if(currentlySelectedCoords.get(currentlySelectedCoords.size()-1)[0] != blockX || 
							currentlySelectedCoords.get(currentlySelectedCoords.size()-1)[1] != blockY){
						endCombo();
					}
				}
			}
		}
		
		return false;
	}

	private void endCombo() {
		int old = blockCount;
		int newscore = 0;
		if(currentlySelectedCoords.size() >= 3){
			blockCount += currentlySelectedCoords.size();
			for(int i = 0; i < currentlySelectedCoords.size(); i++){
				gameArea[currentlySelectedCoords.get(i)[0]][currentlySelectedCoords.get(i)[1]] = 5;
				newscore+=10+(10*(i*0.2));
				life+=15;
				if(life > 100){
					life = 100;
				}
			}
		}
		score += newscore;
		currentlySelectedColor = null;
		currentlySelectedCoords.clear();
		
		if(old < blockRequirement && blockCount >= blockRequirement){
			readySnd.play(1f);
		}
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {return false;}

	@Override
	public boolean scrolled(int amount) {return false;}

	@Override
	public void render(float delta) {
		if(!end && !menu){
			secondAccum += delta;
			if(secondAccum >= 1 ){
				secondAccum = 0;
				time--;
				if(time == 0){
					endSnd.play(0.6f);
				}
				else if(time <= 3){
					clockTickSnd.play(0.6f);
				}
			}
		}
		
		if(time <= 0 && !end && !menu){
			gameOver();
		}
		
		Gdx.gl.glClearColor( 0.8f, 0.8f, 0.8f, 1 );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
		   
		while(areaRenderable == false){};

		int origBlockX = (currentOriginX/squareWidth)-4;
		int origBlockY = (currentOriginY/squareHeight)-4;
		
		if(origBlockX < 0) origBlockX = 0;
		if(origBlockY < 0) origBlockY = 0;

		int limX = origBlockX+(Gdx.graphics.getWidth() / squareWidth)+7;
		int limY = origBlockY+(Gdx.graphics.getHeight() / squareHeight)+7;

		if(limX >= gameAreaWidth) limX = gameAreaWidth-1;
		if(limY >= gameAreaHeight) limY = gameAreaHeight-1;
		
		int difX = (currentOriginX) % squareWidth;
		int difY = (currentOriginY) % squareHeight;

		batch.begin();
		
		for(int i = origBlockX;  i <= limX ; i++){
			for(int j = origBlockY;  j <= limY ; j++){
				
				float x = ((i-origBlockX-4)*squareWidth-difX); 
				float y = Gdx.graphics.getHeight() - ((j-origBlockY-4)*squareHeight-difY) - squareHeight;
				
				switch(gameArea[i][j]){
				case 0:
					batch.draw(yBlock,x, y, squareWidth, squareHeight);
					break;
				case 1:
					batch.draw(pBlock,x, y, squareWidth, squareHeight);
					break;
				case 2:
					batch.draw(gBlock,x, y, squareWidth, squareHeight);
					break;
				case 3:
					batch.draw(bBlock,x, y, squareWidth, squareHeight);
					break;
				case 4:
					batch.draw(vBlock,x, y, squareWidth, squareHeight);
					break;
				case 9:

					batch.draw(button,x, y, squareWidth*2, squareHeight*2);
					
					if(blockCount >= blockRequirement){
						needTxt = "10 secs!";
						fontWhite.setScale((float) (widthCorrect*1.1));
						fontWhite.draw(batch, needTxt, (int)(x+squareWidth*0.20), (int)(y+squareHeight*1.20));
					}
					else{
						needTxt = blockCount+"/"+blockRequirement;
						fontWhite.setScale((float) (widthCorrect*1.5));
						fontWhite.draw(batch, needTxt, (int)(x+squareWidth*0.35), (int)(y+squareHeight*1.20));
					}	
					break;
				
				case 12:
					batch.draw(bombBlock,x, y, squareWidth, squareHeight);
					break;
				case 13:
					batch.draw(plusBlock,x, y, squareWidth, squareHeight);
					break;
				}
				
				for(int d = 0; d < currentlySelectedCoords.size(); d++){
					int[] lastCoords = currentlySelectedCoords.get(d);
					if(lastCoords[0] == i && lastCoords[1] == j){
						batch.draw(transBlock,x, y, squareWidth, squareHeight);
					}
				}
				
			}	
		}
		font.setColor(Color.BLACK);
		if(!end && !menu){
			//Timer and score
			batch.draw(timerTexture, 10*widthCorrect, Gdx.graphics.getHeight()-10*heightCorrect-timerTexture.getHeight()*heightCorrect, 
					timerTexture.getWidth()*widthCorrect, 
					timerTexture.getHeight()*heightCorrect);
	
			batch.draw(scoreTexture, 200*widthCorrect, Gdx.graphics.getHeight()-10*heightCorrect-scoreTexture.getHeight()*heightCorrect, 
					scoreTexture.getWidth()*widthCorrect, 
					scoreTexture.getHeight()*heightCorrect);
			
			timeTxt = ""+time;
			scoreTxt = ""+score;
	
			font.setScale((float) (widthCorrect*1.4));
			font.draw(batch, timeTxt, 40*widthCorrect, Gdx.graphics.getHeight()+40*heightCorrect-timerTexture.getHeight()*heightCorrect);
			font.draw(batch, scoreTxt, 210*widthCorrect, Gdx.graphics.getHeight()+40*heightCorrect-scoreTexture.getHeight()*heightCorrect);
		}
		else if(end){
			batch.draw(gameOverTexture,Gdx.graphics.getWidth()/2-(gameOverTexture.getWidth()*widthCorrect)/2,
									   Gdx.graphics.getHeight()/2-(gameOverTexture.getHeight()*heightCorrect)/2,
									   gameOverTexture.getWidth()*widthCorrect,
									   gameOverTexture.getHeight()*heightCorrect);
			
			
			font.draw(batch, scoreTxt, Gdx.graphics.getWidth()/2-50*widthCorrect,
					   Gdx.graphics.getHeight()/2);
		}
		else if(menu){
			batch.draw(menuTexture,20*widthCorrect, 120*widthCorrect, 
					menuTexture.getWidth()*widthCorrect,
					menuTexture.getHeight()*heightCorrect);

		}
		
		batch.end();
	}

	private void gameOver() {
		game.finalScore = score;
		end = true;
	}

	@Override
	public void resize(int width, int height) {
		widthCorrect = (float)(width)/(float)(480);
		heightCorrect = (float)height/(float)h;

		squareHeight = (int) (buttonHeight*heightCorrect);
		squareWidth =  (int) (buttonWidth*widthCorrect);
		
		currentOriginY = (int)(squareHeight*gameAreaHeight/2 - (Gdx.graphics.getHeight()/2));
		currentOriginX = (int)(squareWidth*gameAreaWidth  /2 - (Gdx.graphics.getWidth() /2));
	}

	@Override
	public void show() {
        Gdx.input.setInputProcessor(this);
        
		camera = new OrthographicCamera();
		h = (float) (480.0*((float)(Gdx.graphics.getHeight())/(float)(Gdx.graphics.getWidth())));

		camera.position.set(480 / 2, h / 2, 0);
		camera.update();
		
		batch = new SpriteBatch();
		
		initLevel();
		menuMusic.setLooping(true);
		menuMusic.play();
	}

	@Override
	public void hide() {}
	@Override
	public void pause() {}
	@Override
	public void resume() {}
	@Override
	public void dispose() {}
	@Override
	public boolean keyDown(int keycode) {return false;}
	@Override
	public boolean keyUp(int keycode) {return false;}
	@Override
	public boolean keyTyped(char character) {return false;}

}

