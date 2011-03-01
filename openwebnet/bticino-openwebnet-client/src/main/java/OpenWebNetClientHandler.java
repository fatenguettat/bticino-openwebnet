package it.incalza.bt.openwebnet.client;

import it.incalza.bt.openwebnet.protocol.OpenWebNetValidation;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
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

public class OpenWebNetClientHandler implements IDataHandler, IConnectHandler, IDisconnectHandler, IConnectExceptionHandler, IIdleTimeoutHandler, IConnectionTimeoutHandler
{
	private static final Logger logger = Logger.getLogger(OpenWebNetClientHandler.class);
	private boolean connectionAccepted;
	private OpenWebNetClient client;
	private boolean monitor;
	
	public OpenWebNetClientHandler(OpenWebNetClient client, boolean monitor)
	{
		this.client = client;
		this.monitor = monitor;
	}

	@Override
	public boolean onConnectException(INonBlockingConnection connection, IOException ioe) throws IOException
	{
		logger.error("onConnectException: " + ioe.getMessage());
		setConnectionAccepted(false);
		return true;
	}

	@Override
	public boolean onDisconnect(INonBlockingConnection connection) throws IOException
	{
		logger.info("Client OpenWebNet disconnect");
		setConnectionAccepted(false);
		return true;
	}

	@Override
	public boolean onConnect(INonBlockingConnection connection) throws IOException, BufferUnderflowException, MaxReadSizeExceededException
	{
		logger.info("Client OpenWebNet connect");
		setConnectionAccepted(false);
		return true;
	}

	@Override
	public boolean onData(INonBlockingConnection connection) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException
	{
//		connection.markReadPosition();
		String received = connection.readStringByDelimiter("##", 1024);
		received = received.concat("##");
		logger.debug("Client OpenWebNet sent " + received);
		if (!isConnectionAccepted())
		{
			if (StringUtils.isNotEmpty(received))
			{
				if (OpenWebNetValidation.ACK.match(received))
				{
					logger.info("Client OpenWebNet connection accepted");
					if (!this.monitor)
					{
						connection.write("*99*0##");
						logger.debug("Send to Client OpenWebNet *99*0##");
					}
					else 
					{
						connection.write("*99*1##");
						logger.debug("Send to Client OpenWebNet *99*1##");
					}
					received = connection.readStringByDelimiter("##", 1024);
					received = received.concat("##");
					logger.debug("Client OpenWebNet sent  " + received);
					if (OpenWebNetValidation.ACK.match(received))
					{
						logger.info("Client OpenWebNet Accepted!");
						setConnectionAccepted(true);
					}
					else
					{
						logger.info("The client requires password!");
						setConnectionAccepted(false);
					}
				}
				else if (OpenWebNetValidation.NACK.match(received))
				{
					logger.info("Connection Client OpenWebNet not Accepted");
					setConnectionAccepted(false);
				}
			}
			else
			{
				logger.info("Client OpenWebNet not response");
				setConnectionAccepted(false);
			}
		}
		else
		{
			this.client.handleReceived(received);
		}
		return true;
	}

	@Override
	public boolean onIdleTimeout(INonBlockingConnection connection) throws IOException
	{
		connection.close();
		setConnectionAccepted(false);
		return true;
	}

	@Override
	public boolean onConnectionTimeout(INonBlockingConnection connection) throws IOException
	{
		connection.close();
		setConnectionAccepted(false);
		return true;
	}
	
	public boolean isMonito()
	{
		return monitor;
	}

	public void setConnectionAccepted(boolean connected)
	{
		this.connectionAccepted = connected;
	}

	public boolean isConnectionAccepted()
	{
		return connectionAccepted;
	}
}