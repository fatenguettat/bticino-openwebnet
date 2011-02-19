package it.incalza.bt.openwebnet.client.own.monitor;

import it.incalza.bt.openwebnet.protocol.OpenWebNet;
import it.incalza.bt.openwebnet.protocol.OpenWebNetException;
import it.incalza.bt.openwebnet.protocol.OpenWebNetValidation;
import it.incalza.bt.openwebnet.protocol.impl.OpenWebNetImpl;
import it.incalza.bt.openwebnet.protocol.tag.TagWhere;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.IConnectExceptionHandler;
import org.xsocket.connection.IConnectHandler;
import org.xsocket.connection.IConnection.FlushMode;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.IDisconnectHandler;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.NonBlockingConnection;

public class ClientMonitorOWN
{
	public static final String DEFAULT_HOST = "192.168.1.35";
	public static final int DEFAULT_PORT = 20000;
	public static final long DEFAULT_TIMEOUT = 30 * 1000L;
	private static final Logger logger = Logger.getLogger(ClientMonitorOWN.class);

	private static ClientMonitorOWN client = null;
	private INonBlockingConnection connection = null;
	private MonitorHandler monitorHandler = null;
	// private List<OpenWebNet> ownMessages = null;
	private Map<TagWhere, OpenWebNet> ownMessages = Collections.synchronizedMap(new HashMap<TagWhere, OpenWebNet>());
	private String host = DEFAULT_HOST;
	private int port = DEFAULT_PORT;
	private boolean connected = false;

	private ClientMonitorOWN()
	{
		start();
	}

	public static ClientMonitorOWN getInstance()
	{
		if (client == null)
		{
			client = new ClientMonitorOWN();
		}
		return client;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public String getHost()
	{
		return host;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public int getPort()
	{
		return port;
	}

	public void disconnect()
	{
		try
		{
			this.connection.close();
		}
		catch (IOException e)
		{
			logger.error(e.getMessage());
		}
	}

	public void start()
	{
		try
		{
			logger.info("Inizialize ClientMonitor ");
			monitorHandler = new MonitorHandler();
			connection = new NonBlockingConnection(InetAddress.getByName(host), port, monitorHandler, DEFAULT_TIMEOUT);
//			connection.setConnectionTimeoutMillis(10000);
			connection.setFlushmode(FlushMode.ASYNC);
			// ownMessages = Collections.synchronizedList(new
			// ArrayList<OpenWebNet>());
			ownMessages.clear();
		}
		catch (IOException e)
		{
			logger.error("Inizialized ClientMonitor " + e.getMessage());
		}
	}

	public boolean isConnected()
	{
		return connected;
	}

	public void setConnected(boolean connected)
	{
		this.connected = connected;
	}

	public OpenWebNet getFirstMessage()
	{
		return getOwnMessages().get(0);
	}

	public synchronized Map<TagWhere, OpenWebNet> getOwnMessages()
	{
		return ownMessages;
	}

	protected class MonitorHandler implements IDataHandler, IConnectHandler, IDisconnectHandler, IConnectExceptionHandler
	{

		@Override
		public boolean onConnect(INonBlockingConnection connection) throws IOException, BufferUnderflowException, MaxReadSizeExceededException
		{
			setConnected(false);
			return true;
		}

		@Override
		public boolean onData(INonBlockingConnection connection) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException
		{
			String received = connection.readStringByDelimiter("##", 1024);
			received = received.concat("##");
			if (!isConnected())
			{
				logger.debug("String Received " + received);
				if (StringUtils.isNotEmpty(received))
				{
					if (OpenWebNetValidation.ACK.match(received))
					{
						logger.info("Connection Client Monitor is OK");
						logger.debug("String Send *99*1##");
						connection.write("*99*1##");
						received = connection.readStringByDelimiter("##", 1024);
						received = received.concat("##");
						logger.debug("String Received " + received);
						if (OpenWebNetValidation.ACK.match(received))
						{
							logger.info("Connected Client Monitor!");
							connection.markReadPosition();
							setConnected(true);
						}
						else
						{
							logger.info("Il client richiede la password");
							setConnected(false);
						}

					}
					else if (OpenWebNetValidation.NACK.match(received))
					{
						logger.info("Connection Client Monitor is KO");
						setConnected(false);
					}
				}
				else
				{
					logger.info("Client Monitor not response");
					setConnected(false);
				}
			}
			else
			{
				logger.debug("Comand Monitor Received " + received);
				try
				{
					OpenWebNet openWebNet = new OpenWebNetImpl();
					openWebNet.createComandOpen(received);
					if (openWebNet.validate())
					{
						addInToMapMessages(openWebNet);
					}
				}
				catch (OpenWebNetException e)
				{
					logger.error(e.getMessage());
				}
				connection.markReadPosition();
			}
			return true;
		}

		@Override
		public boolean onDisconnect(INonBlockingConnection connection) throws IOException
		{
			logger.info("Disconnect Client Monitor");
			setConnected(false);
			return true;
		}

		@Override
		public boolean onConnectException(INonBlockingConnection connection, IOException e) throws IOException
		{
			logger.error("Client Monitor onConnectException " + e.getMessage());
			setConnected(false);
			return true;
		}

		private void addInToMapMessages(OpenWebNet own) throws OpenWebNetException
		{
			if (getOwnMessages().isEmpty()) getOwnMessages().put(own.getWhere(), own);
			else if (getOwnMessages().containsKey(own.getWhere()))
			{
				getOwnMessages().remove(own.getWhere());
				getOwnMessages().put(own.getWhere(), own);
			}
			else getOwnMessages().put(own.getWhere(), own);
		}
	}

}
