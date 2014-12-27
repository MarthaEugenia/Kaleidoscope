package Kaleidoscope;

import java.awt.Color;
import java.util.Observable;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This is the Model class for a figure. It is an Observable, which means that
 * it can notifyObservers that something in the model has changed, and they
 * should take appropriate actions.
 * 
 * @author David Matuszek
 * @author Theresa Breiner
 * @author Martha Trevino
 */
public class Model extends Observable {
	Random rn = new Random();

	public final int FIGURE_SIZE = rn.nextInt(30) + 50;
	private int xPosition = rn.nextInt(100) + 1;
	private int yPosition = rn.nextInt(100) + 1;
	private int xLimit, yLimit;
	final int SPEEDX = rn.nextInt(7) + 2;
	final int SPEEDY = rn.nextInt(7) + 2;
	private int xDelta = SPEEDX;
	private int yDelta = SPEEDY;
	private String shapeType;

	final float hue = rn.nextFloat();
	final float saturation = 0.9f; // 1.0 for brilliant, 0.0 for dull
	final float luminance = 1.0f; // 1.0 for brighter, 0.0 for black

	private Color myColor = Color.getHSBColor(hue, saturation, luminance);

	private Timer timer;

	/**
	 * Sets the "walls" that the figure should bounce off from.
	 * 
	 * @param xLimit
	 *            The position (in pixels) of the wall on the right.
	 * @param yLimit
	 *            The position (in pixels) of the floor.
	 */
	public void setLimits(int xLimit, int yLimit) {
		this.xLimit = (xLimit - FIGURE_SIZE) / 2;
		this.yLimit = (yLimit - FIGURE_SIZE) / 2;
		if (xPosition > 0) {
			xPosition = Math.min(xPosition, this.xLimit);
		} else {
			xPosition = Math.max(xPosition, -this.xLimit);
		}
		if (yPosition > 0) {
			yPosition = Math.min(yPosition, this.yLimit);
		} else {
			yPosition = Math.max(yPosition, -this.yLimit);
		}
	}

	/**
	 * @return The figure X position.
	 */
	public int getX() {
		return xPosition;
	}

	/**
	 * @return The figure Y position.
	 */
	public int getY() {
		return yPosition;
	}

	/**
	 * @return The figure color.
	 */
	public Color getColor() {
		return myColor;
	}

	/**
	 * Sets a new random color.
	 */
	public void setColor() {
		this.myColor = Color.getHSBColor(rn.nextFloat(), saturation, luminance);
	}

	/**
	 * Sets new speed.
	 */
	public void setSpeed(int percent) {
		xDelta = SPEEDX * percent / 100;
		yDelta = SPEEDY * percent / 100;
	}

	/**
	 * @return the shapeType.
	 */
	public String getShapeType() {
		return shapeType;
	}

	/**
	 * 
	 * @param type
	 *            The shape that this model should take
	 */
	public void setShapeType(String type) {
		shapeType = type;
	}

	/**
	 * Tells the figure to start moving. This is done by starting a Timer that
	 * periodically executes a TimerTask. The TimerTask then tells the figure to
	 * make one "step."
	 */
	public void start() {
		timer = new Timer(true);
		timer.schedule(new Strobe(), 0, 40); // 25 times a second
	}

	/**
	 * Tells the figure to stop where it is.
	 */
	public void pause() {
		timer.cancel();
	}

	/**
	 * Tells the figure to advance one step in the direction that it is moving.
	 * If it hits a wall, its direction of movement changes.
	 */
	public void makeOneStep() {
		// Do the work
		xPosition += xDelta;
		if (xPosition < -xLimit || xPosition >= xLimit) {
			xDelta = -xDelta;
			xPosition += xDelta;
		}

		yPosition += yDelta;
		if (yPosition < -yLimit || yPosition >= yLimit) {
			yDelta = -yDelta;
			yPosition += yDelta;
		}
		// Notify observers
		setChanged();
		notifyObservers();
	}

	/**
	 * Tells the model to advance one "step."
	 */
	private class Strobe extends TimerTask {
		@Override
		public void run() {
			makeOneStep();
		}
	}
}