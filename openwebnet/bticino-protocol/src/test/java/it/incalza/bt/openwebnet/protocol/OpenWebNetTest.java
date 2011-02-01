package it.incalza.bt.openwebnet.protocol;

import static org.junit.Assert.*;
import it.incalza.bt.openwebnet.protocol.impl.OpenWebNetImpl;
import org.apache.log4j.Logger;
import org.junit.Test;



public class OpenWebNetTest
{
	
	private static final Logger LOGGER = Logger.getLogger(OpenWebNetTest.class);

	@Test
	public void createComandOpen()
	{
		LOGGER.info("START Test ");
		OpenWebNet openWebNet = new OpenWebNetImpl();
		try
		{
			LOGGER.info("Comand Open WebNet *1*0*77##");
			openWebNet.createComandOpen("*1*0*77##");
			LOGGER.info(openWebNet.getComand());
			assertTrue("Validation is NotValid", OpenWebNetValidation.NORMAL.equals(openWebNet.getValidation()));
			assertTrue("WHO is not valid|", openWebNet.getWho().getTagValidated().equals("1"));
			assertTrue("WHERE is not valid|", openWebNet.getWhere().getTagValidated().equals("77"));
			assertTrue("WHAT is not valid|", openWebNet.getWhat().getTagValidated().equals("0"));
			
		
			
		}
		catch (OpenWebNetException e)
		{
			LOGGER.error("Errore in createComandOpenWrite ", e);
		}
		LOGGER.info("END Test ");
	}
	
	@Test
	public void createComandOpenState()
	{
		LOGGER.info("START Test ");
		OpenWebNet openWebNet = new OpenWebNetImpl();
		try
		{
			LOGGER.info("Comand Open WebNet *#4*1##");
			openWebNet.createComandOpen("*#4*1##");
			LOGGER.info(openWebNet.getComand());
			assertTrue("Validation is NotValid", OpenWebNetValidation.REQUEST_STATUS.equals(openWebNet.getValidation()));
			assertTrue("WHO is not valid|", openWebNet.getWho().getTagValidated().equals("4"));
			assertTrue("WHERE is not valid|", openWebNet.getWhere().getTagValidated().equals("1"));
		}
		catch (OpenWebNetException e)
		{
			LOGGER.error("Errore in createComandOpenWrite ", e);
		}
		LOGGER.info("END Test ");
	}
	
	@Test
	public void createComandOpenReady()
	{
		LOGGER.info("START Test ");
		OpenWebNet openWebNet = new OpenWebNetImpl();
		try
		{
			LOGGER.info("Comand Open WebNet *#4*44*0*0251*2##");
			openWebNet.createComandOpen("*#4*44*0*0251*2##");
			LOGGER.info(openWebNet.getComand());
			assertTrue("Validation is NotValid", OpenWebNetValidation.READY_SIZES.equals(openWebNet.getValidation()));
			assertTrue("WHO is not valid|", openWebNet.getWho().getTagValidated().equals("4"));
			assertTrue("WHERE is not valid|", openWebNet.getWhere().getTagValidated().equals("44"));
			assertTrue("SIZE is not valid|", openWebNet.getSize().getTagValidated().equals("0*0251*2"));
			
			LOGGER.info("Comand Open WebNet *#4*44*0##");
			openWebNet.createComandOpen("*#4*44*0##");
			LOGGER.info(openWebNet.getComand());
			assertTrue("Validation is NotValid", OpenWebNetValidation.REQUEST_SIZES.equals(openWebNet.getValidation()));
			assertTrue("WHO is not valid|", openWebNet.getWho().getTagValidated().equals("4"));
			assertTrue("WHERE is not valid|", openWebNet.getWhere().getTagValidated().equals("44"));
			assertTrue("SIZE is not valid|", openWebNet.getSize().getTagValidated().equals("0"));
			
		}
		catch (OpenWebNetException e)
		{
			LOGGER.error("Errore in createComandOpenWrite ", e);
		}
		LOGGER.info("END Test ");
	}
	
	@Test
	public void createComandOpenWrite()
	{
		LOGGER.info("START Test ");
		OpenWebNet openWebNet = new OpenWebNetImpl();
		try
		{
			LOGGER.info("Comand Open WebNet *#16*#2*#1*16*44*6*##");
			openWebNet.createComandOpen("*#16*#2*#1*16*44*6*##");
			LOGGER.info(openWebNet.getComand());
			assertTrue("WHO is not valid|", openWebNet.getWho().getTagValidated().equals("16"));
			assertTrue("WHERE is not valid|", openWebNet.getWhere().getTagValidated().equals("#2"));
			assertTrue("SIZE is not valid|", openWebNet.getSize().getTagValidated().equals("1*16*44*6"));
			
		}
		catch (OpenWebNetException e)
		{
			LOGGER.error("Errore in createComandOpenWrite ", e);
		}
		LOGGER.info("END Test ");
	}
	
}
