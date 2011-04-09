package it.incalza.myhome.input.controller;

import it.incalza.bt.openwebnet.client.OpenWebNetClient;
import it.incalza.bt.openwebnet.client.OpenWebNetClientPoolConnection;
import it.incalza.bt.openwebnet.protocol.OpenWebNet;
import it.incalza.bt.openwebnet.protocol.impl.OpenWebNetImpl;
import it.incalza.bt.openwebnet.protocol.tag.TagWhat;
import it.incalza.myhome.input.controller.configuration.ActionComand;
import it.incalza.myhome.input.controller.configuration.Command;
import it.incalza.myhome.input.controller.configuration.ConfigurationCommands;
import it.incalza.myhome.input.controller.configuration.Room;
import it.incalza.myhome.input.controller.configuration.SpecialComand;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.List;
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
	private Room room = Room.ROOM_1;
	private OpenWebNetClient client = null;
	private ArrayDeque<Command> commandsQueen = new ArrayDeque<Command>();
	private static ConfigurationCommands configurationCommands;
	private static OpenWebNetClientPoolConnection clientPoolConnection;

	static
	{
		try
		{
			String fileName = StringUtils.isEmpty(System.getProperty("configuration.file.name")) ? "configurationCommands.xml" : System.getProperty("configuration.file.name");
			JAXBContext jc = JAXBContext.newInstance(it.incalza.myhome.input.controller.configuration.ObjectFactory.class);
			Unmarshaller u = jc.createUnmarshaller();
			InputStream inputStream = ClassLoader.getSystemResourceAsStream(fileName);
			if (inputStream == null) { throw new NullPointerException("The configuration file is null!"); }
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
		main.init();
	}

	public void init()
	{

		inputController = new InputController(this);
		controllerThread = new Thread(inputController);
		inputController.setThread(controllerThread);
		controllerThread.start();
		try
		{
			client = clientPoolConnection.newOpenWebNetClient();
		}
		catch (IOException e)
		{
			logger.debug(e.getMessage(), e);
		}
	}

	public synchronized void handleEventPressed(String action)
	{
		if (!lastAction.equalsIgnoreCase(action))
		{
			logger.info("Pressed Action " + action);
			Command c = getCommand(action);
			if (c != null && !commandsQueen.contains(c))
			{
				checkAndCreateConnectionOpenWebNetClient();
				if (client.isConnected())
				{
					try
					{
						sentComand(c, action);
					}
					catch (Exception e)
					{
						logger.error(e.getMessage(), e);
					}
				}
				lastAction = action;
				if (lastAction.equalsIgnoreCase(GamePadController.getStringDirection(GamePadController.OFF)) || lastAction.equalsIgnoreCase(GamePadController.getStringDirection(GamePadController.ON)))
				{
					return;
				}
				else
				{
					if ((c.getActionComand().equals(ActionComand.SOUTH) && commandsQueen.contains(getCommand(ActionComand.NORTH.name())))) commandsQueen.remove(getCommand(ActionComand.NORTH.name()));
					else if ((c.getActionComand().equals(ActionComand.NORTH) && commandsQueen.contains(getCommand(ActionComand.SOUTH.name())))) commandsQueen.remove(getCommand(ActionComand.SOUTH.name()));
					if ((c.getActionComand().equals(ActionComand.WEST) && commandsQueen.contains(getCommand(ActionComand.EAST.name())))) commandsQueen.remove(getCommand(ActionComand.EAST.name()));
					else if ((c.getActionComand().equals(ActionComand.EAST) && commandsQueen.contains(getCommand(ActionComand.WEST.name())))) commandsQueen.remove(getCommand(ActionComand.WEST.name()));
					logger.debug("Adding in to queen commands sent");
					commandsQueen.addLast(c);
				}
			}
		}
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
			for (Command cmd : commandsQueen)
			{
				if (!cmd.getActionComand().name().startsWith("BUTTON")) sentStop(cmd);
			}
			lastAction = action;
		}
	}

	private void sentStop(Command command)
	{
		checkAndCreateConnectionOpenWebNetClient();
		if (client.isConnected())
		{
			try
			{
				for (String own : command.getOpenWebNetComands().getOpenWebNetComand())
				{
					OpenWebNet ownSent = new OpenWebNetImpl().createComandOpen(own);
					client.write(new OpenWebNetImpl().createComandOpen(ownSent.getWho(), new TagWhat(ownSent.getWho(), "0"), ownSent.getWhere()));
					logger.debug("CommandPoll remove " + command.getActionComand() + " sent comand own " + own);
				}
				commandsQueen.remove(command);
			}
			catch (Exception e)
			{
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	private void sentComand(Command command, String action)
	{
		try
		{
			for (String own : command.getOpenWebNetComands().getOpenWebNetComand())
			{
				client.write(new OpenWebNetImpl().createComandOpen(own));
				logger.debug("Pressed Action " + action + " sent comand own " + own);
			}
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}
	}

	public void handleEventPressedButton(List<String> buttons)
	{
		for (String button : buttons)
		{
			Command cmd = getCommand(button);
			if (cmd != null)
			{
				if (cmd.getSpecialComand() != null && cmd.getSpecialComand().equals(SpecialComand.SWITCH_ROOM))
				{
					switch (room)
					{
						case ROOM_1:
							room = Room.ROOM_2;
							break;
						case ROOM_2:
							room = Room.ROOM_1;
							break;
						default:
							room = Room.ROOM_1;
							break;
					}
					logger.debug("Change Room " + room);
				}
				else if (cmd.getOpenWebNetComands() != null && cmd.getActionComand().equals(ActionComand.BUTTON_17) && commandsQueen.contains(cmd))
				{					
					checkAndCreateConnectionOpenWebNetClient();
					if (client.isConnected())
					{
						try
						{
							sentStop(cmd);
						}
						catch (Exception e)
						{
							logger.error(e.getMessage(), e);
						}
					}

				}
				else if (cmd.getOpenWebNetComands() != null && !commandsQueen.contains(cmd))
				{
					checkAndCreateConnectionOpenWebNetClient();
					if (client.isConnected())
					{
						sentComand(cmd, button);
						commandsQueen.addLast(cmd);
					}
				}
				try
				{
					if ((cmd.getSpecialComand() != null && cmd.getSpecialComand().equals(SpecialComand.SWITCH_ROOM)) || 
							(cmd.getActionComand().equals(ActionComand.BUTTON_16) && cmd.getActionComand().equals(ActionComand.BUTTON_17))) 
						Thread.sleep(500);
					else Thread.sleep(250);
				}
				catch (InterruptedException e)
				{
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	public void handleEventReleasedButton(List<String> buttons)
	{
		for (String button : buttons)
		{
			Command cmd = getCommand(button);
			if (cmd != null && cmd.getOpenWebNetComands() != null && !cmd.getActionComand().equals(ActionComand.BUTTON_17) && commandsQueen.contains(cmd))
			{
				sentStop(cmd);
			}
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
			if (c.getRoom() != null && c.getActionComand().name().equalsIgnoreCase(action) && c.getRoom().equals(room))
			{
				logger.debug("Found command! Action: " + action + " Room " + room);
				return c;
			}
			else if (c.getRoom() == null && c.getActionComand().name().equalsIgnoreCase(action))
			{
				logger.debug("Found command! Action: " + action);
				return c;
			}
		}
		return null;
	}

}
