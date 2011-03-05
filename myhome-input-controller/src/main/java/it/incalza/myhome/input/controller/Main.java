package it.incalza.myhome.input.controller;

import it.incalza.bt.openwebnet.client.OpenWebNetClient;
import it.incalza.bt.openwebnet.client.OpenWebNetClientPoolConnection;
import it.incalza.bt.openwebnet.protocol.OpenWebNet;
import it.incalza.bt.openwebnet.protocol.impl.OpenWebNetImpl;
import it.incalza.bt.openwebnet.protocol.tag.TagWhat;
import it.incalza.myhome.input.controller.configuration.Command;
import it.incalza.myhome.input.controller.configuration.ConfigurationCommands;
import it.incalza.myhome.input.controller.configuration.ObjectFactory;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class Main implements InputControllerHandler
{
	private static final Logger logger = Logger.getLogger(Main.class);
	private InputController inputController;
	private Thread controllerThread;
	private String lastAction = GamePadController.getStringDirection(GamePadController.OFF);
	private Command lastCommand = null;
	private OpenWebNetClient client = null;
	private boolean settingOpenWebNetClient = false;
	private static ConfigurationCommands configurationCommands;
	private static OpenWebNetClientPoolConnection clientPoolConnection;

	static
	{
		try
		{
			String fileName = "configuration/".concat(StringUtils.isEmpty(System.getProperty("configuration.file.name")) ? "configurationCommands.xml" : System.getProperty("configuration.file.name"));
			ClassLoader cl = ObjectFactory.class.getClassLoader();
			JAXBContext jc = JAXBContext.newInstance("it/incalza/myhome/input/controller/configuration",cl);
			Unmarshaller u = jc.createUnmarshaller();
			InputStream inputStream = ClassLoader.getSystemResourceAsStream(fileName);
			logger.debug("InputStream is NULL " + inputStream==null);
			configurationCommands = (ConfigurationCommands) u.unmarshal(inputStream);
			clientPoolConnection = OpenWebNetClientPoolConnection.getInstance();
		}
		catch (JAXBException e)
		{
			logger.error(e.getMessage(), e);
		}
	}

	public static void main(String[] args)
	{
		Main main = new Main();
		main.init(false);
	}

	public void init(boolean openWebNetClient)
	{
		settingOpenWebNetClient = openWebNetClient;
		inputController = new InputController(this);
		controllerThread = new Thread(inputController);
		inputController.setThread(controllerThread);
		controllerThread.start();
		if (openWebNetClient)
		{
			try
			{
				client = clientPoolConnection.newOpenWebNetClient();
			}
			catch (IOException e)
			{
				logger.debug(e.getMessage(), e);
			}
		}
	}

	public synchronized void handleEventPressed(String action)
	{
		if (!lastAction.equalsIgnoreCase(action))
		{
			logger.info("Pressed Action " + action);
			Command c = getCommand(action);
			if (c != null)
			{
				if (!settingOpenWebNetClient)
				{
					checkAndCreateConnectionOpenWebNetClient();
					if (client.isConnected())
					{
						try
						{
							for (String own : c.getOpenWebNetComands().getOpenWebNetComand())
							{
								client.write(new OpenWebNetImpl().createComandOpen(own));
								lastCommand = c;
								logger.debug("Pressed Action " + action + " sent comand own " + own);
							}
						}
						catch (Exception e)
						{
							logger.error(e.getMessage(), e);
						}
					}
				}
				else
				{
					for (String own : c.getOpenWebNetComands().getOpenWebNetComand())
					{
						lastCommand = c;
						logger.debug("Pressed Action " + action + " sent comand own " + own);
					}
				}
			}
		}
		else
		{

		}
		lastAction = action;
	}

	public synchronized void handleEventReleased(String action)
	{
		if (lastAction.equalsIgnoreCase(GamePadController.getStringDirection(GamePadController.OFF)) || lastAction.equalsIgnoreCase(GamePadController.getStringDirection(GamePadController.ON)) || lastAction.equalsIgnoreCase(action))
		{
			return;
		}
		else
		{
			logger.info("Released Action " + action);
			if (settingOpenWebNetClient)
			{
				checkAndCreateConnectionOpenWebNetClient();
				if (client.isConnected())
				{
					try
					{
						for (String own : lastCommand.getOpenWebNetComands().getOpenWebNetComand())
						{
							OpenWebNet ownSent = new OpenWebNetImpl().createComandOpen(own);
							client.write(new OpenWebNetImpl().createComandOpen(ownSent.getWho(), new TagWhat(ownSent.getWho(), "0"), ownSent.getWhere()));
							logger.debug("Pressed Action " + action + " sent comand own " + own);
						}
					}
					catch (Exception e)
					{
						logger.error(e.getMessage(), e);
					}
				}
			}
			else 
			{
				for (String own : lastCommand.getOpenWebNetComands().getOpenWebNetComand())
				{
					try
					{
						OpenWebNet ownSent = new OpenWebNetImpl().createComandOpen(own);
						OpenWebNet ownSendig = new OpenWebNetImpl().createComandOpen(ownSent.getWho(),new TagWhat(ownSent.getWho(),"0"), ownSent.getWhere());
						logger.debug("Simulate Sendig OpenWebNetClient " + action + " sent comand own " + ownSendig.getComand());
					}
					catch (Exception e)
					{
						logger.error(e.getMessage(), e);
					}
				}
			}
			lastAction = action;
			
		}
	}

	private void checkAndCreateConnectionOpenWebNetClient()
	{
		try
		{
			if (client == null || !client.isConnected()) client = clientPoolConnection.newOpenWebNetClient();
			Thread.sleep(50);
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}
	}

	private Command getCommand(String action)
	{
		for (Command c : configurationCommands.getCommands().getCommand())
		{
			if (c.getActionComand().name().equalsIgnoreCase(action))
			{
				logger.debug("Found command! Action: " + action);
				return c;
			}
		}
		logger.debug("NOT found command! Action: " + action);
		return null;
	}
}
