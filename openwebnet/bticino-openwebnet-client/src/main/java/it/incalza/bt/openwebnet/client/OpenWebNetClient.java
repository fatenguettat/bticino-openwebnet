package it.incalza.bt.openwebnet.client;

import it.incalza.bt.openwebnet.protocol.OpenWebNet;
import it.incalza.bt.openwebnet.protocol.OpenWebNetException;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import org.apache.log4j.Logger;
import org.xsocket.connection.IConnection.FlushMode;
import org.xsocket.connection.INonBlockingConnection;

public class OpenWebNetClient implements Observer
{
	private static final Logger logger = Logger.getLogger(OpenWebNetClient.class);
	private INonBlockingConnection connection;
	private OpenWebNetClientHandler openWebNetClientHandler;
	private ArrayDeque<String> recivingQueue = new ArrayDeque<String>();

	public OpenWebNetClient(INonBlockingConnection connection, boolean monitor) throws IOException
	{
		this.connection = connection;
		if (monitor) 
			connection.setFlushmode(FlushMode.ASYNC);
		else
			connection.setFlushmode(FlushMode.SYNC);
		this.openWebNetClientHandler = new OpenWebNetClientHandler(monitor);
		this.openWebNetClientHandler.addObserver(this);
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
		return this.connection.isOpen();
	}
	
	public INonBlockingConnection getConnection()
	{
		return connection;
	}

	@Override
	public void update(Observable obs, Object o)
	{
		if (o instanceof OpenWebNetClientReceived)
		{
			OpenWebNetClientReceived clientReceived = (OpenWebNetClientReceived) o;
			logger.debug("Observable Client OpenWebNet sent " + clientReceived.getReceived());
			recivingQueue.addFirst(clientReceived.getReceived());
		}
	}
	
	public Iterator<String> getReceivingIterator()
	{
		return recivingQueue.iterator();
	}
	

	public Queue<String> getRecivingQueue()
	{
		return recivingQueue;
	}
	
}
