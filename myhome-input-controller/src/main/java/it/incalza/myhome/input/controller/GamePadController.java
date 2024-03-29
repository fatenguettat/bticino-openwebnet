package it.incalza.myhome.input.controller;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import net.java.games.input.Component;
import net.java.games.input.Component.POV;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Rumbler;

public class GamePadController
{
	private static final Logger logger = Logger.getLogger(InputController.class);
	private static final float _x0 = 0.003921628f;
	private static final float _x1 = -1.5258789E-5f;
	private static final float _y0 = -0.019607842f;
	private static final float _y1 =  0.015518427f;
	public static final int NUM_BUTTONS = 20;

	// public stick and hat compass positions
	public static final int NUM_COMPASS_DIRS = 9;

	public static final int NW = 0;
	public static final int NORTH = 1;
	public static final int NE = 2;
	public static final int WEST = 3;
	public static final int NONE = 4; // default value
	public static final int EAST = 5;
	public static final int SW = 6;
	public static final int SOUTH = 7;
	public static final int SE = 8;
	public static final int ON = 100;
	public static final int OFF = 101;

	private Controller controller;

	private Component[] comps; // holds the components

	// comps[] indices for specific components
	private int xAxisIdx, yAxisIdx, zAxisIdx, rzAxisIdx;
	// indices for the analog sticks axes
	private int povIdx; // index for the POV hat
	private int buttonsIdx[]; // indices for the buttons

	private Rumbler[] rumblers;
	private int rumblerIdx; // index for the rumbler being used
	private boolean rumblerOn = false; // whether rumbler is on or off
	private boolean on = false;

	public GamePadController()
	{
		// get the controllers
		ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment();
		Controller[] cs = ce.getControllers();
		if (cs.length == 0)
		{
			System.out.println("No controllers found");
			System.exit(0);
		}
		else logger.info("Num. controllers: " + cs.length);

		// get the game pad controller
		controller = findGamePad(cs);
		logger.info("Selected Game controller: " + controller.getName() + ", " + controller.getType());

		// collect indices for the required game pad components
		findCompIndices(controller);

		findRumblers(controller);
	} // end of GamePadController()

	private Controller findGamePad(Controller[] cs)
	/*
	 * Search the array of controllers until a suitable game pad
	 * controller is found (eith of type GAMEPAD or STICK).
	 */
	{
		Controller.Type type;
		int i = 0;
		while (i < cs.length)
		{
		  	logger.info(i +") Game controller: " + cs[i].getName() + ", " + cs[i].getType());
			type = cs[i].getType();
			if ((type == Controller.Type.GAMEPAD) || (type == Controller.Type.STICK)) break;
			i++;
		}

		if (i == cs.length)
		{
			logger.info("No game pad found");
			System.exit(0);
		}
		else logger.info("Game pad index: " + i);

		return cs[i];
	} // end of findGamePad()

	private void findCompIndices(Controller controller)
	/*
	 * Store the indices for the analog sticks axes
	 * (x,y) and (z,rz), POV hat, and
	 * button components of the controller.
	 */
	{
		comps = controller.getComponents();
		if (comps.length == 0)
		{
			logger.info("No Components found");
			System.exit(0);
		}
		else logger.info("Num. Components: " + comps.length);

		// get the indices for the axes of the analog sticks: (x,y) and (z,rz)
		xAxisIdx = findCompIndex(comps, Component.Identifier.Axis.X, "x-axis");
		yAxisIdx = findCompIndex(comps, Component.Identifier.Axis.Y, "y-axis");

		zAxisIdx = findCompIndex(comps, Component.Identifier.Axis.Z, "z-axis");
		rzAxisIdx = findCompIndex(comps, Component.Identifier.Axis.RZ, "rz-axis");

		// get POV hat index
		povIdx = findCompIndex(comps, Component.Identifier.Axis.POV, "POV hat");

		findButtons(comps);
	} // end of findCompIndices()

	private int findCompIndex(Component[] comps, Component.Identifier id, String nm)
	/*
	 * Search through comps[] for id, returning the corresponding
	 * array index, or -1
	 */
	{
		Component c;
		for (int i = 0; i < comps.length; i++)
		{
			c = comps[i];
			if ((c.getIdentifier() == id) && !c.isRelative())
			{
				logger.info("Found " + c.getName() + "; index: " + i);
				return i;
			}
		}

		logger.info("No " + nm + " component found");
		return -1;
	} // end of findCompIndex()

	private void findButtons(Component[] comps)
	/*
	 * Search through comps[] for NUM_BUTTONS buttons, storing
	 * their indices in buttonsIdx[]. Ignore excessive buttons.
	 * If there aren't enough buttons, then fill the empty spots in
	 * buttonsIdx[] with -1's.
	 */
	{
		buttonsIdx = new int[NUM_BUTTONS];
		int numButtons = 0;
		Component c;

		for (int i = 0; i < comps.length; i++)
		{
			c = comps[i];
			if (isButton(c))
			{ // deal with a button
				if (numButtons == NUM_BUTTONS) // already enough buttons
				logger.info("Found an extra button; index: " + i + ". Ignoring it");
				else
				{
					buttonsIdx[numButtons] = i; // store button index
					logger.info("Found " + c.getName() + "; Ientifier: " + c.getIdentifier().getName() + "; index: " + i);
					numButtons++;
				}
			}
		}

		// fill empty spots in buttonsIdx[] with -1's
		if (numButtons < NUM_BUTTONS)
		{
			logger.info("Too few buttons (" + numButtons + "); expecting " + NUM_BUTTONS);
			while (numButtons < NUM_BUTTONS)
			{
				buttonsIdx[numButtons] = -1;
				numButtons++;
			}
		}
	} // end of findButtons()

	private boolean isButton(Component c)
	/*
	 * Return true if the component is a digital/absolute button, and
	 * its identifier name ends with "Button" (i.e. the
	 * identifier class is Component.Identifier.Button).
	 */
	{
		if (!c.isAnalog() && !c.isRelative())
		{ // digital and absolute
			String className = c.getIdentifier().getClass().getName();
			// logger.info(c.getName() + " identifier: " + className);
			if (className.endsWith("Button")) return true;
		}
		return false;
	} // end of isButton()

	private void findRumblers(Controller controller)
	/*
	 * Find the rumblers. Use the last rumbler for making vibrations,
	 * an arbitrary decision.
	 */
	{
		// get the game pad's rumblers
		rumblers = controller.getRumblers();
		if (rumblers.length == 0)
		{
			logger.info("No Rumblers found");
			rumblerIdx = -1;
		}
		else
		{
			logger.info("Rumblers found: " + rumblers.length);
			rumblerIdx = rumblers.length - 1; // use last rumbler
		}
	} // end of findRumblers()

	// ----------------- polling and getting data ------------------

	public void poll()
	// update the component values in the controller
	{
		controller.poll();
	}

	public int getXYStickDir()
	// return the (x,y) analog stick compass direction
	{
		if ((xAxisIdx == -1) || (yAxisIdx == -1))
		{
			logger.info("(x,y) axis data unavailable");
			return NONE;
		}
		else return getCompassDir(xAxisIdx, yAxisIdx);
	} // end of getXYStickDir()

	public int getZRZStickDir()
	// return the (z,rz) analog stick compass direction
	{
		if ((zAxisIdx == -1) || (rzAxisIdx == -1))
		{
			logger.info("(z,rz) axis data unavailable");
			return NONE;
		}
		else return getCompassDir(zAxisIdx, rzAxisIdx);
	} // end of getXYStickDir()

	private int getCompassDir(int xA, int yA)
	// Return the axes as a single compass value  
	{
		float xCoord = comps[xA].getPollData();
		float yCoord = comps[yA].getPollData();
//		logger.info("(x,y): (" + xCoord + "," + yCoord + ")");

		if ((xCoord == _x0  && yCoord == _y0) || (xCoord == _x1 && yCoord == _x1) && !on)
		{
			on = true;
			return ON;
		}
		else if (((xCoord == _x0 && yCoord == _x0) || (xCoord == _x1 && yCoord == _y1)) && on)
		{
			on = false;
			return OFF;
		}

		int xc = Math.round(xCoord);
		int yc = Math.round(yCoord);
		// logger.info("Rounded (x,y): (" + xc + "," + yc + ")");

		if ((yc == -1) && (xc == -1)) // (y,x)
		return NW;
		else if ((yc == -1) && (xc == 0)) return NORTH;
		else if ((yc == -1) && (xc == 1)) return NE;
		else if ((yc == 0) && (xc == -1)) return WEST;
		else if ((yc == 0) && (xc == 0)) return NONE;
		else if ((yc == 0) && (xc == 1)) return EAST;
		else if ((yc == 1) && (xc == -1)) return SW;
		else if ((yc == 1) && (xc == 0)) return SOUTH;
		else if ((yc == 1) && (xc == 1)) return SE;
		else
		{
			logger.info("Unknown (x,y): (" + xc + "," + yc + ")");
			return NONE;
		}
	} // end of getCompassDir()

	public int getHatDir()
	// Return the POV hat's direction as a compass direction
	{
		if (povIdx == -1)
		{
			logger.info("POV hat data unavailable");
			return NONE;
		}
		else
		{
			float povDir = comps[povIdx].getPollData();
			if (povDir == POV.CENTER) // 0.0f
			return NONE;
			else if (povDir == POV.DOWN) // 0.75f
			return SOUTH;
			else if (povDir == POV.DOWN_LEFT) // 0.875f
			return SW;
			else if (povDir == POV.DOWN_RIGHT) // 0.625f
			return SE;
			else if (povDir == POV.LEFT) // 1.0f
			return WEST;
			else if (povDir == POV.RIGHT) // 0.5f
			return EAST;
			else if (povDir == POV.UP) // 0.25f
			return NORTH;
			else if (povDir == POV.UP_LEFT) // 0.125f
			return NW;
			else if (povDir == POV.UP_RIGHT) // 0.375f
			return NE;
			else
			{ // assume center
				logger.info("POV hat value out of range: " + povDir);
				return NONE;
			}
		}
	} // end of getHatDir()

	public static String getStringDirection(int direction)
	{
		switch (direction)
		{
			case NW:
				return "NORTH_WEST";
			case NORTH:
				return "NORTH";
			case NE:
				return "NORTH_EAST";
			case WEST:
				return "WEST";
			case EAST:
				return "EAST";
			case SOUTH:
				return "SOUTH";
			case SW:
				return "SOUTH_WEST";
			case SE:
				return "SOUTH_EAST";
			case ON:
				return "ON";
			case OFF:
				return "OFF";
			case NONE:
				return "NONE";
			default:
				return null;
		}
	}

	public boolean[] getButtons()
	/*
	 * Return all the buttons in a single array. Each button value is
	 * a boolean.
	 */
	{
		boolean[] buttons = new boolean[NUM_BUTTONS];
		float value;
		for (int i = 0; i < NUM_BUTTONS; i++)
		{
			value = comps[buttonsIdx[i]].getPollData();
			buttons[i] = ((value == 0.0f) ? false : true);
		}
		return buttons;
	} // end of getButtons()

	public List<String> getButtonsInAction()
	{
		List<String> buttons = new ArrayList<String>();
		float value;
		for (int i = 0; i < NUM_BUTTONS; i++)
		{
			value = comps[buttonsIdx[i]].getPollData();
			if (value != 0.0f)
			{
				buttons.add("BUTTON_".concat(comps[buttonsIdx[i]].getIdentifier().getName()));
			}
		}
		return buttons;
	}

	public boolean isButtonPressed(int pos)
	/*
	 * Return the button value (a boolean) for button number 'pos'.
	 * pos is in the range 1-NUM_BUTTONS to match the game pad
	 * button labels.
	 */
	{
		if ((pos < 1) || (pos > NUM_BUTTONS))
		{
			logger.info("Button position out of range (1-" + NUM_BUTTONS + "): " + pos);
			return false;
		}

		if (buttonsIdx[pos - 1] == -1) // no button found at that pos
		return false;

		float value = comps[buttonsIdx[pos - 1]].getPollData();
		// array range is 0-NUM_BUTTONS-1
		return ((value == 0.0f) ? false : true);
	} // end of isButtonPressed()

	// ------------------- trigger a rumbler -------------------

	public void setRumbler(boolean switchOn)
	// turn the rumbler on or off
	{
		if (rumblerIdx != -1)
		{
			if (switchOn) rumblers[rumblerIdx].rumble(0.8f); // almost full on for
																												// last rumbler
			else // switch off
			rumblers[rumblerIdx].rumble(0.0f);
			rumblerOn = switchOn; // record rumbler's new status
		}
	} // end of setRumbler()

	public boolean isRumblerOn()
	{
		return rumblerOn;
	}

} // end of GamePadController class
