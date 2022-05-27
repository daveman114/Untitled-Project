package com.mygdx.game;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.MoveEnum.MovementDirection;

public class PlayerMovementManager {
	private TiledMapTileLayer CollisionLayer;
	private int tileWidth;
	private int tileHeight;
	private int PlayerWidth = 16;
	private int PlayerHeight = 16;
	public float RunSpeed = 50f, gravity = 30f, FastFall = 20, JumpSpeed = 30, MaxRunSpeed = 50f, MaxNegSpeed= 50f;
	public PlayerMovementManager(TiledMapTileLayer collisionLayer) {
		CollisionLayer = collisionLayer;
		tileWidth = collisionLayer.getTileWidth();
		tileHeight = collisionLayer.getTileHeight();
		
	}
	/** Checks to see if the player collides of the Y axis. Checks below the player.
	 @param PlayerX The X axis coordinate of the player.
	 @param PlayerY the Y axis coordinate of the player.
	 @return Weather or not the player collides on the Y axis (below the player).
	 */
	
	public boolean ColidesLowerY(float PlayerX, float PlayerY) {
		boolean ColLowerY = false;
		ColLowerY = CollisionLayer.getCell((int) (PlayerX/tileWidth), (int)PlayerY/tileHeight).getTile().getProperties().containsKey("CanCollide");
		//Bottom Left
		if(!ColLowerY)
			ColLowerY = CollisionLayer.getCell((int) ((PlayerX + PlayerWidth)/2)/tileWidth, (int)PlayerY/tileHeight).getTile().getProperties().containsKey("CanCollide");
		//Bottom Middle
		if(!ColLowerY)
			ColLowerY = CollisionLayer.getCell((int) (PlayerX + PlayerWidth)/tileWidth, (int)PlayerY/tileHeight).getTile().getProperties().containsKey("CanCollide");
		//Bottom right
		return ColLowerY;
		
	}
	/** Checks to see if the player collides of the Y axis. Checks above the player.
	 @param PlayerX The X axis coordinate of the player.
	 @param PlayerY the Y axis coordinate of the player.
	 @return Weather or not the player collides on the Y axis (above the player).
	 */
	
	public boolean CollidesUpperY(float PlayerX, float PlayerY) {
		boolean ColUpperY;
		ColUpperY = CollisionLayer.getCell((int) PlayerX/tileWidth,  (int)(PlayerY+PlayerHeight)/tileHeight).getTile().getProperties().containsKey("CanCollide");
		//Top Left
		if(!ColUpperY)
	ColUpperY = CollisionLayer.getCell((int)(PlayerX + (PlayerWidth/2))/tileWidth, (int)(PlayerY + PlayerHeight)/tileHeight).getTile().getProperties().containsKey("CanCollide");
		//Top middle
		if(!ColUpperY)
	ColUpperY = CollisionLayer.getCell((int)(PlayerX + PlayerWidth)/tileWidth, (int) (PlayerY + PlayerHeight)/tileHeight).getTile().getProperties().containsKey("CanCollide");
		//Top Right
		return ColUpperY;
	}
	/** Checks to see if the player collides of the X axis. Checks to the left of the player.
	 @param PlayerX The X axis coordinate of the player.
	 @param PlayerY the Y axis coordinate of the player.
	 @return Weather or not the player collides on the X axis (To the left of the player).
	 */
	public boolean CollidesLeftX(float PlayerX, float PlayerY) {
		boolean ColLeftX;
		ColLeftX = CollisionLayer.getCell((int) PlayerX/tileWidth, (int)((PlayerY+(PlayerHeight))/tileHeight)).getTile().getProperties().containsKey("CanCollide");
		//top left
		if(!ColLeftX)
		ColLeftX = CollisionLayer.getCell((int)PlayerX/ tileWidth, (int) ((int)(PlayerY+PlayerHeight/2)/tileHeight)).getTile().getProperties().containsKey("CanCollide");
		//middle left
		if(!ColLeftX)
		ColLeftX = CollisionLayer.getCell((int) PlayerX/tileWidth, (int)(PlayerY)/tileHeight).getTile().getProperties().containsKey("CanCollide");
		// bottom left
		return ColLeftX;
	}
	/** Checks to see if the player collides of the X axis. Checks to the right of the player.
	 @param PlayerX The X axis coordinate of the player.
	 @param PlayerY the Y axis coordinate of the player.
	 @return Weather or not the player collides on the X axis (To the right of the player).
	 */
	public boolean CollidesRightX(float PlayerX, float PlayerY) {
		boolean ColRightX;
		//If the player is going right it checks to see if it collides. l
			ColRightX = CollisionLayer.getCell((int) (PlayerX + PlayerWidth)/ tileWidth, (int) ((PlayerY + PlayerHeight)/tileHeight)).getTile().getProperties().containsKey("CanCollide");
			//top right
			if(!ColRightX)
				ColRightX = CollisionLayer.getCell((int) (PlayerX+PlayerWidth)/tileWidth, (int) ((PlayerY+(PlayerHeight/2))/tileHeight)).getTile().getProperties().containsKey("CanCollide");
			//Middle right
			if(!ColRightX) 
			ColRightX = CollisionLayer.getCell((int) (PlayerX+PlayerWidth)/tileWidth, (int) ((PlayerY)/tileHeight)).getTile().getProperties().containsKey("CanCollide");
			//Bottom Right
			return ColRightX;
		
	}
	
	public boolean run(float PlayerX, float PlayerY, Vector2 Velocity, MovementDirection Move, double delta) {
		int maxMoveSpeed= 50;
		switch(Move) {
		case LEFT: if(Velocity.x > 0)  {
			Velocity.x -= 300*delta;
			break;
			//If the player is going right, a huge boost in speed is given to make them go left faster. 
			//I hope this makes the game feel more responsive.
		}
		
	
			//TODO make it so that if the velocity is between 0 and 10 the player is given 100 velocity
			
			Velocity.x -= 100;
		
		
		case RIGHT: 
			


		
			
			
		}
		return true;
	}
	/** Makes the player either jump, or if the player is already jumping, increases the height of the existing jump.
	 @param PlayerX The X axis coordinate of the player.
	 @param PlayerY the Y axis coordinate of the player.
	 @param Move The direction in which the player is supposed to jump.
	 @param canJump Weather or not the player is able to start a jump. Should be true only while grounded.
	 @param airTime How long the player has been in the air.
	 @param delta The time since the last frame was rendered.
	 @param Velocity Contains the Velocity of the player.
	 @returns If the Player jumped/extended jump. 
	 */
	
	public void jump(float PlayerX, float PlayerY, MovementDirection Move, boolean canJump, double airTime, double delta, Vector2 Velocity, boolean IsJumping) {
		switch(Move) {
		case UP: if(canJump) {
			Velocity.y = JumpSpeed;
			break;
		}
		if(airTime < 0.5) {
			Velocity.y += JumpSpeed * delta;
			break;
		}
		break;
		
		case DOWN: if(canJump) {
			Velocity.y = -JumpSpeed;
			break;
		}
		if(airTime < 0.5) {
			Velocity.y -= JumpSpeed * delta;
			break;
		}
		break;
		
		case LEFT:  if(canJump) {
			Velocity.x = -JumpSpeed;
			break;
		}
		if(airTime < 0.5) {
			Velocity.x -= JumpSpeed * delta;
			break;
		}
		break;

		case RIGHT:  if(canJump) {
			Velocity.x = JumpSpeed;
			break;
		}
		if(airTime < 0.5) {
			Velocity.x += JumpSpeed * delta;
			break;
		}
		break;
		
		}
		
	}
}
