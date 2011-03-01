package it.incalza.bt.openwebnet.client;

import java.io.IOException;
import java.net.SocketTimeoutException;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.MaxConnectionsExceededException;
import org.xsocket.connection.NonBlockingConnectionPool;

public class OpenWebNetClientPoolConnection
{
	public static final int MAX_CONNECTION = 50;
	public static final String DEFAULT_HOST = "192.168.1.35";
	public static final int DEFAULT_PORT = 20000;
	public static final long DEFAULT_TIMEOUT = 30 * 1000L;
	private static OpenWebNetClientPoolConnection _instance;
	private NonBlockingConnectionPool poolConnection;
	private String host;
	private int port;
	
	
	private OpenWebNetClientPoolConnection(String host, int port)
	{
		poolConnection = new NonBlockingConnectionPool();
		poolConnection.setMaxActive(MAX_CONNECTION);
		this.host = host;
		this.port = port;
	}
	
	public static OpenWebNetClientPoolConnection getInstance()
	{
		if (_instance == null)
		{
			_instance = new OpenWebNetClientPoolConnection(DEFAULT_HOST,DEFAULT_PORT);
		}
		return _instance;
	}
	
	public static OpenWebNetClientPoolConnection getInstance(String host, int port)
	{
		if (_instance == null)
		{
			_instance = new OpenWebNetClientPoolConnection(host,port);
		}
		return _instance;
	}
	
	public OpenWebNetClient newOpenWebNetClient() throws SocketTimeoutException, MaxConnectionsExceededException, IOException
	{
		INonBlockingConnection connection = poolConnection.getNonBlockingConnection(this.host, this.port);
		return new OpenWebNetClient(connection);
	}

	public OpenWebNetClient newOpenWebNetClientMonitor() throws SocketTimeoutException, MaxConnectionsExceededException, IOException
	{
		INonBlockingConnection connection = poolConnection.getNonBlockingConnection(this.host, this.port);
		return new OpenWebNetClient(connection, true);
	}
	
	
	
}
