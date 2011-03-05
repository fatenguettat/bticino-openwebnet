package it.incalza.myhome.input.controller;

public class InputController implements Runnable
{
	private final int SLEEP_TIME = 50; // in ms
	private Main application = null;
	private volatile boolean mAlive = true;
	private Thread mThread;
	private GamePadController gpController = null;

	public InputController(Main application)
	{
		this.application = application;
		this.gpController = new GamePadController();
	}

	public void run()
	{
		while (mAlive)
		{
			gpController.poll();

			int compassDir = gpController.getXYStickDir();
			if (compassDir != GamePadController.NONE) application.handleEventPressed(GamePadController.getStringDirection(compassDir));
			else application.handleEventReleased(GamePadController.getStringDirection(compassDir));
//			
//			if (buttons.length > 0) application.handleEventPressed(ArrayUtils.toString(buttons), 0f, false);
//
//			boolean[] buttons = gpController.getButtons();
//			if (buttons.length != GamePadController.NUM_BUTTONS)
//	      System.out.println("Wring number of button values");
//	    else {
//	      for (int i=0; i < GamePadController.NUM_BUTTONS; i++) {
//	      	
//	      }    
//	    }
			try
			{
				Thread.sleep(SLEEP_TIME);
			}
			catch (InterruptedException e)
			{
					
			}
		}
	}

	public void setAlive(boolean aAlive)
	{
		this.mAlive = aAlive;
		Thread tmpBlinker = mThread;
		mThread = null;
		if (tmpBlinker != null)
		{
			tmpBlinker.interrupt();
		}
		System.gc();
	}

	public void setThread(Thread aThread)
	{
		this.mThread = aThread;
	}
}
