package it.incalza.myhome.input.controller;

import it.incalza.bt.openwebnet.client.OpenWebNetClient;
import it.incalza.bt.openwebnet.client.OpenWebNetClientPoolConnection;
import it.incalza.bt.openwebnet.protocol.OpenWebNet;
import it.incalza.bt.openwebnet.protocol.impl.OpenWebNetImpl;
import it.incalza.bt.openwebnet.protocol.tag.TagWhat;
import it.incalza.myhome.input.controller.configuration.ActionComand;
import it.incalza.myhome.input.controller.configuration.Command;
import it.incalza.myhome.input.controller.configuration.Command.OpenWebNetComands;
import it.incalza.myhome.input.controller.configuration.ConfigurationCommands;
import it.incalza.myhome.input.controller.configuration.Room;
import it.incalza.myhome.input.controller.configuration.SpecialComand;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.List;

import javax.swing.text.AbstractDocument.Content;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
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
			if (inputStream == null)
			{
				throw new NullPointerException("The configuration file is null!");
			}
			configurationCommands = (ConfigurationCommands) u.unmarshal(inputStream);
			clientPoolConnection = OpenWebNetClientPoolConnection.getInstance();
		} catch (JAXBException e)
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
		} catch (IOException e)
		{
			logger.debug(e.getMessage(), e);
		}
	}

	public synchronized void handleEventPressed(String action)
	{
		if (!lastAction.equalsIgnoreCase(action))
		{
			logger.info("Pressed Action " + action);
			Command cmd = getCommand(action);
			if (cmd != null && !commandsQueen.contains(cmd))
			{
				if (checkAndCreateConnectionOpenWebNetClient())
				{
					try
					{
						for (String own : getContentOpenWebNetComands(cmd).getOpenWebNetComand())
						{
							client.write(new OpenWebNetImpl().createComandOpen(own));
							logger.debug("Pressed Action " + action + " sent comand own " + own);
						}
					} catch (Exception e)
					{
						logger.error(e.getMessage(), e);
					}
					if ((lastAction = action).equalsIgnoreCase(GamePadController.getStringDirection(GamePadController.OFF)) || (lastAction = action).equalsIgnoreCase(GamePadController.getStringDirection(GamePadController.ON)))
					{
						return;
					}
					else
					{
						if ((cmd.getActionComand().equals(ActionComand.SOUTH) && commandsQueen.contains(getCommand(ActionComand.NORTH.name()))))
							commandsQueen.remove(getCommand(ActionComand.NORTH.name()));
						else if ((cmd.getActionComand().equals(ActionComand.NORTH) && commandsQueen.contains(getCommand(ActionComand.SOUTH.name()))))
							commandsQueen.remove(getCommand(ActionComand.SOUTH.name()));
						if ((cmd.getActionComand().equals(ActionComand.WEST) && commandsQueen.contains(getCommand(ActionComand.EAST.name()))))
							commandsQueen.remove(getCommand(ActionComand.EAST.name()));
						else if ((cmd.getActionComand().equals(ActionComand.EAST) && commandsQueen.contains(getCommand(ActionComand.WEST.name()))))
							commandsQueen.remove(getCommand(ActionComand.WEST.name()));
						logger.debug("Adding in to queen commands sent");
						commandsQueen.addLast(cmd);
					}
				}

			}
		}
	}

	public synchronized void handleEventReleased(String action)
	{
		if (lastAction.equalsIgnoreCase(GamePadController.getStringDirection(GamePadController.OFF)) || lastAction.equalsIgnoreCase(GamePadController.getStringDirection(GamePadController.ON)) || lastAction.equalsIgnoreCase(action))
		{
			return;
		} else
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

	public void handleEventPressedButton(List<String> buttons)
	{
		for (String button : buttons)
		{
			Command cmd = getCommand(button);
			if (cmd != null)
			{
				if (getContentSpecialComand(cmd) != null && getContentSpecialComand(cmd).equals(SpecialComand.SWITCH_ROOM))
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
				else if (getContentOpenWebNetComands(cmd) != null && !commandsQueen.contains(cmd))
				{
					if (checkAndCreateConnectionOpenWebNetClient())
					{
						try
						{
							for (String own : getContentOpenWebNetComands(cmd).getOpenWebNetComand())
							{
								client.write(new OpenWebNetImpl().createComandOpen(own));
								logger.debug("Pressed Button " + button + " sent comand own " + own);
							}
						} catch (Exception e)
						{
							logger.error(e.getMessage(), e);
						}
						commandsQueen.addLast(cmd);
					}
				}
				else if (getContentOpenWebNetComands(cmd) != null && commandsQueen.contains(cmd)  && 
						 (getContentSpecialComand(cmd) != null && getContentSpecialComand(cmd).equals(SpecialComand.SWITCH)))
				{
					sentStop(cmd);
				}
				try
				{
					if (getContentSpecialComand(cmd) != null && 
							(getContentSpecialComand(cmd).equals(SpecialComand.PRESSED) || 
							 getContentSpecialComand(cmd).equals(SpecialComand.SWITCH_ROOM)))
						Thread.sleep(500);
					else
						Thread.sleep(250);
				} catch (InterruptedException e)
				{
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	private OpenWebNetComands getContentOpenWebNetComands(Command cmd)
	{
		for (JAXBElement<?> cnt : cmd.getContent())
		{
			if (cnt.getValue().getClass().isInstance(OpenWebNetComands.class))
				return (OpenWebNetComands) cnt.getValue();
		}
		return null;
	}
	
	private SpecialComand getContentSpecialComand(Command cmd)
	{
		for (JAXBElement<?> cnt : cmd.getContent())
		{
			if (cnt.getValue().getClass().isInstance(OpenWebNetComands.class))
				return (SpecialComand) cnt.getValue();
		}
		return null;
	}
	
	public void handleEventReleasedButton(List<String> buttons)
	{
		for (String button : buttons)
		{
			Command cmd = getCommand(button);
			if (cmd != null && getContentOpenWebNetComands(cmd) != null && commandsQueen.contains(cmd) && 
				(getContentSpecialComand(cmd) != null && !getContentSpecialComand(cmd).equals(SpecialComand.SWITCH)))
			{
				sentStop(cmd);
			}
		}
	}

	private boolean checkAndCreateConnectionOpenWebNetClient()
	{
		try
		{
			if (client == null || !client.isConnected())
				client = clientPoolConnection.newOpenWebNetClient();
			Thread.sleep(50);
		} catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}
		return client.isConnected();
	}

	private Command getCommand(String action)
	{
		for (Command c : configurationCommands.getCommands().getCommand())
		{
			if (c.getRoom() != null && c.getActionComand().name().equalsIgnoreCase(action) && c.getRoom().equals(room))
			{
				logger.debug("Found command! Action: " + action + " Room " + room);
				return c;
			} else if (c.getRoom() == null && c.getActionComand().name().equalsIgnoreCase(action))
			{
				logger.debug("Found command! Action: " + action);
				return c;
			}
		}
		return null;
	}
	
	private void sentStop(Command cmd)
	{
		if (checkAndCreateConnectionOpenWebNetClient())
		{
			try
			{
				for (String own : getContentOpenWebNetComands(cmd).getOpenWebNetComand())
				{
					OpenWebNet ownSent = new OpenWebNetImpl().createComandOpen(own);
					client.write(new OpenWebNetImpl().createComandOpen(ownSent.getWho(), new TagWhat(ownSent.getWho(), "0"), ownSent.getWhere()));
					logger.debug("CommandPoll remove " + cmd.getActionComand() + " sent comand own " + own);
					commandsQueen.remove(cmd);
				}
			} catch (Exception e)
			{
				logger.error(e.getMessage(), e);
			}
		}
	}

}
