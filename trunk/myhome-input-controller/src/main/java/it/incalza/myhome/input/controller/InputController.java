package it.incalza.myhome.input.controller;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

public class InputController implements Runnable
{
	private static final Logger logger = Logger.getLogger(InputController.class);
	private final int SLEEP_TIME = 50; // in ms
	private InputControllerHandler application = null;
	private volatile boolean mAlive = true;
	private Thread mThread;
	private GamePadController gpController = null;
	private List<String> buttonsLast = new ArrayList<String>();

	public InputController(InputControllerHandler application)
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

			List<String> buttons = null;
			if ((buttons = gpController.getButtonsInAction()).isEmpty() == false)
			{
				application.handleEventPressedButton(buttons);
			}

			try
			{
				if (buttonsLast.removeAll(buttons))
				{
					application.handleEventReleasedButton(buttonsLast);
				}
			}
			catch (Exception e)
			{
				logger.error(e.getMessage(), e);
			}

			buttonsLast = buttons;

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
