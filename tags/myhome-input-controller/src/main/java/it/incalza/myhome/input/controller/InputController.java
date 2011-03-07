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
			boolean[] buttons = gpController.getButtons();
			if (buttons[0])
			{
				application.handleEventPressed("ROOM");
				try
				{
					Thread.sleep(500);
				}
				catch (InterruptedException e)
				{
				}
			}

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
