package it.incalza.bt.openwebnet.client;

public class InviaComandoThread extends Thread
{

	String open = null;

	boolean mantieniSocket;

	/**
	 * Costruttore
	 * 
	 * @param comandoOpen
	 *          Comando da inviare
	 */
	public InviaComandoThread(String comandoOpen, boolean mantieniSocket)
	{

		open = comandoOpen;
		this.mantieniSocket = mantieniSocket;
	}

	/**
	 * Avvia il Thread per l'invio di un comando open, al termine riabilita i
	 * pulsanti per
	 * l'invio di un nuovo comando open
	 */
	public void run()
	{

		Client.gestSocketComandi.invia(open, mantieniSocket);

	}

}