package it.incalza.bt.openwebnet.client;

import it.incalza.bt.openwebnet.client.configuration.Command;
import it.incalza.bt.openwebnet.client.configuration.Commands;
import it.incalza.bt.openwebnet.client.configuration.Configuration;
import it.incalza.bt.openwebnet.client.configuration.ControllerInput;
import it.incalza.bt.openwebnet.client.configuration.ControllerSelected;
import it.incalza.bt.openwebnet.client.configuration.ObjectFactory;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;
import net.java.games.input.Rumbler;
import net.java.games.input.Version;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class KeyBoardClient
{

	private static final Logger logger = Logger.getLogger(KeyBoardClient.class);
	public static final String KEY_PATHCONFIGURATION = "pathConfiguration";
	public static final String KEY_FILENAMECONFIGURATIONCOMMANDS = "fileNameConfigurationCommands";
	public static final String COMANDO_1 = "w";
	public static final String COMANDO_2 = "s";
	public static final String COMANDO_3 = "d";
	public static final String COMANDO_4 = "a";
	public static final String COMANDO_5 = "";
	public static final String COMANDO_6 = "";
	public static final String COMANDO_7 = "";
	public static final String COMANDO_8 = "";

	private static Configuration configuration;

	public static void main(String[] args)
	{
		Controller controllerSelected = null;
		String pathConfigurations = System.getProperty(KEY_PATHCONFIGURATION);
		String filenameConfigurationsCommands = System.getProperty(KEY_FILENAMECONFIGURATIONCOMMANDS);
		File xmlConfigurationComand = new File(pathConfigurations.concat(filenameConfigurationsCommands));
		if (xmlConfigurationComand.exists())
		{
			try
			{
				setConfiguration(loadConfiguration(xmlConfigurationComand));
			}
			catch (Exception e)
			{
				logger.error("LOAD CONFIGURATION FAILED!!! ", e);
			}
			try
			{
				controllerSelected = getController(getConfiguration().getControllerSelected());
			}
			catch (NullPointerException e)
			{
				logger.error(e.getMessage());
				System.exit(0);
			}
		}
		else
		{
			logger.info("JInput version: " + Version.getVersion());
			Controller[] cs = getControllers();
			if (cs.length == 0)
			{
				logger.info("No controllers found");
				System.exit(0);
			}
			// print the name and type of each controller
			int i = 0;
			for (Controller c : cs)
			{
				logger.info("\r\nControllers found index: " + i);
				printDetails(c);
				i++;
			}

			Scanner scanner = new Scanner(System.in);
			Integer index = -1;

			while (index == -1)
			{
				logger.info("\r\nSelect index of controller: ");
				String indexSelected = scanner.nextLine();
				try
				{
					index = Integer.parseInt(indexSelected);
				}
				catch (NumberFormatException e)
				{
					logger.error(e.getMessage());
					index = -1;
				}
				controllerSelected = getController(index);
				if (controllerSelected == null)
				{
					logger.error("No controllers found");
					index = -1;
				}
			}
			ObjectFactory of = new ObjectFactory();
			ControllerSelected controllerConf = of.createControllerSelected();
			controllerConf.setName(controllerSelected.getName());
			controllerConf.setType(controllerSelected.getType().toString());
			controllerConf.setPortType(controllerSelected.getPortType().toString());
			Commands commands = of.createCommands();
			Command test = of.createCommand();
			ControllerInput controllerInput = of.createControllerInput();
			controllerInput.setIdentifier("TEST");
			controllerInput.setValue("TEST");
			test.setControllerInput(controllerInput);
			test.setOpenWebNetComand("TEST");
			commands.getCommand().add(test);
			Configuration conf = of.createConfiguration();
			conf.setControllerSelected(controllerConf);
			conf.setCommands(commands);
			setConfiguration(conf);
			// registerEvent(controllerSelected);
			try
			{
				saveConfiguration(xmlConfigurationComand);
			}
			catch (JAXBException e)
			{
				logger.error("Save Configuration FAILED!!!");
			}

		}
		
		printDetails(controllerSelected);
		getEvent(controllerSelected);

	}

	private static void addComand(String id, String value, int delay)
	{
		ObjectFactory of = new ObjectFactory();
		Command command = of.createCommand();
		ControllerInput controllerInput = of.createControllerInput();
		controllerInput.setIdentifier(id);
		controllerInput.setValue(value);
		controllerInput.setDelay(BigInteger.valueOf(delay));
		command.setControllerInput(controllerInput);
		command.setOpenWebNetComand("");
		getConfiguration().getCommands().getCommand().add(command);
	}

	private static Configuration loadConfiguration(File xml) throws JAXBException, ParserConfigurationException, SAXException, IOException
	{
		JAXBContext context = JAXBContext.newInstance("it.incalza.bt.openwebnet.client.configuration");
		Unmarshaller unmarshaller = context.createUnmarshaller();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(xml);
		@SuppressWarnings("unchecked")
		JAXBElement<Configuration> jaxbElement = (JAXBElement<Configuration>) unmarshaller.unmarshal(doc);
		return jaxbElement.getValue();
	}

	private static void saveConfiguration(File xml) throws JAXBException
	{
		ObjectFactory oFactory = new ObjectFactory();
		JAXBContext context = JAXBContext.newInstance(Configuration.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.marshal(oFactory.createConfigurationCommands(getConfiguration()), xml);
	}

	private static void printComponents(Component[] components)
	{
		int i = 0;
		for (Component component : components)
		{
			logger.info(i + ". " + component.getName() + ", " + getIdentifierName(component) + ", " + (component.isRelative() ? "relative" : "absolute") + ", " + (component.isAnalog() ? "analog" : "digital") + ", " + component.getDeadZone());
			i++;
		}
	}

	private static String getIdentifierName(Component comp)
	{
		Component.Identifier id = comp.getIdentifier();
		if (id == Component.Identifier.Button.UNKNOWN) return "button";
		else if (id == Component.Identifier.Key.UNKNOWN) return "key";
		else return id.getName();
	}

	private static void printRumblers(Rumbler[] rumblers)
	{
		if (rumblers.length == 0) logger.info("No Rumblers");
		else
		{
			logger.info("Rumblers: (" + rumblers.length + ")");
			for (int i = 0; i < rumblers.length; i++)
				logger.info(i + ". " + rumblers[i].getAxisName() + " on axis " + rumblers[i].getAxisIdentifier().getName());
		}
	}

	private static void printDetails(Controller c)
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

	private static Controller getController(ControllerSelected controllerSelected) throws NullPointerException
	{
		Controller controller = null;
		for (Controller c : getControllers())
		{
			if (c.getName().equalsIgnoreCase(controllerSelected.getName()) && c.getType().toString().equalsIgnoreCase(controllerSelected.getType()) && c.getPortType().toString().equalsIgnoreCase(controllerSelected.getPortType()))
			{
				controller = c;
			}
		}
		if (controller == null) throw new NullPointerException("Not find Controller!!!");
		return controller;
	}

	private static Controller[] getControllers()
	{
		return ControllerEnvironment.getDefaultEnvironment().getControllers();
	}

	private static Controller getController(Integer index)
	{
		return getControllers()[index];
	}

	private static void registerEvent(Controller c)
	{

		boolean continueRegistration = false;
		while (true)
		{

			/* Remember to poll each one */
			c.poll();

			/* Get the controllers event queue */
			EventQueue queue = c.getEventQueue();

			/* Create an event object for the underlying plugin to populate */
			Event event = new Event();

			logger.info("Push a buttun to register: ");
			/* For each object in the queue */
			while (queue.getNextEvent(event))
			{

				StringBuffer buffer = new StringBuffer(c.getName());
				Component comp = event.getComponent();
				buffer.append(" Id: ").append(comp.getIdentifier().getName()).append(", DeadZone: ").append(comp.getDeadZone()).append(", ");
				buffer.append(comp.getName()).append(" changed to " + event.getValue());
				logger.info(buffer.toString());
				do
				{
					logger.info("Adding this command ? (y/n)  ");
					Scanner scanner = new Scanner(System.in);
					String choice = scanner.nextLine();
					if (choice.equalsIgnoreCase("y"))
					{
						addComand(comp.getIdentifier().getName(), String.valueOf(event.getValue()), 0);
						break;
					}
					else if (choice.equalsIgnoreCase("n"))
					{
						break;
					}
				}
				while (true);

				do
				{
					logger.info("Do you continue? (y/n)  ");
					Scanner scanner = new Scanner(System.in);
					if (scanner.nextLine().equalsIgnoreCase("y"))
					{
						continueRegistration = true;
						break;
					}
					else if (scanner.nextLine().equalsIgnoreCase("n"))
					{
						continueRegistration = false;
						break;
					}
				}
				while (true);

			}
			if (!continueRegistration) break;
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

	private static void getEvent(Controller c)
	{

		while (true)
		{

			/* Remember to poll each one */
			c.poll();

			/* Get the controllers event queue */
			EventQueue queue = c.getEventQueue();

			/* Create an event object for the underlying plugin to populate */
			Event event = new Event();

			/* For each object in the queue */
			while (queue.getNextEvent(event))
			{

				/*
				 * Create a strug buffer and put in it, the controller name,
				 * the time stamp of the event, the name of the component
				 * that changed and the new value.
				 * Note that the timestamp is a relative thing, not
				 * absolute, we can tell what order events happened in
				 * across controllers this way. We can not use it to tell
				 * exactly *when* an event happened just the order.
				 */
				float value = event.getValue();
				Component comp = event.getComponent();
				if ((value == -0.011764705f || value == -0.019607842f) && comp.getIdentifier().equals(Identifier.Axis.Y))
				{
					//logger.info("VALUE_REPETED!!!");
					continue;
				}
				StringBuffer buffer = new StringBuffer(c.getName());
				buffer.append(" at ");
				buffer.append(event.getNanos()).append(" ns, ");
				
				buffer.append(TimeUnit.NANOSECONDS.toMillis(event.getNanos())).append(" ms, ");
				buffer.append(TimeUnit.NANOSECONDS.toSeconds(event.getNanos())).append(" s, ");
				buffer.append("Identifier: ").append(comp.getIdentifier().toString()).append(", ");
				buffer.append(comp.getName()).append(" changed to ");
				/*
				 * Check the type of the component and display an
				 * appropriate value
				 */
				if (comp.isAnalog())
				{
					buffer.append(value);
				}
				else
				{
					if (value == 1.0f)
					{
						buffer.append("On");
					}
					else
					{
						buffer.append("Off");
					}
				}
				logger.info(buffer.toString());
				sendComand(comp.getIdentifier(), value);

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

	private static void sendComand(Identifier identifier, float value)
	{
		for (Command cmd : getConfiguration().getCommands().getCommand())
		{
			if (cmd.getControllerInput().getIdentifier().equalsIgnoreCase(identifier.getName()) 
					&& cmd.getControllerInput().getValue().equalsIgnoreCase(String.valueOf(value)))
			{
				Client.getInstance().sendComand(cmd.getOpenWebNetComand(), true);
			}
		}
	}

	private static void printControllerDetails(Controller c)
	{
		logger.info("name: " + c.getName());
		logger.info("type: " + c.getType());
		logger.info("port: " + c.getPortType());
	}

	public static void setConfiguration(Configuration configuration)
	{
		KeyBoardClient.configuration = configuration;
	}

	public static Configuration getConfiguration()
	{
		return configuration;
	}
}
