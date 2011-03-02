package it.incalza.myhome.input.controller;

import net.java.games.input.Controller;

public class Main implements InputControllerHandler
{
	
	private InputController inputController;
	private Thread controllerThread;

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		

	}
	
	public void init(Controller controller)
	{
		inputController = new InputController(this, controller);
    controllerThread = new Thread(inputController);
    inputController.setThread(controllerThread);
    controllerThread.start();
	}

	public synchronized void handleEventPressed(String action, float aValue, boolean aIsAnalog)
	{
		// TODO Auto-generated method stub
		
	}

	public synchronized void handleEventReleased(String action, float aValue)
	{
		// TODO Auto-generated method stub
		
	}

}
