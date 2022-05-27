package com.mygdx.game;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.MoveEnum.MovementDirection;

public class player extends Sprite implements InputProcessor {
	private Vector2 PlayerVelocity = new Vector2();
	//Creates a vector used to store Velocity.
	public float deltaFailSafe;
	public float RunSpeed = 50f, gravity = 30f, FastFall = 20, JumpSpeed = 100, MaxRunSpeed = 50f, MaxNegSpeed= 50f;
	private float timeInAir  = 0;
	/**Is true when the player is using inputs to move the player on the X axis*/
	boolean isMovingX=false;
	/**Is true when the player is using inputs to move the player on the Y axis*/
	boolean isMovingY=false;
	/**A global reference to a collisionLayer from a TiledMap. Used extensively for checking collision.*/
	private TiledMapTileLayer collisionLayer;
	protected enum GravEnum {
		DOWN, UP, LEFT, RIGHT
	}
	
	//Although the values are the same as the gravity Enum, I keep this one separate. 
	//It tracks which direction the player wants to move.
    private GravEnum Gravity; 
    public float tileWidth;
    public float tileHeight;
    private PlayerMovementManager PlayerChecker;
    private boolean isJumping = false;

	
public player(Sprite sprite, TiledMapTileLayer collisionLayer) {
	super(sprite);
	this.collisionLayer = collisionLayer;
	setX(200);setY(210);
	tileWidth = collisionLayer.getTileWidth();
	tileHeight = collisionLayer.getTileHeight();
	setOrigin((getWidth()/2), (getHeight()/2));
	Gravity = GravEnum.DOWN;
	PlayerChecker = new PlayerMovementManager(collisionLayer);
	}
public void draw(Batch spritebatch){
	update(Gdx.graphics.getDeltaTime());
	super.draw(spritebatch);
	}
public void update(float delta) {
	//A kind man named James over on discord helped me with this. I stops the player from falling through the floor when resizing the window. Thanks James.
	deltaFailSafe = Math.min(Gdx.graphics.getDeltaTime(), 1/10f);
	preformGravityCheck();
	boolean IsGrounded = CheckIfGrounded();
	if(!IsGrounded) {
		timeInAir += deltaFailSafe;
	}
	else {
		timeInAir = 0;
	}
	float OldX = getX();
	float OldY = getY();
	ApplyGravity(deltaFailSafe);
	ProcessInputs(deltaFailSafe, PlayerVelocity, IsGrounded);
	setY(getY()+ (PlayerVelocity.y*deltaFailSafe));
	if(PlayerVelocity.y < 0) {
		if(PlayerChecker.ColidesLowerY(getX(), getY())) {
			PlayerVelocity.y = 0;
			setY(OldY);
		}
	}
	//Checks if the player collides on the Y below above the player.
	
	if(PlayerVelocity.y > 0) {
		if(PlayerChecker.CollidesUpperY(getX(), getY())) {
			PlayerVelocity.y = 0;
			setY(OldY);
		}
	}
	//Checks if the player collides on the Y below below the player.
	
	setX(getX()+ (PlayerVelocity.x * deltaFailSafe));
	
	
	if(PlayerVelocity.x > 0) {
		if(PlayerChecker.CollidesRightX(getX(), getY())) {
			PlayerVelocity.x = 0;
			setX(OldX);
		}
	}
	//Checks if the player collides on the X to the right of the player.
	if(PlayerVelocity.x < 0) {
		if(PlayerChecker.CollidesLeftX(getX(), getY())) {
			PlayerVelocity.x = 0;
			setX(OldX);
		}
	}
	if(!isMovingY) {
		PlayerVelocity.y -= (PlayerVelocity.y*deltaFailSafe)*5;
	}
	if(!isMovingX) {
		PlayerVelocity.x -= (PlayerVelocity.x*deltaFailSafe)*5;
	}
	
}
		
	
	
/** Checks to see what keys are being pressed and updates the Player's velocity accordingly. This also determines wether or not the player is affected by friction.
@param DeltaTime The Time since the last frame.
*/
	private void ProcessInputs(double DeltaTime, Vector2 PlayerVelocity, boolean isGrounded) {
		boolean CanMoveLeft, CanMoveRight, CanMoveUp, CanMoveDown;
		
		boolean xMovGrav=false;
		boolean yMovGrav=false;
		isMovingX=false;
		isMovingY=false;
		
		switch(Gravity) {
		case DOWN: isMovingY=true;
		break;
		case RIGHT: isMovingX=true;
		break;
		case LEFT: isMovingX=true;
		break;
		case UP: isMovingY=true;
		break;
		}
		//Related to weather or not the player will have friction applied to them. 
		//If the player's gravity is UP or DOWN, friction will not be applied on the Y axis. 
		//Likewise, is the gravity is LEFT or RIGHT, friction will not be applied on the X axis.
		
	if(Gdx.input.isKeyPressed(Input.Keys.I)) {
			System.out.println();
			
			//I have set a breakpoint to trigger for this. Used for debugging.
		}
		
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			xMovGrav=true;
			switch(Gravity) {
			case DOWN: PlayerVelocity.x -=RunSpeed*DeltaTime;
			break;
			case RIGHT: PlayerChecker.jump(getX(), getY(), MovementDirection.LEFT, isGrounded, timeInAir, DeltaTime, PlayerVelocity, isJumping);
			break;
			case LEFT: PlayerVelocity.x -= FastFall*DeltaTime;
			break;
			case UP: PlayerVelocity.x -=RunSpeed*DeltaTime;
			break;
			}
		}
		
		
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
				xMovGrav = true;
				switch(Gravity) {
				case DOWN: PlayerVelocity.x +=RunSpeed*DeltaTime;
				break;
				case RIGHT: PlayerVelocity.x += FastFall*DeltaTime;
				break;
				case LEFT: PlayerChecker.jump(getX(), getY(), MovementDirection.RIGHT, isGrounded, timeInAir, DeltaTime, PlayerVelocity, isJumping);
				break;
				case UP: PlayerVelocity.x +=RunSpeed*DeltaTime;
				break;
				}
		}
		
		if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
			yMovGrav = true;
			switch(Gravity) {
				case DOWN: PlayerChecker.jump(getX(), getY(), MovementDirection.UP, isGrounded, timeInAir, DeltaTime, PlayerVelocity, isJumping);
				break;
				case RIGHT: PlayerVelocity.y += RunSpeed*DeltaTime;
				break;
				case LEFT: PlayerVelocity.y += RunSpeed*DeltaTime;					
				break;
				case UP: PlayerVelocity.y +=FastFall *DeltaTime;
				break;
					}
		}
		if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			yMovGrav=true;
			switch(Gravity) {
				case DOWN: PlayerVelocity.y -=FastFall*DeltaTime;
				break;
				case RIGHT: PlayerVelocity.y -= RunSpeed*DeltaTime;
				break;
				case LEFT: PlayerVelocity.y -= RunSpeed*DeltaTime;					
				break;
				case UP: PlayerChecker.jump(getX(), getY(), MovementDirection.DOWN, isGrounded, timeInAir, DeltaTime, PlayerVelocity, isJumping);
				break;
					}
		}
		if(xMovGrav) {
			if(Gravity == GravEnum.UP|| Gravity==GravEnum.DOWN) {
				isMovingX = true;
			}
		}
		if(yMovGrav) {
			if(Gravity == GravEnum.LEFT|| Gravity==GravEnum.RIGHT) {
				isMovingY = true;
			}
		}
		
	
	}
	

	private boolean CheckIfGrounded() {
	boolean isGrounded = false;
		switch(Gravity) {
	case DOWN: isGrounded = collisionLayer.getCell((int) ((getX()+1)/tileWidth), (int)((getY()-1)/tileHeight)).getTile().getProperties().containsKey("CanCollide");
	//Bottom Left
	if(!isGrounded)
		isGrounded = collisionLayer.getCell((int) ((getX() + (getWidth())/2)/tileWidth), (int)((getY()-1)/tileHeight)).getTile().getProperties().containsKey("CanCollide");
	//Bottom Middle
	if(!isGrounded)
		isGrounded = collisionLayer.getCell((int) (((getX()-1) + getWidth())/tileWidth), (int)((getY()-1)/tileHeight)).getTile().getProperties().containsKey("CanCollide");
	//Bottom right 
		break;
	case UP: 
		isGrounded = collisionLayer.getCell((int)((getX()+1)/tileWidth),  (int)((getY()+tileHeight+1)/tileHeight)).getTile().getProperties().containsKey("CanCollide");
	//Top Left
	if(!isGrounded)
		isGrounded = collisionLayer.getCell((int)((getX() + (tileWidth/2))/tileWidth), (int)(((getY() + tileHeight+1))/tileHeight)).getTile().getProperties().containsKey("CanCollide");
	//Top middle
	if(!isGrounded)
		isGrounded = collisionLayer.getCell((int)((getX() + tileWidth-1)/tileWidth), (int) ((getY() + tileHeight+1)/tileHeight)).getTile().getProperties().containsKey("CanCollide");
	//Top Right 
		break;
	case LEFT: isGrounded = collisionLayer.getCell((int) ((getX()-1)/tileWidth), (int)(((getY()-1)+(getHeight()))/tileHeight)).getTile().getProperties().containsKey("CanCollide");
	//top left
	if(!isGrounded)
		isGrounded = collisionLayer.getCell((int)((getX()-1)/ tileWidth), (int) ((int)(getY()+getHeight()/2)/tileHeight)).getTile().getProperties().containsKey("CanCollide");
	//middle left
	if(!isGrounded)
		isGrounded = collisionLayer.getCell((int) ((getX()-1)/tileWidth), (int)((getY()+1)/tileHeight)).getTile().getProperties().containsKey("CanCollide");
	// bottom left
		
		break;
	case RIGHT: 
		isGrounded = collisionLayer.getCell((int) (((getX()+1) + getWidth())/ tileWidth), (int) (((getY()-1) + getHeight())/tileHeight)).getTile().getProperties().containsKey("CanCollide");
		//top right
		if(!isGrounded)
			isGrounded = collisionLayer.getCell((int) ((getX()+1+getWidth())/tileWidth), (int) ((getY()+(getHeight()/2))/tileHeight)).getTile().getProperties().containsKey("CanCollide");
		//Middle right
		if(!isGrounded) 
			isGrounded = collisionLayer.getCell(((int) ((getX()+1+getWidth())/tileWidth)), (int) ((getY()+1)/tileHeight)).getTile().getProperties().containsKey("CanCollide");
		//Bottom Right
		
		
		break;
	default: System.out.println("You fu*ked up");
	}
	return isGrounded;
}
	private void ApplyGravity(float DeltaTime) {
		switch(Gravity) {
		case DOWN: PlayerVelocity.y -= gravity * DeltaTime;
		break;
		case UP: PlayerVelocity.y += gravity * DeltaTime;
		break;
		case LEFT: PlayerVelocity.x -= gravity * DeltaTime;
		break;
		case RIGHT: PlayerVelocity.x += gravity * DeltaTime;
		break;
				}
		}	

	/**Checks if the player is touching a valid block that can change gravity. If the player is, the gravity is changed and the respective "since" timer is reset.*/
	private void preformGravityCheck() {
		boolean TouchingLower;
		boolean TouchingUpper;
		boolean TouchingLeft;
		boolean TouchingRight;
		float tileWidth = collisionLayer.getTileWidth(), tileHeight = collisionLayer.getTileHeight();
		
	
		TouchingLeft = collisionLayer.getCell((int) ((getX()-1)/tileWidth), (int)(((getY()-1)+getHeight())/tileHeight)).getTile().getProperties().containsKey("CanChangeGravity");
		//top left
		if(!TouchingLeft)
		TouchingLeft = collisionLayer.getCell((int)((getX()-1)/ tileWidth), (int) ((int)(getY()+getHeight()/2)/tileHeight)).getTile().getProperties().containsKey("CanChangeGravity");
		//middle left 
		if(!TouchingLeft)
		TouchingLeft = collisionLayer.getCell((int) ((getX()-1)/tileWidth), (int)((getY()+1)/tileHeight)).getTile().getProperties().containsKey("CanChangeGravity");
		// bottom left
		
		TouchingRight = collisionLayer.getCell((int) (((getX()+1) + getWidth())/ tileWidth), (int) (((getY()-1) + getHeight() )/tileHeight)).getTile().getProperties().containsKey("CanChangeGravity");
		//top right
		if(!TouchingRight)
			TouchingRight = collisionLayer.getCell((int) (((getX()+1)+getWidth())/tileWidth), (int) ((getY()+(getHeight()/2))/tileHeight)).getTile().getProperties().containsKey("CanChangeGravity");
		//Middle right
		if(!TouchingRight) 
			TouchingRight = collisionLayer.getCell(((int) (((getX()+1)+getWidth())/tileWidth)), (int) ((getY()+1)/tileHeight)).getTile().getProperties().containsKey("CanChangeGravity");
		//Bottom Right
		
		
		TouchingLower = collisionLayer.getCell((int) (((getX()+1)/tileWidth)), (int)((getY()-1)/tileHeight)).getTile().getProperties().containsKey("CanChangeGravity");
		//Bottom Left
		if(!TouchingLower)
			TouchingLower = collisionLayer.getCell((int) ((getX() + (getWidth())/2)/tileWidth), (int)((getY()-1)/tileHeight)).getTile().getProperties().containsKey("CanChangeGravity");
		//Bottom Middle
		if(!TouchingLower)
			TouchingLower = collisionLayer.getCell((int) (((getX()-1) + getWidth())/tileWidth), (int)((getY()-1)/tileHeight)).getTile().getProperties().containsKey("CanChangeGravity");
		//Bottom right
		
		
		TouchingUpper = collisionLayer.getCell((int)((getX()+1)/tileWidth),  (int)((getY()+tileHeight+1)/tileHeight)).getTile().getProperties().containsKey("CanChangeGravity");
		//Top Left
			if(!TouchingUpper)
				TouchingUpper = collisionLayer.getCell((int)((getX() + 1 +(tileWidth/2))/tileWidth),  (int) (((getY() + tileHeight+1))/tileHeight)).getTile().getProperties().containsKey("CanChangeGravity");
		//Top middle
			if(!TouchingUpper)
				TouchingUpper = collisionLayer.getCell((int)((getX()-1 + tileWidth)/tileWidth), (int) ((getY() + tileHeight+1)/tileHeight)).getTile().getProperties().containsKey("CanChangeGravity");
         if(TouchingLeft) {
        		 if((Gravity == GravEnum.LEFT) == true) {}
        		 else {
        			 Gravity = GravEnum.LEFT;
             		//TimeSinceLeft = 0; 
        		 }
        		 
        	 }
         if(TouchingRight) {
        	 if((Gravity == GravEnum.RIGHT) == true ) {}
    		 else {
    			 Gravity = GravEnum.RIGHT;
         		//TimeSinceRight = 0; 
    		 }
        	 }
         
         if(TouchingLower) {
        		 if((Gravity == GravEnum.DOWN) == true ) {}
        		 else {
        			 Gravity = GravEnum.DOWN;
             		//TimeSinceDown = 0; 
        		 }
        	 }
         
         if(TouchingUpper) {
        	 
        		 if((Gravity == GravEnum.UP) == true ) {}
        		 else {
        			 Gravity = GravEnum.UP;
             	//	TimeSinceUp = 0; 
        		 }
         }
        	 
         }
		
				
		// TODO Auto-generated method stub
		

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean scrolled(float amountX, float amountY) {
		// TODO Auto-generated method stub
		return false;
	}

}

