package it.incalza.bt.openwebnet.client;

import org.apache.log4j.Logger;
import it.incalza.bt.openwebnet.client.configuration.ActionComand;
import it.incalza.bt.openwebnet.client.configuration.Command;
import it.incalza.bt.openwebnet.client.own.ClientOWN;
import it.incalza.bt.openwebnet.protocol.OpenWebNet;
import it.incalza.bt.openwebnet.protocol.impl.OpenWebNetImpl;

public class ControllerSendComand extends Thread
{
	private static final Logger logger = Logger.getLogger(ControllerSendComand.class);
	private Command command;

	public ControllerSendComand(Command command)
	{
		super();
		this.command = command;
	}


	@Override
	public void run()
	{
		for (String own : command.getOpenWebNetComands().getOpenWebNetComand())
		{
			try
			{
				OpenWebNet openWebNet = new OpenWebNetImpl();
				openWebNet.createComandOpen(own);
				if (openWebNet.validate())
				{
					ClientOWN.getInstance().sendComandOpenWebNet(openWebNet);
				}
			}
			catch (Exception e)
			{
				logger.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ControllerSendComand)
		{
			return this.command.equals(((ControllerSendComand)obj).command);
		}
		return false;
		
	}

}
