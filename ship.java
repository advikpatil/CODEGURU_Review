import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a ship.  It is displayed on the screen in the Nexus Defense game.
 * 
 * @author Michael Layzell
 * Made for CISC 124, Winter 2013
 */
public class Ship {
	
	// Possible values for the 'dir' variable
	public static final int DIR_UP = 0;
	public static final int DIR_DOWN = 1;
	public static final int DIR_LEFT = 2;
	public static final int DIR_RIGHT = 3;
	
	// Possible values for the 'type' variable
	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_FAST = 1;

	// The speeds of the normal and fast types
	private static final double NORMAL_SPEED = 0.05;
	private static final double FAST_SPEED = 0.1;
	
	// Base dimensions
	public static final int BASE_HALF_DIM = 20;
	public static final int HALF_DIM_SCALING = 2; 
	private static final int MAX_LINKS = 5;
	
	private static final Color linkColor = new Color(255, 156, 255, 50);
	
	// The position and type of the ship
	private double x, y;
	private int type;
	
	// The direction the ship is moving in
	private int dir;
	
	// The ships which this ship is linked to
	// The more links there are between ships, the stronger the ship
	private List<Ship> links = new ArrayList<Ship>();
	
	// The lifespan of the ship
	private long lifespan = 0;
	
	// The size of the ship
	private int half_dim = BASE_HALF_DIM;
	
	// Whether or not the ship is moving
	private boolean moving = true;
	
	// The game panel
	private GamePanel gamePanel;
	
	/**
	 * Create a ship with the following parameters
	 * @param x The x position of the ship on the screen
	 * @param y The y position of the ship on the screen
	 * @param dir The direction which the ship is moving
	 * @param type The type of the ship
	 */
	public Ship(int x, int y, int dir, int type, GamePanel gamePanel) {
		this.x = x;
		this.y = y;
		this.dir = dir;
		this.type = type;
		
		this.gamePanel = gamePanel;
	}
	
	/**
	 * STATIC FUNCTION
	 * Returns true if the Ships 'a' and 'b' are colliding
	 * @param a The first ship
	 * @param b The second ship
	 * @return Whether or not the two ships are colliding with eachother
	 */
	public static boolean areColliding(Ship a, Ship b) {
		int xDistance = (int) Math.abs(a.x - b.x);
		int yDistance = (int) Math.abs(a.y - b.y);
		
		if (xDistance <= a.half_dim + b.half_dim) {
			// Colliding in the x direction
			if (yDistance <= a.half_dim + b.half_dim) {
				// Colliding in the y direction
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * @return Whether or not the ship is moving (low enough # of links)
	 */
	public boolean isMoving() {
		return moving;
	}
	
	/**
	 * @return The Y-coordinate of the ship
	 */
	public double getY() {
		return y;
	}
	
	/**
	 * @return The X-coordinate of the ship
	 */
	public double getX() {
		return x;
	}
	
	/**
	 * Determines whether the point is on the ship
	 * @param xPt The X coordinate of the point
	 * @param yPt The Y coordinate of the point
	 * @return Whether or not these coordinates intersect with the ship
	 */
	public boolean pointOnShip(int xPt, int yPt) {
		int xDistance = (int) Math.abs(x - xPt);
		int yDistance = (int) Math.abs(y - yPt);
		
		return xDistance <= half_dim && yDistance <= half_dim;
	}
	
	/**
	 * Connects this ship and the ship 'other'
	 * @param other The other ship to connect to.
	 */
	public void link(Ship other) {
		if (!links.contains(other)) {
			links.add(other);
			other.link(this);
		}
		
		recalculateDimensions();
	}
	
	/**
	 * Disconnects this ship and the ship 'other'
	 * @param other The other ship to disconnect from.
	 */
	public void unlink(Ship other) {
		if (links.contains(other)) {
			links.remove(other);
			other.unlink(this);
		}
		
		recalculateDimensions();
	}
	
	/**
	 * @return The number of links which this ship has
	 */
	public int getNumLinks() {
		return links.size();
	}
	
	/**
	 * Causes the ship to bounce, changing direction
	 */
	public void bounce() {
		switch (dir) {
			case DIR_LEFT:
				dir = DIR_RIGHT;
				break;
			case DIR_RIGHT:
				dir = DIR_LEFT;
				break;
			case DIR_UP:
				dir = DIR_DOWN;
				break;
			case DIR_DOWN:
				dir = DIR_UP;
				break;
		}
	}
	/**
	 * Recalculates the dimensions based on the number of links.
	 * If the ship has enough links, causes it to stop moving.
	 */
	private void recalculateDimensions() {
		if (links.size() >= MAX_LINKS) // Stop moving if you have too many links
			moving = false;
		else
			moving = true;
		
		half_dim = BASE_HALF_DIM + HALF_DIM_SCALING * Math.min(links.size(), 3);
	}

	/**
	 * Update the position etc. of the Ship 'ship'
	 * @param delta The time in millis since the last tick
	 */
	public void tick(long delta) {
		lifespan += delta;
		
		// Move the ship
		if (moving) {
			int distance = (int) (delta * NORMAL_SPEED);
			
			if (type == TYPE_FAST)
				distance = (int) (delta * FAST_SPEED);
			
			switch (dir) {
				case DIR_UP:
					y -= distance;
					break;
				case DIR_DOWN:
					y += distance;
					break;
				case DIR_LEFT:
					x -= distance;
					break;
				case DIR_RIGHT:
					x += distance;
					break;
			}
		}
		
		// Check that I am within the panel - and ensure I stay that way.
		if (x - half_dim <= 0 && dir == DIR_LEFT)
			dir = DIR_RIGHT;
		
		if (x + half_dim >= gamePanel.getWidth()) {
			x = gamePanel.getWidth() - half_dim;
			if (dir == DIR_RIGHT)
				dir = DIR_LEFT;
		}
		
		if (y - half_dim <= 0 && dir == DIR_UP)
			dir = DIR_DOWN;
		
		if (y + half_dim >= gamePanel.getHeight()) {
			y = gamePanel.getHeight() - half_dim;
			if (dir == DIR_DOWN)
				dir = DIR_UP;
		}
	}
	
	/**
	 * Paints the links between this ship and other ships to the graphics context represented by 'g'
	 * @param g The graphics context
	 */
	public void paint_links(Graphics2D g) {
		// Get the style for the line
		g.setColor(linkColor);
		g.setStroke(new BasicStroke((lifespan / 40) % 15 + 5));
		
		// Draw a line for the connection to each ship
		for (Ship other : links)
			g.drawLine((int) x, (int) y, (int) other.x, (int) other.y);
	}
	
	/**
	 * Paints the ship to the graphics context represented by 'g'
	 * @param g The graphics context
	 */
	public void paint(Graphics2D g) {
		BufferedImage img = null;
		
		if (type == TYPE_FAST) {
			if (moving)
				img = SpriteManager.getSprite(SpriteManager.DRONE_FAST);
			else
				img = SpriteManager.getSprite(SpriteManager.DRONE_FAST_STILL);
		} else {
			if (moving)
				img = SpriteManager.getSprite(SpriteManager.DRONE_NORMAL);
			else
				img = SpriteManager.getSprite(SpriteManager.DRONE_NORMAL_STILL);
		}
		
		g.drawImage(img, (int) x - half_dim, (int) y - half_dim, 2 * half_dim, 2 * half_dim, null);
	}

	/**
	 * Destroys the ship
	 */
	public void dispose() {
		List<Ship> oldLinks = links;
		links = new ArrayList<Ship>(); // Make empty array list as dummy - we don't want Comodification Exception
		
		// Destroy all links
		for (Ship ship : oldLinks)
			ship.unlink(this);
		
		// Create an explosion
		EffectManager.getEffectManager().addEffect(new Explosion((int) x, (int) y));
	}

}