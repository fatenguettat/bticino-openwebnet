package it.incalza.bt.openwebnet.client;

import it.incalza.bt.openwebnet.client.configuration.ActionComand;
import it.incalza.bt.openwebnet.client.configuration.Command;
import it.incalza.bt.openwebnet.client.configuration.ConfigurationCommands;
import it.incalza.bt.openwebnet.client.configuration.ControllerInput;
import it.incalza.bt.openwebnet.client.configuration.ControllerSelected;
import it.incalza.bt.openwebnet.client.configuration.Value;
import it.incalza.bt.openwebnet.client.own.monitor.ClientMonitorOWN;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;
import net.java.games.input.Rumbler;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

public class ClientController
{

	private static final Logger logger = Logger.getLogger(ClientController.class);
	public static final String KEY_PATHCONFIGURATION = "pathConfiguration";
	public static final String KEY_FILENAMECONFIGURATIONCOMMANDS = "fileNameConfigurationCommands";

	private static ClientController keyBoardClient;
	private ConfigurationCommands configuration;
	private ActionComand actionComand;
	private List<ControllerSendComand> controllerSendComands = new ArrayList<ControllerSendComand>();


	public static ClientController getInstance()
	{
		if (keyBoardClient == null)
		{
			keyBoardClient = new ClientController();
		}
		return keyBoardClient;
	}

	private ClientController()
	{
		actionComand = ActionComand.STARTUP;
	}

	public static void main(String[] args)
	{
		ClientController keyBoardClient = ClientController.getInstance();
		Controller controllerSelected = null;
		String pathConfigurations = System.getProperty(KEY_PATHCONFIGURATION);
		String filenameConfigurationsCommands = System.getProperty(KEY_FILENAMECONFIGURATIONCOMMANDS);
		File xmlConfigurationComand = new File(pathConfigurations.concat(filenameConfigurationsCommands));
		if (xmlConfigurationComand.exists())
		{
			try
			{
				keyBoardClient.setConfiguration(loadConfiguration(xmlConfigurationComand));
			}
			catch (Exception e)
			{
				logger.error("ConfigurationCommands non valida!", e);
			}
			try
			{
				controllerSelected = keyBoardClient.getController(keyBoardClient.getConfiguration().getControllerSelected());
			}
			catch (NullPointerException e)
			{
				logger.error(e.getMessage());
				System.exit(0);
			}
		}
		else
		{
			int i = 0;
			for (Controller c : keyBoardClient.getControllers())
			{

				keyBoardClient.printDetails(c);
				logger.info("Controller Index: " + i);
				i++;
			}
			Scanner scanner = new Scanner(System.in);
			System.out.print("Select a index controller: ");
			boolean repeat = true;
			do
			{
				if (scanner.hasNext())
				{
					try
					{
						int index = Integer.valueOf(scanner.next());
						controllerSelected = keyBoardClient.getController(index);
						ConfigurationCommands configurationCommands = new ConfigurationCommands();
						configurationCommands.setControllerSelected(new ControllerSelected());
						configurationCommands.getControllerSelected().setName(controllerSelected.getName());
						configurationCommands.getControllerSelected().setPortType(controllerSelected.getPortType().toString());
						configurationCommands.getControllerSelected().setType(controllerSelected.getType().toString());
						keyBoardClient.setConfiguration(configurationCommands);
						saveConfiguration(xmlConfigurationComand, configurationCommands);
						repeat = false;
					}
					catch (Exception e)
					{
						logger.error(e.getMessage());
						repeat = true;
					}
				}
			}
			while (repeat);
			logger.info("Compile ConfigurationCommands.xml and restart application!");
		}

		keyBoardClient.getEvent(controllerSelected);

	}

	public Command[] getCommandsStartup()
	{
		return getCommands(ActionComand.STARTUP);
	}

	public Command[] getCommandsClosed()
	{
		return getCommands(ActionComand.CLOSED);
	}

	public Command[] getCommandsAction()
	{
		return getCommands(ActionComand.ACTION);
	}

	public Command[] getCommandsPause()
	{
		return getCommands(ActionComand.PAUSE);
	}

	public Command[] getCommands(ActionComand action)
	{

		List<Command> commands = new ArrayList<Command>();
		if (getConfiguration().getCommands() != null)
		{
			for (Command cmd : getConfiguration().getCommands().getCommand())
			{
				if (cmd.getActionComand().equals(action))
				{
					commands.add(cmd);
				}
			}
		}
		return commands.toArray(new Command[commands.size()]);
	}

	public Command getComandFromValues(Command[] commands, Identifier identifier, BigDecimal value)
	{
		Command command = null;
		for (Command cmd : commands)
		{
			if (matchValue(cmd.getControllerInput(), identifier, value)) { return cmd; }
		}
		return command;
	}

	private boolean matchValue(ControllerInput controllerInput, Identifier identifier, BigDecimal value)
	{
		boolean match = false;
		try
		{
			if (controllerInput.getIdentifier().equalsIgnoreCase(identifier.getName()))
			{
				for (Value v : controllerInput.getValues().getValue())
				{
					if (StringUtils.isNotEmpty(v.getMax()) && StringUtils.isNotEmpty(v.getMin()))
					{
						boolean max = value.compareTo(new BigDecimal(v.getMax()).setScale(3, RoundingMode.HALF_DOWN)) <= 0;
						boolean min = value.compareTo(new BigDecimal(v.getMin()).setScale(3, RoundingMode.HALF_DOWN)) >= 0;
						if (max && min) match = true;
					}
				}
			}
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}
		return match;
	}

	private static ConfigurationCommands loadConfiguration(File xml) throws JAXBException, ParserConfigurationException, SAXException, IOException
	{
		JAXBContext context = JAXBContext.newInstance(ConfigurationCommands.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		return (ConfigurationCommands) unmarshaller.unmarshal(xml);
	}

	private static void saveConfiguration(File xml, ConfigurationCommands configurationCommands) throws JAXBException
	{
		JAXBContext context = JAXBContext.newInstance(ConfigurationCommands.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.marshal(configurationCommands, xml);
	}

	private void printComponents(Component[] components)
	{
		int i = 0;
		for (Component component : components)
		{
			logger.info(i + ". " + component.getName() + ", " + getIdentifierName(component) + ", " + (component.isRelative() ? "relative" : "absolute") + ", " + (component.isAnalog() ? "analog" : "digital") + ", " + component.getDeadZone());
			i++;
		}
	}

	private String getIdentifierName(Component comp)
	{
		Component.Identifier id = comp.getIdentifier();
		if (id == Component.Identifier.Button.UNKNOWN) return "button";
		else if (id == Component.Identifier.Key.UNKNOWN) return "key";
		else return id.getName();
	}

	private void printRumblers(Rumbler[] rumblers)
	{
		if (rumblers.length == 0) logger.info("No Rumblers");
		else
		{
			logger.info("Rumblers: (" + rumblers.length + ")");
			for (int i = 0; i < rumblers.length; i++)
				logger.info(i + ". " + rumblers[i].getAxisName() + " on axis " + rumblers[i].getAxisIdentifier().getName());
		}
	}

	private void printDetails(Controller c)
	{
		logger.info("Details for controller: \r\n\t Name:" + c.getName() + ",\r\n\t Type:" + c.getType() + ",\r\n\t PortType:" + c.getPortType());
		printComponents(c.getComponents());
		printRumblers(c.getRumblers());
		// print details about any subcontrollers
		Controller[] subCtrls = c.getControllers();
		if (subCtrls.length == 0) logger.info("No subcontrollers");
		else
		{
			logger.info("No. of subcontrollers: " + subCtrls.length);
			// recursively visit each subcontroller
			for (int i = 0; i < subCtrls.length; i++)
			{
				logger.info("---------------");
				logger.info("Subcontroller: " + i);
				printDetails(subCtrls[i]);
			}
		}
	} // end of printDetails()

	private Controller getController(ControllerSelected controllerSelected) throws NullPointerException
	{
		for (Controller c : getControllers())
		{
			if (c.getName().equalsIgnoreCase(controllerSelected.getName()) && c.getType().toString().equalsIgnoreCase(controllerSelected.getType()) && c.getPortType().toString().equalsIgnoreCase(controllerSelected.getPortType())) { return c; }
		}
		throw new NullPointerException("Controller non trovato!!!");
	}

	private Controller[] getControllers()
	{
		return ControllerEnvironment.getDefaultEnvironment().getControllers();
	}

	private Controller getController(Integer index)
	{
		return getControllers()[index];
	}

	private void getEvent(Controller c)
	{
		int counterForStartup = 6;
		Command[] commandsStartup = getCommandsStartup();
		Command[] commandsPause = getCommandsPause();
		Command[] commandsAction = getCommandsAction();
		Command[] commandsClosed = getCommandsClosed();
		ClientMonitorOWN.getInstance();
		while (true)
		{
			c.poll();
			EventQueue queue = c.getEventQueue();
			Event event = new Event();
			Command cmdAction = null;
			Command cmdPause = null;
			Command cmdClosed = null;
			Command cmdStartup = null;
			boolean next = false;
			while (next = queue.getNextEvent(event))
			{
				Component comp = event.getComponent();
				BigDecimal value = BigDecimal.valueOf(event.getValue()).setScale(3, RoundingMode.HALF_DOWN);
				logger.debug("Identifier -> " + comp.getIdentifier() + " Values captured -> " + value);

				if (actionComand.equals(ActionComand.CLOSED) && next)
				{
					actionComand = ActionComand.PAUSE;
				}

				if (actionComand.equals(ActionComand.STARTUP))
				{
					if ((cmdStartup = getComandFromValues(commandsStartup, comp.getIdentifier(), value)) != null)
					{
						counterForStartup--;
					}

					if (counterForStartup == 0)
					{
						logger.info("Applicazione in fase " + ActionComand.STARTUP);
						actionComand = ActionComand.PAUSE;
						sendComand(cmdStartup);
						continue;
					}
				}
				else
				{

					if ((cmdAction = getComandFromValues(commandsAction, comp.getIdentifier(), value)) != null)
					{
						actionComand = ActionComand.ACTION;
						cmdStartup = null;
						cmdPause = null;
						cmdClosed = null;
					}
					else if ((cmdPause = getComandFromValues(commandsPause, comp.getIdentifier(), value)) != null)
					{
						actionComand = ActionComand.PAUSE;
						cmdStartup = null;
						cmdAction = null;
						cmdClosed = null;
					}
					else if ((cmdClosed = getComandFromValues(commandsClosed, comp.getIdentifier(), value)) != null)
					{
						actionComand = ActionComand.CLOSED;
						cmdStartup = null;
						cmdPause = null;
						cmdAction = null;
					}

					if (actionComand.equals(ActionComand.ACTION))
					{
						// TODO richiedo lo stato del singolo comando e se non sono in stato
						// pausa li inserisco
						logger.info("Applicazione in fase " + ActionComand.ACTION);
						sendComand(cmdAction);
					}
					else if (actionComand.equals(ActionComand.PAUSE))
					{
						logger.info("Applicazione in fase " + ActionComand.PAUSE);
						sendComand(cmdPause);
					}
				}

			}

			if (actionComand.equals(ActionComand.CLOSED) && !next)
			{
				// TODO richiedo lo stato del singolo comando e se non sono in stato
				// pausa li inserisco
				logger.info("Applicazione in fase " + ActionComand.CLOSED);
				sendComand(cmdClosed);
				actionComand = ActionComand.STARTUP;
				counterForStartup = 6;
				terminateAllControllerSendComand();
			}

			/*
			 * Sleep for 20 milliseconds, in here only so the example doesn't
			 * thrash the system.
			 */
			try
			{
				Thread.sleep(20);
			}
			catch (InterruptedException e)
			{
				logger.error(e);
			}

		}
	}

	private void sendComand(Command cmd)
	{
		if (cmd != null)
		{
			ControllerSendComand sendComand = new ControllerSendComand(cmd);
			if (!this.controllerSendComands.contains(sendComand))
			{
				sendComand.start();
				this.controllerSendComands.add(sendComand);
			}
		}
	}

	private void terminateAllControllerSendComand()
	{
		for (ControllerSendComand csc : this.controllerSendComands)
		{
			try
			{
				csc.join();
			}
			catch (InterruptedException e)
			{
				logger.error(e.getMessage());
			}
		}
		this.controllerSendComands.clear();
	}

	public void setConfiguration(ConfigurationCommands configuration)
	{
		this.configuration = configuration;
	}

	public ConfigurationCommands getConfiguration()
	{
		return configuration;
	}

}
