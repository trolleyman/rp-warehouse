package warehouse.nxt;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import lejos.nxt.Button;
import lejos.nxt.Sound;
import lejos.util.Delay;

/**
 * A class for the robot interface
 */
public class RobotDisplay {

	// robot interface 
	private String robotName = "BOT1";
	private Graphics g;

	// about current state
	private boolean inDropOff = false; // Whether the robot is in drop off point
	private boolean inPickUp = false; // Whether the robot is in pick up point

	// about the job
	private int counter = 0;
	private int quantity = 5;
	private String jobName = "Job";
	private int weight = 2;
	private int completedJobs = 0; 
	
	// about localisation
	private int yCo = 40;
	private int xCo = 24;

	/*
	 public Display(String jobName, int weight, int quantity, int xCo, int yCo) {
	 
	 }
	 */
	public static void main(String[] args) {
		new RobotDisplay().show();
	}

	/**
	 * Shows the Van Gogh masterpiece on screen
	 */
	public void show() {
		Sound.setVolume(Sound.VOL_MAX / 4);
		g = new Graphics();

		// draws the home screen
		drawMainMenu();

		// just for testing now.. Press escape to quit.
		while (Button.ESCAPE.isUp()) {
			
			// just for testing now.. Press RIGHT to activate pick up
			if (Button.RIGHT.isDown()) {
				pickUp();
			}

			// just for testing now.. Press LEFT to activate drop off
			if (Button.LEFT.isDown()) {
				dropOff();
			}

			Thread.yield();
		} // Press ESCAPE to end robot
	}

	/**
	 * Shows the interface when arriving at the pick up point
	 */
	private void pickUp() {
		Sound.beepSequenceUp();
		this.inPickUp = true;

		// draws the screen during loading phase
		drawPickUpMenu();
		
		if (inPickUp) {
			// just for testing now.. Press ENTER to load item.
			while (!Button.ENTER.isDown()) {

				if (Button.RIGHT.isDown()) {
					g.clear();
					g.drawRect(5, 5, 90, 45);
					Sound.playTone(1000, 200);; // Added beeping sound
					if(counter < 50) {
						this.counter++;
						drawPickUpUpdate();
						Delay.msDelay(250);
					} else {  // LOL for fancy fancy. Shakes the text when user tries to go over limit.
						Sound.buzz();
						drawPickUpUpdate();
						Delay.msDelay(250);
					}
				}
				if (Button.LEFT.isDown()) {
					g.clear();
					g.drawRect(5, 5, 90, 45);
					Sound.playTone(500, 200);;  // Added beeping sound
					if(counter > 0) {
						this.counter--;
						drawPickUpUpdate();
						Delay.msDelay(250);
					} else { // Yup, again fancy fancy. Not really needed but for now just for fun.
						Sound.buzz();
						drawPickUpUpdate();
						Delay.msDelay(250);
					}
				}
				Thread.yield();
			}
			
			g.setFont(Font.getLargeFont());
			// Robot will check if items are loaded properly, and replies accordingly.
			if (counter == quantity) {
				g.clear();
				g.drawString("Thank", 49, 15, Graphics.HCENTER);
				g.drawString("You!", 49, 31, Graphics.HCENTER);
				Sound.beepSequenceUp();
			} else if (counter < quantity) {
				g.clear();
				g.drawString("Need", 49, 15, Graphics.HCENTER);
				g.drawString("More", 49, 31, Graphics.HCENTER);
				Sound.beepSequence();
			} else {
				g.clear();
				g.drawString("Over", 49, 6, Graphics.HCENTER);
				g.drawString("The", 49, 23, Graphics.HCENTER);
				g.drawString("Limit!", 49, 39, Graphics.HCENTER);
				Sound.beepSequence();
			}
			g.setFont(Font.getDefaultFont());
			
			Delay.msDelay(2000);
			g.clear();
			this.inPickUp = false;
			this.counter = 0;
		}
	}

	/**
	 * Shows the interface when reaching the drop off point
	 */
	public void dropOff() {
		Sound.beepSequence();
		this.inDropOff = true;
		g.clear();

		while (inDropOff) {
			g.drawRect(15, 10, 70, 40);
			g.drawString("Unload me!", 20, 25, 0);

			// just for testing now.. Press ENTER to unload items
			if (Button.ENTER.isDown()) {
				this.inDropOff = false;
				g.clear();
				g.setFont(Font.getLargeFont());
				g.drawString("You're", 49, 15, Graphics.HCENTER);
				g.drawString("Welcome!", 49, 31, Graphics.HCENTER);		
				g.setFont(Font.getDefaultFont());
				Sound.beepSequenceUp();
				Delay.msDelay(2000);
				g.clear();
			}
			Thread.yield();
		}
	}
	
	////////////////////////////////////////////////////////////////////////
	//////////////////////// HELPER METHODS ////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	private void drawMainMenu() {
		// Draws the robot name on top of the screen
		g.drawString(robotName, 49, 2, Graphics.HCENTER);
		
		// Draws the base
		g.drawRect(0, 0, 99, 10);
		g.drawRect(0, 10, 49, 53);
		g.drawRect(50, 10, 49, 53);
		
		// Draws the action box 
		g.setFont(Font.getSmallFont());
		g.drawString("ACTION", 26, 13, Graphics.HCENTER);
		g.drawRect(10, 20, 30, 30);
		g.drawString("(" + xCo + ", " + yCo + ")", 26, 54, Graphics.HCENTER);
		
		// Draws the job status box
		g.drawString("JOB STATUS", 76, 13, Graphics.HCENTER);
		g.drawString(" Current: ", 76, 27, Graphics.HCENTER);
		g.drawString(jobName, 76, 35, Graphics.HCENTER);
		g.drawString(" Completed: ", 76, 48, Graphics.HCENTER);
		g.drawString("" + completedJobs, 76, 55, Graphics.HCENTER);
		g.setFont(Font.getDefaultFont());
	}
	
	private void drawPickUpMenu() {
		g.clear();

		// Draws a box containing the information
		g.drawRect(5, 5, 90, 45);
		// How many items are needed to be loaded
		g.drawString("Required : " + String.valueOf(quantity), 10, 10, 0);
		// How many items are currently loaded
		g.drawString("Loaded   : " + String.valueOf(counter), 10, 20, 0); 

		g.drawString("Load me!", 10, 35, 0);
		
		// Some arrows to indicate increase or decrease load
		g.drawString("+", 75, 54, 0);
		g.drawString("-", 20, 54, 0);
		g.drawString(" ->", 80, 54, 0);
		g.drawString("<- ", 5, 54, 0);
	}
	
	private void drawPickUpUpdate() {
		// How many items are needed to be loaded
		g.drawString("Required : " + String.valueOf(quantity), 10, 10, 0);
		// 
		g.drawString("Loaded   : " + String.valueOf(counter), 10, 20, 0);
		g.drawString("W / item : " + String.valueOf(weight), 10, 30, 0);
		g.drawString("MaxLoad  : " + String.valueOf(50/weight), 10, 40, 0);
		g.drawString("+", 75, 54, 0);
		g.drawString("-", 20, 54, 0);
		g.drawString(" ->", 80, 54, 0);
		g.drawString("<- ", 5, 54, 0);
	}
}
