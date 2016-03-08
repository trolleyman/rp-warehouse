package warehouse.nxt.display;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import lejos.nxt.Button;
import lejos.nxt.Sound;
import lejos.util.Delay;

public class NXTInterface {

	private Graphics g; 					// robot interface

	private boolean inDropOff = false;		// Whether the robot is in drop off point
	private boolean inPickUp = false;		// Whether the robot is in pick up point
	
	private int completedJobs = 0;			// private int counter = 0; not needed now

	private String robotName;				// Information passed though about the robot's job and location
	private int quantity;
	private String jobName;
	private int weight;
	private int y;
	private int x;

	int action = 0;							// For Drawing

	//For testing without client info (test values)
	public NXTInterface( String _robotName, String _jobName, int _weight, int _quantity, int _x, int _y ) {
		this.robotName = _robotName;
		this.jobName = _jobName;
		this.weight = _weight;
		this.quantity = _quantity;
		this.x = _x;
		this.y = _y;
	}
	
	//For testing with client info (like the final design)
	public NXTInterface( String _robotName, int _x, int _y ) {
		this.robotName = _robotName;
		this.jobName = "None";
		this.x = _x;
		this.y = _y;
	}

	// prints main Interface
	public void show() {
		Sound.setVolume( Sound.VOL_MAX / 4 );
		g = new Graphics();

		drawMainMenu();

		while( Button.ESCAPE.isUp() ) {
			if( Button.ENTER.isDown() ) {
				g.clear();
				Delay.msDelay(500);
				action++;
				drawMainMenuUpdate();
			}

			if( inPickUp ) { pickUp( this.quantity, this.weight); }

			if( inDropOff ) { dropOff(); }

			Thread.yield();
		}
	}

	// displays Pick Up Interface
	public void pickUp( int _quantity, int _weight ) {
		
		this.inPickUp = true;
		
		int counter = 0;
		this.quantity = _quantity;
		this.weight = _weight;
		
		Sound.beepSequenceUp();
		
		g.setFont( Font.getDefaultFont() );

		drawPickUpMenu( counter );

		if( inPickUp ) {
			while( !Button.ENTER.isDown() ) {

				if( Button.ESCAPE.isDown() ) { // Escape Is Down? Then we should cancel the job right?
					this.inPickUp = false;
					g.clear();
					g.setFont( Font.getLargeFont() );
					g.drawString( "At", 49, 6, Graphics.HCENTER );
					g.drawString( "Wrong", 49, 23, Graphics.HCENTER );
					g.drawString( "Location!", 49, 39, Graphics.HCENTER );
					Sound.beepSequence();
					Delay.msDelay( 2000 );
					g.setFont( Font.getDefaultFont() );
					g.clear();
					counter = -1;
					break;

				}
				
				if( Button.RIGHT.isDown() ) {
					g.clear();
					g.drawRect( 5, 5, 90, 45 );
					Sound.playTone( 1000, 200 );
					
					if( counter < ( 50 / weight ) ) {
						counter++;
						drawPickUpUpdate( counter );
						Delay.msDelay( 250 );
					}
					else {
						Sound.buzz();
						drawPickUpUpdate( counter );
						Delay.msDelay( 250 );
					}
				}
				if( Button.LEFT.isDown() ) {
					g.clear();
					g.drawRect( 5, 5, 90, 45 );
					Sound.playTone( 500, 200 );
					
					if( counter > 0 ) {
						counter--;
						drawPickUpUpdate( counter );
						Delay.msDelay( 250 );
					}
					else {
						Sound.buzz();
						drawPickUpUpdate( counter );
						Delay.msDelay( 250 );
					}
				}
				Thread.yield();
			}

			// Robot will check if items are loaded properly, and replies
			// accordingly.
			if( counter < 0 ) { drawMainMenuUpdate(); }
			
			else {

				g.setFont( Font.getLargeFont() );
				if( counter == quantity ) {
					g.clear();
					g.drawString( "Thank", 49, 15, Graphics.HCENTER );
					g.drawString( "You!", 49, 31, Graphics.HCENTER );
					this.inPickUp = false;
					Sound.beepSequenceUp();
					Delay.msDelay( 2000 );
					g.clear();
					g.setFont( Font.getDefaultFont() );
					drawMainMenu();
				}
				else if( counter < quantity ) {
					g.clear();
					g.drawString( "Need", 49, 15, Graphics.HCENTER );
					g.drawString( "More", 49, 31, Graphics.HCENTER );
					Sound.beepSequence();
					Delay.msDelay( 2000 );
					pickUp( quantity, weight );
				}
				else {
					g.clear();
					g.drawString( "Over", 49, 6, Graphics.HCENTER );
					g.drawString( "The", 49, 23, Graphics.HCENTER );
					g.drawString( "Limit!", 49, 39, Graphics.HCENTER );
					Sound.beepSequence();
					Delay.msDelay( 2000 );
					pickUp( quantity, weight );
				}
			}

			// counter = 0;
		}
	}

	// Displays Drop Off Interface
	public void dropOff() {
		Sound.beepSequence();
		this.inDropOff = true;
		g.clear();

		while( inDropOff ) {
			g.drawRect( 15, 10, 70, 40 );
			g.drawString( "Unload me!", 20, 25, 0 );

			// just for testing now.. Press ENTER to unload items
			if( Button.ENTER.isDown() ) {
				this.inDropOff = false;
				g.clear();
				g.setFont( Font.getLargeFont() );
				g.drawString( "You're", 49, 15, Graphics.HCENTER );
				g.drawString( "Welcome!", 49, 31, Graphics.HCENTER );
				g.setFont( Font.getDefaultFont() );
				completedJobs++;
				Sound.beepSequenceUp();
				Delay.msDelay( 2000 );
				g.clear();
			}
			Thread.yield();
		}
		action = 0;
		drawMainMenuUpdate();
	}

	////////////////////////////////////////////////////////////////////////
	//////////////////////// HELPER METHODS ////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	private void drawMainMenu() {
		// Draws the robot name on top of the screen
		g.drawString( robotName, 49, 2, Graphics.HCENTER );

		// Draws the base
		g.drawRect( 0, 0, 99, 10 );
		g.drawRect( 0, 10, 49, 53 );
		g.drawRect( 50, 10, 49, 53 );

		// Draws the action box
		g.setFont( Font.getSmallFont() );
		g.drawString( "ACTION", 26, 13, Graphics.HCENTER );
		g.drawRect( 10, 20, 30, 30 );
		g.drawString( "(" + x + ", " + y + ")", 26, 54, Graphics.HCENTER );

		// Draws the job status box
		g.drawString( "JOB STATUS", 76, 13, Graphics.HCENTER );
		g.drawString( " Current: ", 76, 27, Graphics.HCENTER );
		g.drawString( jobName, 76, 35, Graphics.HCENTER );
		g.drawString( " Completed: ", 76, 48, Graphics.HCENTER );
		g.drawString( "" + completedJobs, 76, 55, Graphics.HCENTER );
		g.setFont( Font.getDefaultFont() );
	}

	private void drawMainMenuUpdate() {
		drawMainMenu();

		int label = action % 5;
		switch( label ) {		
			case 0 : g.setFont( Font.getLargeFont() ); g.drawChar( 'I', 22, 28, 0 ); g.setFont( Font.getDefaultFont() ); break;
			case 1 : g.drawLine( 25, 25, 15, 32 ); g.drawLine( 25, 25, 35, 32 ); g.drawLine( 25, 25, 25, 45 ); break;
			case 2 : g.drawLine( 15, 35, 22, 28 ); g.drawLine( 15, 35, 22, 42 ); g.drawLine( 15, 35, 35, 35 ); break;
			case 3 : g.drawLine( 35, 35, 28, 28 ); g.drawLine( 35, 35, 28, 42 ); g.drawLine( 15, 35, 35, 35 ); break;
			case 4 : g.drawLine( 25, 45, 15, 38 ); g.drawLine( 25, 45, 35, 38 ); g.drawLine( 25, 25, 25, 45 ); break;
			default: g.drawString( "How the heck does this happen?", 1, 5, 0 );
		}
	}

	private void drawPickUpMenu( int _counter ) {
		g.clear();

		g.drawRect( 5, 5, 90, 45 ); // Draws a box containing the information
		g.drawString( "Required : " + String.valueOf( quantity ), 10, 10, 0 ); // How many items are needed to be loaded
		g.drawString( "Loaded   : " + String.valueOf( _counter ), 10, 20, 0 ); // How many items are currently loaded

		g.drawString( "Load me!", 10, 35, 0 );

		// Some arrows to indicate increase or decrease load
		g.drawString( "+", 75, 54, 0 );
		g.drawString( "-", 20, 54, 0 );
		g.drawString( " ->", 80, 54, 0 );
		g.drawString( "<- ", 5, 54, 0 );
	}

	private void drawPickUpUpdate( int _counter ) {
		g.drawString( "Required : " + String.valueOf(quantity), 10, 10, 0 );
		g.drawString( "Loaded   : " + String.valueOf(_counter), 10, 20, 0 );
		g.drawString( "W / item : " + String.valueOf(weight), 10, 30, 0 );
		g.drawString( "MaxLoad  : " + String.valueOf(50 / weight), 10, 40, 0 );
		g.drawString( "+", 75, 54, 0 );
		g.drawString( "-", 20, 54, 0 );
		g.drawString( " ->", 80, 54, 0 );
		g.drawString( "<- ", 5, 54, 0 );
	}

	public void setDropOff( boolean _value) { inDropOff = _value; }

	public void setPickUp( boolean _value ) { inPickUp = _value; }

	public void setJobName( String _name) { jobName = _name; }

	public void setQuantity( int _quantity ) { quantity = _quantity; }

	public void setWeight( int _weight ) { weight = _weight; }

	public void directionUpdate( String _direction ) {
		g.clear();
		
		switch( _direction ) {
			case "Forward" 	: action = 1; break;
			case "Left" 	: action = 2; break;
			case "Right" 	: action = 3; break;
			case "Backward" : action = 4; break;
			default 		: action = 0; 
		}
				
		drawMainMenuUpdate();
	}
	
	public void updatePosition( int _x, int _y ) { this.x = _x; this.y = _y; }
	
}
