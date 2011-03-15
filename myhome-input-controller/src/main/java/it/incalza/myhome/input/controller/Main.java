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
	private boolean settingOpenWebNetClient = false;
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
		boolean client = Boolean.valueOf(System.getProperty("client", "true"));
		Main main = new Main();
		main.init(client);
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
			if (c != null && !commandsQueen.contains(c))
			{
				if (settingOpenWebNetClient)
				{
					checkAndCreateConnectionOpenWebNetClient();
					if (client.isConnected())
					{
						try
						{
							for (String own : c.getOpenWebNetComands().getOpenWebNetComand())
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
				}
				else
				{
					for (String own : c.getOpenWebNetComands().getOpenWebNetComand())
					{
						logger.debug("Pressed Action " + action + " sent comand own " + own);
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
				if (!cmd.getActionComand().name().startsWith("BUTTON"))
					sentStop(cmd);
			}	
			lastAction = action;
		}
	}

	private void sentStop(Command command)
	{

		if (settingOpenWebNetClient)
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
				}
				catch (Exception e)
				{
					logger.error(e.getMessage(), e);
				}
			}
		}
		else
		{
			for (String own : command.getOpenWebNetComands().getOpenWebNetComand())
			{
				try
				{
					OpenWebNet ownSent = new OpenWebNetImpl().createComandOpen(own);
					OpenWebNet ownSendig = new OpenWebNetImpl().createComandOpen(ownSent.getWho(), new TagWhat(ownSent.getWho(), "0"), ownSent.getWhere());
					logger.debug("CommandPoll remove " + command.getActionComand() + " sent comand own " + ownSendig.getComand());
				}
				catch (Exception e)
				{
					logger.error(e.getMessage(), e);
				}
			}
		}	
		commandsQueen.remove(command);

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
					try
					{
						Thread.sleep(500L);
					}
					catch (InterruptedException e){}
					return;
				}
				else if (cmd.getOpenWebNetComands() != null && !commandsQueen.contains(cmd))
				{
					if (settingOpenWebNetClient)
					{
						checkAndCreateConnectionOpenWebNetClient();
						if (client.isConnected())
						{
							try
							{
								for (String own : cmd.getOpenWebNetComands().getOpenWebNetComand())
								{
									client.write(new OpenWebNetImpl().createComandOpen(own));
									logger.debug("Pressed Button " + button + " sent comand own " + own);
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
						for (String own : cmd.getOpenWebNetComands().getOpenWebNetComand())
						{
							logger.debug("Pressed Button " + button + " sent comand own " + own);
						}
					}
					commandsQueen.addLast(cmd);
					try
					{
						Thread.sleep(250);
					}
					catch (InterruptedException e)
					{
						logger.error(e.getMessage(),e);
					}
				}
			}
		}
	}

	public void handleEventReleasedButton(List<String> buttons)
	{
		for (String button : buttons)
		{
			Command cmd = getCommand(button);
			if (cmd != null && cmd.getOpenWebNetComands() != null && commandsQueen.contains(cmd))
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
		logger.debug("NOT found command! Action: " + action);
		return null;
	}

}
