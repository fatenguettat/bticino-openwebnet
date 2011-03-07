package it.incalza.bt.openwebnet.client.test;

import it.incalza.bt.openwebnet.client.OpenWebNetClient;
import it.incalza.bt.openwebnet.client.OpenWebNetClientPoolConnection;
import it.incalza.bt.openwebnet.protocol.OpenWebNet;
import it.incalza.bt.openwebnet.protocol.OpenWebNetValidation;
import it.incalza.bt.openwebnet.protocol.impl.OpenWebNetImpl;
import org.junit.Assert;
import org.junit.Test;

public class ClientTest
{

	private static OpenWebNetClientPoolConnection clientPoolConnection;
	
	static
	{
		clientPoolConnection = OpenWebNetClientPoolConnection.getInstance();
	}
	
	@Test
	public void testOneClient()
	{
		
		try
		{
			OpenWebNetClient client = clientPoolConnection.newOpenWebNetClient();
			Thread.sleep(5000);
			Assert.assertTrue("The client is not connected", client.isConnected());
			if (client.isConnected())
			{
				OpenWebNet own = new OpenWebNetImpl().createComandOpen("*1*1*16##");
				client.write(own);
				Assert.assertTrue("The client respons NACK", OpenWebNetValidation.ACK.match(client.getReceivingIterator().next()));
				clientPoolConnection.destroyConnection(client);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
}
