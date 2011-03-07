package it.incalza.myhome.input.controller;

// ControllerDetails.java
// Andrew Davison, October 2006, ad@fivedots.coe.psu.ac.th

/*
 * Print component and rumbler information for the controller
 * specified with an index number, and optionally save
 * the output to a file.
 * The component index printed alongside each component will
 * be useful in the TestController application.
 * Usage pattern:
 * runJI ControllerDetails <index> [ <fnm> ]
 * e.g.
 * runJI ControllerDetails 2
 * The index value comes from the output of ListControllers.
 * Based on ControllerDetails.java by Robert Schuster in his
 * JInput tutorial at
 * https://freefodder.dev.java.net/tutorial/jinputTutorialOne.html
 */

import java.io.PrintStream;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Rumbler;
import net.java.games.input.Version;

public class ControllerDetails
{

	public static void main(String[] args)
	{

		System.out.println("JInput version: " + Version.getVersion());

		ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment();
		Controller[] cs = ce.getControllers();
		if (cs.length == 0)
		{
			System.out.println("No controllers found");
			System.exit(0);
		}
		int index = 0;
		for (Controller c : cs)
		{
			System.out.println("Controller: (" + index + ")");
			printDetails(c, System.out);
			index++;
		}
	} // end of main()

	public static void printDetails(Controller c, PrintStream ps)
	/*
	 * Report the component and rumbler information for the controller c.
	 * A controller may contain subcontrollers (e.g. the mouse), so
	 * recursively visit them, and report their details as well.
	 */
	{
		ps.println("Details for: " + c.getName() + ", " + c.getType() + ", " + c.getPortType());

		
	
		
		printComponents(c.getComponents(), ps);
		printRumblers(c.getRumblers(), ps);
		
		// print details about any subcontrollers
		Controller[] subCtrls = c.getControllers();
		if (subCtrls.length == 0) ps.println("No subcontrollers");
		else
		{
			ps.println("No. of subcontrollers: " + subCtrls.length);
			// recursively visit each subcontroller
			for (int i = 0; i < subCtrls.length; i++)
			{
				ps.println("---------------");
				ps.println("Subcontroller: " + i);
				printDetails(subCtrls[i], ps);
			}
		}
	} // end of printDetails()

	public static void printComponents(Component[] comps, PrintStream ps)
	// print info about each Component
	{
		if (comps.length == 0) ps.println("No Components");
		else
		{
			ps.println("Components: (" + comps.length + ")");
			for (int i = 0; i < comps.length; i++)
				ps.println(i + ". " + comps[i].getName() + ", " + getIdentifierName(comps[i]) + ", " +
				// comps[i].getIdentifier() + ", " +
						(comps[i].isRelative() ? "relative" : "absolute") + ", " + (comps[i].isAnalog() ? "analog" : "digital") + ", " + comps[i].getDeadZone());
		}
	} // end of printComponents()

	public static String getIdentifierName(Component comp)
	/*
	 * Return the identifier name for the component.
	 * If the component's identifier is UNKNOWN, then change the
	 * returned string to "button" or "key" depending on the
	 * identifier type.
	 */
	{
		Component.Identifier id = comp.getIdentifier();
		// System.out.println("Id: " + id.getName());

		if (id == Component.Identifier.Button.UNKNOWN) return "button"; // an
																																		// unknown
																																		// button
		else if (id == Component.Identifier.Key.UNKNOWN) return "key"; // an unknown
																																		// key
		else return id.getName();
	} // end of getIdentifierName()

	public static void printRumblers(Rumbler[] rumblers, PrintStream ps)
	// print info about each rumbler
	{
		if (rumblers.length == 0) ps.println("No Rumblers");
		else
		{
			ps.println("Rumblers: (" + rumblers.length + ")");
			Component.Identifier rumblerID;
			for (int i = 0; i < rumblers.length; i++)
			{
				rumblerID = rumblers[i].getAxisIdentifier();
				ps.print(i + ". " + rumblers[i].getAxisName() + " on axis; ");
				if (rumblerID == null) ps.println("no name");
				else ps.println("name: " + rumblerID.getName());
			}
		}
	} // end of printRumblers()

	public static int findCompIndex(Component[] comps, Component.Identifier id, String nm)
	{
		Component c;
		for (int i = 0; i < comps.length; i++)
		{
			c = comps[i];
			if ((c.getIdentifier() == id) && !c.isRelative())
			{
				System.out.println("Found " + c.getName() + "; index: " + i);
				return i;
			}
		}
		System.out.println("No " + nm + " component found");
		return -1;
	} // end of findCompIndex()

} // end of ControllerDetails class
