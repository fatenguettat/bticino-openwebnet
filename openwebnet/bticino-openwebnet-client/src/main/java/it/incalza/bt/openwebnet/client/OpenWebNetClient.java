package it.incalza.bt.openwebnet.client;

import it.incalza.bt.openwebnet.protocol.OpenWebNet;
import it.incalza.bt.openwebnet.protocol.OpenWebNetException;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.xsocket.connection.IConnection.FlushMode;
import org.xsocket.connection.IHandler;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.NonBlockingConnectionPool;

public class OpenWebNetClient 
{
	private static final Logger logger = Logger.getLogger(OpenWebNetClient.class);
	private INonBlockingConnection connection;
	private OpenWebNetClientHandler openWebNetClientHandler;
	
	private List<String> reciving = new ArrayList<String>();
	
	public OpenWebNetClient(INonBlockingConnection connection, boolean monitor) throws IOException
	{
		this.connection = connection;
		if (monitor) connection.setFlushmode(FlushMode.ASYNC);
		else connection.setFlushmode(FlushMode.SYNC);
		this.openWebNetClientHandler = new OpenWebNetClientHandler(this, monitor);
		this.connection.setHandler(this.openWebNetClientHandler);
	}
	
	public OpenWebNetClient(INonBlockingConnection connection) throws IOException
	{
		this(connection, false);
	}
	
	public void write(String request) throws BufferOverflowException, IOException
	{
		this.connection.write(request);
		logger.debug("Command send to server " + request);
	}
	
	public void write(OpenWebNet request) throws BufferOverflowException, IOException, OpenWebNetException
	{
		this.connection.write(request.getComand());
		logger.debug("Command send to server " + request.getComand());
	}
	
	public boolean isConnected()
	{
		return this.connection.isOpen() && this.openWebNetClientHandler.isConnectionAccepted();
	}

	public synchronized void handleReceived(String received)
	{
		this.reciving.add(0,received);
	}

	public String getReceiving()
	{
		return this.reciving.get(0);
	}
	
}
