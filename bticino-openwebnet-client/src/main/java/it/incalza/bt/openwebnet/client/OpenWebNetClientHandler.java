package it.incalza.bt.openwebnet.client;

import it.incalza.bt.openwebnet.protocol.OpenWebNetValidation;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import java.util.Observable;
import org.apache.log4j.Logger;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.IConnectExceptionHandler;
import org.xsocket.connection.IConnectHandler;
import org.xsocket.connection.IConnectionTimeoutHandler;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.IDisconnectHandler;
import org.xsocket.connection.IIdleTimeoutHandler;
import org.xsocket.connection.INonBlockingConnection;

public class OpenWebNetClientHandler extends Observable implements OpenWebNetClientReceived, IDataHandler, IConnectHandler, IDisconnectHandler, IConnectExceptionHandler, IIdleTimeoutHandler, IConnectionTimeoutHandler
{
	private static final Logger logger = Logger.getLogger(OpenWebNetClientHandler.class);
	private boolean connectionAccepted;
	private boolean requestTypeClient;
	private String received;
	private boolean monitor;

	public OpenWebNetClientHandler(boolean monitor)
	{
		this.monitor = monitor;
	}

	@Override
	public boolean onConnectException(INonBlockingConnection connection, IOException ioe) throws IOException
	{
		logger.error("onConnectException: " + ioe.getMessage());
		connectionAccepted = false;
		return true;
	}

	@Override
	public boolean onDisconnect(INonBlockingConnection connection) throws IOException
	{
		logger.info("Client OpenWebNet disconnect");
		connectionAccepted = false;
		requestTypeClient = false;
		return true;
	}

	@Override
	public boolean onConnect(INonBlockingConnection connection) throws IOException, BufferUnderflowException, MaxReadSizeExceededException
	{
		logger.info("Client OpenWebNet connect");
		connectionAccepted = false;
		requestTypeClient = false;
		return true;
	}

	@Override
	public boolean onData(INonBlockingConnection connection) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException
	{

		received = connection.readStringByDelimiter("##", 1024);
		received = received.concat("##");
		logger.debug("Server OpenWebNet sent " + received);
		if (!connectionAccepted && !requestTypeClient)
		{
			if (OpenWebNetValidation.ACK.match(received))
			{
				logger.info("Server OpenWebNet connection accepted");
				if (!isMonitor())
				{
					connection.write("*99*0##");
					logger.debug("Send to Server OpenWebNet *99*0##!");
				}
				else
				{
					connection.write("*99*1##");
					logger.debug("Send to Server OpenWebNet *99*1##!");
				}
				requestTypeClient = true;
			}
			else if (OpenWebNetValidation.NACK.match(received))
			{
				logger.info("Server OpenWebNet connection not accepted!");
				connectionAccepted = false;
			}
		}
		else if (!connectionAccepted && requestTypeClient)
		{
			if (OpenWebNetValidation.ACK.match(received))
			{
				logger.info("Server OpenWebNet Accepted client!");
				connectionAccepted = true;
			}
			else
			{
				logger.info("Server OpenWebNet requires password!");
				connectionAccepted = false;
			}
		}
		else if (connectionAccepted && requestTypeClient)
		{
			changeSomething();
		}
		return true;
	}

	@Override
	public boolean onIdleTimeout(INonBlockingConnection connection) throws IOException
	{
		connection.close();
		connectionAccepted = false;
		return true;
	}

	@Override
	public boolean onConnectionTimeout(INonBlockingConnection connection) throws IOException
	{
		connection.close();
		connectionAccepted = false;
		return true;
	}

	public boolean isMonitor()
	{
		return monitor;
	}

	public void changeSomething()
	{
		// Notify observers of change
		setChanged();
		notifyObservers();
	}

	public void setConnectionAccepted(boolean connected)
	{
		this.connectionAccepted = connected;
	}

	public boolean isConnectionAccepted()
	{
		return connectionAccepted;
	}

	@Override
	public String getReceived()
	{
		return received;
	}
}