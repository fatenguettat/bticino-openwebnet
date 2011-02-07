package it.incalza.bt.openwebnet.client;

import org.apache.log4j.Logger;


public class Client
{

//	public static final String LUCE_DEMO_1 = "*1*1*16##";
	public static final String LUCE_DEMO_1 = "*1*1*32##";
//	public static final String LUCE_DEMO_2 = "*1*0*16##";
	public static final String LUCE_DEMO_2 = "*1*0*32##";
	public static final String LUCE_DEMO_3 = "*1*1*18##";
	public static final String LUCE_DEMO_4 = "*1*0*18##";
	public static final String LUCE_DEMO_5 = "";
	public static final String LUCE_DEMO_6 = "";
	public static final String LUCE_DEMO_7 = "";
	public static final String LUCE_DEMO_8 = "";
	private static final Logger logger = Logger.getLogger(Client.class);
	static GestioneSocketComandi gestSocketComandi;
	static Client client;
	
	
	public static Client getInstance()
	{
		if (client == null)
		{
			client = new Client();
			gestSocketComandi = new GestioneSocketComandi();
		}	
		return client;	
	}
	
	public  void sendComand(String comand)
	{
		String ip = "192.168.1.35";
		boolean superSoket = false;
		boolean mantieniSocket = false;		
		sendComand(comand, ip, superSoket, mantieniSocket);
	}
	
	public  void sendComand(String comand, boolean test)
	{
		if (!test)
		{
			sendComand(comand);
		}
		
		logger.info("TEST COMANDO INVIATO " + comand);
	}
	
	
	
	public  void sendComand(String comandoOpen, String ip, boolean superSoket, boolean mantieniSocket)
	{
		if (gestSocketComandi.connect(ip, 20000, superSoket, mantieniSocket))
		{
			InviaComandoThread inviaComandoThread = new InviaComandoThread(comandoOpen, mantieniSocket);
			inviaComandoThread.start();

		}
		else
		{
			logger.info("CONNESSIONE NON RIUSCITA!!!");
		}
	}
}
