package it.incalza.bt.openwebnet.client.own;

import it.incalza.bt.openwebnet.protocol.OpenWebNet;
import it.incalza.bt.openwebnet.protocol.OpenWebNetException;
import it.incalza.bt.openwebnet.protocol.OpenWebNetValidation;
import it.incalza.bt.openwebnet.protocol.impl.OpenWebNetImpl;
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
import org.xsocket.connection.IConnectionTimeoutHandler;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.IDisconnectHandler;
import org.xsocket.connection.IIdleTimeoutHandler;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.NonBlockingConnectionPool;

public class ClientOWN
{
	private static final Logger logger = Logger.getLogger(ClientOWN.class);
	public static final int MAX_CONNECTION = 49;
	public static final String DEFAULT_HOST = "192.168.1.35";
	public static final int DEFAULT_PORT = 20000;
	public static final long DEFAULT_TIMEOUT = 30 * 1000L;
	private static ClientOWN client = null;
	private NonBlockingConnectionPool poolConnection = null;
	private INonBlockingConnection connection = null;
	private String host = DEFAULT_HOST;
	private int port = DEFAULT_PORT;
	private boolean connected = false;
	

	private ClientOWN()
	{
		poolConnection = new NonBlockingConnectionPool();
		poolConnection.setMaxActivePerServer(MAX_CONNECTION);
	}

	public static ClientOWN getInstance()
	{
		if (client == null)
		{
			client = new ClientOWN();
		}
		return client;
	}

	private INonBlockingConnection connection() throws Exception
	{
		logger.info("Creating a new Connection OpenWebNet");
		ClientOWNHandler clientOWNHandler = new ClientOWNHandler();
		INonBlockingConnection connection = poolConnection.getNonBlockingConnection(InetAddress.getByName(host), getPort(), clientOWNHandler);
		connection.setConnectionTimeoutMillis(DEFAULT_TIMEOUT);
		connection.setIdleTimeoutMillis(DEFAULT_TIMEOUT);
		return connection;
	}

	/**
	 * 
	 * @param request is OpenWebNet comand
	 * @return index di sendAndReceiveMessage
	 * @throws Exception
	 */
	public void sendComandOpenWebNet(OpenWebNet request) throws Exception
	{
		if (this.connection == null || !this.connection.isOpen())
		{
			logger.info("Creo una nuova connessione Open Web Net");
			this.connection = connection();
		}
		
		this.connection.write(request.getComand());
		logger.info("Send comand to client OpenWebNet " + request.getComand());
	}

	public NonBlockingConnectionPool getPoolConnection()
	{
		return poolConnection;
	}

	public String getHost()
	{
		return host;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public void setConnected(boolean connected)
	{
		this.connected = connected;
	}

	public boolean isConnected()
	{
		return connected;
	}

	protected class ClientOWNHandler implements IDataHandler, IConnectHandler, IDisconnectHandler, IConnectExceptionHandler, IIdleTimeoutHandler, IConnectionTimeoutHandler
	{

		@Override
		public boolean onConnectException(INonBlockingConnection connection, IOException ioe) throws IOException
		{
			logger.error(ioe.getMessage());
			setConnected(false);
			return true;
		}

		@Override
		public boolean onDisconnect(INonBlockingConnection connection) throws IOException
		{
			logger.info("Client OpenWebNet disconnect");
			setConnected(false);
			return true;
		}

		@Override
		public boolean onConnect(INonBlockingConnection connection) throws IOException, BufferUnderflowException, MaxReadSizeExceededException
		{
			logger.info("Client OpenWebNet connect");
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
						logger.info("Accepted Connection Client OpenWebNet");
						logger.debug("String Send Client OpenWebNet *99*0##");
						connection.write("*99*0##");
						received = connection.readStringByDelimiter("##", 1024);
						received = received.concat("##");
						logger.debug("String Received Client OpenWebNet " + received);
						if (OpenWebNetValidation.ACK.match(received))
						{
							logger.info("Connected Client OpenWebNet!");
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
						logger.info("Connection Client OpenWebNet not Accepted");
						setConnected(false);
					}
				}
				else
				{
					logger.info("Client OpenWebNet not response");
					setConnected(false);
				}
			}
			else
			{
				logger.debug("Comand OpenWebNet Received " + received);
				connection.markReadPosition();
			}

			return true;
		}

		@Override
		public boolean onIdleTimeout(INonBlockingConnection connection) throws IOException
		{
			connection.close();
			setConnected(false);
			return true;
		}

		@Override
		public boolean onConnectionTimeout(INonBlockingConnection connection) throws IOException
		{
			connection.close();
			setConnected(false);
			return true;
		}

	}

}
