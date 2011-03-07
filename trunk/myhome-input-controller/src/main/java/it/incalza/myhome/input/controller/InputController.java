package it.incalza.myhome.input.controller;

import java.util.ArrayList;
import java.util.List;

public class InputController implements Runnable
{
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

			List<String> buttons = gpController.getButtonsInAction();

			application.handleEventPressedButton(buttons);
			try
			{
				Thread.sleep(500);
			}
			catch (InterruptedException e)
			{
			}
			if (buttonsLast.isEmpty())
				buttonsLast = buttons;
			else if (buttonsLast.removeAll(buttons))
			{
				application.handleEventReleasedButton(buttonsLast);
				buttonsLast = buttons;
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
