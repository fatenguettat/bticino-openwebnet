package it.incalza.bt.openwebnet.client;

import it.incalza.bt.openwebnet.protocol.OpenWebNet;
import it.incalza.bt.openwebnet.protocol.OpenWebNetValidation;
import it.incalza.bt.openwebnet.protocol.impl.OpenWebNetImpl;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import org.apache.log4j.Logger;

public class GestioneSocketComandi
{

	/*
	 * stato 0 = non connesso.
	 * stato 1 = inviata richiesta socket comandi, in attesa di risposta.
	 * stato 2 = inviato risultato sulle operazioni della password, attesa per ack
	 * o nack. Se la
	 * risposta Ã¨ ack si passa allo stato 3.
	 * stato 3 = connesso correttamente.
	 */

	private static final Logger logger = Logger.getLogger(GestioneSocketComandi.class);

	static ReadThread readTh = null; // thread per la ricezione dei caratteri
																		// inviati dal webserver
	static NewThread timeoutThread = null; // thread per la gestione dei timeout
	 int  stato = 0; // stato socket comandi


	Socket socket = null;
	static String responseLine = null; // stringa in ricezione dal Webserver
	static final String socketComandi = "*99*0##";
	static final String socketSuperComandi = "*99*9##";

	BufferedReader input = null;
	PrintWriter output = null;
	OpenWebNet openWebNet = null;

	/**
	 * Costruttore
	 * 
	 */
	public GestioneSocketComandi()
	{
		openWebNet = new OpenWebNetImpl();
	}

	/**
	 * Tentativo di apertura socket comandi verso il webserver
	 * Diversi possibili stati:
	 * stato 0 = non connesso
	 * stato 1 = inviata richiesta socket comandi, in attesa di risposta
	 * stato 2 = inviato risultato sulle operazioni della password, attesa per ack
	 * o nack. Se la
	 * risposta Ã¨ ack si passa allo stato 3
	 * stato 3 = connesso correttamente
	 * 
	 * @param ip
	 *          Ip del webserver al quale connettersi
	 * @param port
	 *          Porta sulla quale aprire la connessione
	 * @param passwordOpen
	 *          Password open del webserver
	 * @return true Se la connessione va a buon fine, false altrimenti
	 */
	public boolean connect(String ip, int port, boolean superSoket, boolean mantieniSocket)
	{
		try
		{
			logger.info("Tentativo connessione a " + ip + "  Port: " + port);
			socket = new Socket(ip, port);
			setTimeout(0);
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			logger.info("Buffer reader creato");
			output = new PrintWriter(socket.getOutputStream(), true);
			logger.info("Print Writer creato");
		}
		catch (IOException e)
		{
			logger.error("Impossibile connettersi!", e);
			this.close();
		}

		if (socket != null)
		{
			while (true)
			{
				readTh = null;
				readTh = new ReadThread(socket, input, 0);
				readTh.start();
				try
				{
					readTh.join();
				}
				catch (InterruptedException e)
				{
					logger.error("----- ERRORE readThread.join() durante la connect:", e);

				}

				if (responseLine != null)
				{
					if (stato == 0)
					{ // ho mandato la richiesta di connessione
						logger.info("\n----- STATO 0 ----- ");
						logger.info("Rx: " + responseLine);
						if (OpenWebNetValidation.ACK.match(responseLine))
						{
							if (superSoket)
							{
								logger.info("Tx: " + socketSuperComandi);
								output.write(socketSuperComandi); // super comandi
							}
							else
							{
								logger.info("Tx: " + socketComandi);
								output.write(socketComandi); // comandi
							}
							output.flush();
							stato = 1;
							setTimeout(0);
						}
						else
						{
							// se non mi connetto chiudo la socket
							logger.info("Chiudo la socket verso il server " + ip);
							this.close();
							break;
						}
					}
					else if (stato == 1)
					{ // ho mandato il tipo di servizio richiesto
						logger.info("\n----- STATO 1 -----");
						logger.info("Rx: " + responseLine);

						// if(ClientFrame.abilitaPass.isSelected()){
						// //applico algoritmo di conversione
						// logger.info("Controllo sulla password");
						// long risultato = gestPassword.applicaAlgoritmo(passwordOpen,
						// responseLine);
						// logger.info("Tx: "+"*#"+risultato+"##");
						// output.write("*#12345##");
						// output.flush();
						// stato = 2; //setto stato dopo l'autenticazione
						// setTimeout(0);
						// }else{
						// non devo fare il controllo della password
						logger.info("NON effettuo il controllo sulla password - mi aspetto ACK");
						if (OpenWebNetValidation.ACK.match(responseLine))
						{
							logger.info("Ricevuto ack, stato = 3");
							stato = 3;
							break;
						}
						else
						{
							logger.info("Impossibile connettersi!!");
							// se non mi connetto chiudo la socket
							logger.info("Chiudo la socket verso il server " + ip);
							this.close();
							break;
						}

					}
					else if (stato == 2)
					{
						logger.info("\n----- STATO 2 -----");
						logger.info("Rx: " + responseLine);
						if (OpenWebNetValidation.ACK.match(responseLine))
						{
							logger.info("Connessione OK");
							stato = 3;
							break;
						}
						else
						{
							logger.info("Impossibile connettersi!!");
							// se non mi connetto chiudo la socket
							logger.info("Chiudo la socket verso il server " + ip);
							this.close();
							break;
						}
					}
					else break; // non dovrebbe servire (quando passo per lo stato tre
											// esco dal ciclo con break)
				}
				else
				{
					logger.info("--- Risposta dal webserver NULL");
					this.close();
					break;// ramo else di if(responseLine != null)
				}
			}// chiude while(true)
		}
		else
		{

		}

		if (stato == 3) return true;
		else return false;
	}// chiude connect()

	/**
	 * Chiude la socket comandi ed imposta stato = 0
	 * 
	 */
	public void close()
	{
		if (socket != null)
		{
			try
			{
				socket.close();
				socket = null;
				stato = 0;
				logger.info("-----Socket chiusa correttamente-----");
			}
			catch (IOException e)
			{
				logger.error("Errore Socket: <GestioneSocketComandi>" + e.getMessage());
			}
		}
	}

	/**
	 * Metodo per l'invio di un comando open
	 * 
	 * @param comandoOpen
	 *          comando da inviare
	 * @return 0 se il comando vine inviato, 1 se non Ã¨ possibile inviare il
	 *         comando
	 */
	public int invia(String comandoOpen, boolean mantieniSocket)
	{
		// creo l'oggetto openWebNet con il comandoOpen
		try
		{
			openWebNet.createComandOpen(comandoOpen);
			if (!openWebNet.validate())
			{
				logger.info("ERRATA frame open " + comandoOpen + ", la invio comunque!!!");
			}
			else
			{
				logger.info("CREATO oggetto OpenWebNet " + openWebNet.getComand());
			}
		}
		catch (Exception e)
		{
			logger.info("ERRORE nella creazione dell'oggetto OpenWebNet " + comandoOpen);
			logger.error("Eccezione in GestioneSocketComandi durante la creazione del'oggetto OpenWebNet" + e.getMessage());
			e.printStackTrace();
		}

		logger.info("Tx: " + comandoOpen);
		output.write(comandoOpen);
		output.flush();

		do
		{
			setTimeout(0);
			readTh = null;
			readTh = new ReadThread(socket, input, 0);
			readTh.start();
			try
			{
				readTh.join();
			}
			catch (InterruptedException e1)
			{
				logger.error("----- ERRORE readThread.join() durante l'invio comando:", e1);

			}

			if (responseLine != null)
			{
				if (OpenWebNetValidation.ACK.match(responseLine))
				{
					logger.info("Rx: " + responseLine);
					logger.info("Comando inviato correttamente");
					if (!mantieniSocket) this.close();
					return 0;
					// break;
				}
				else if (OpenWebNetValidation.NACK.match(responseLine))
				{
					logger.info("Rx: " + responseLine);
					logger.info("Comando NON inviato correttamente");
					if (!mantieniSocket) this.close();
					return 0;
					// break;
				}
				else
				{
					// RICHIESTA STATO
					logger.info("Rx: " + responseLine);
					if (OpenWebNetValidation.ACK.match(responseLine))
					{
						logger.info("Comando inviato correttamente");
						if (!mantieniSocket) this.close();
						return 0;
						// break;
					}
					else if (OpenWebNetValidation.NACK.match(responseLine))
					{
						logger.info("Comando NON inviato correttamente");
						if (!mantieniSocket) this.close();
						return 0;
						// break;
					}
				}
			}
			else
			{
				logger.info("Impossibile inviare il comando");
				if (!mantieniSocket) this.close();
				return 1;
				// break;
			}
		}
		while (true);
	}// chiude metodo invia(...)

	/**
	 * Attiva il thread per il timeout sulla risposta inviata dal WebServer.
	 * 
	 * @param tipoSocket
	 *          : 0 se Ã¨ socket comandi, 1 se Ã¨ socket monitor
	 */
	public void setTimeout(int tipoSocket)
	{
		timeoutThread = null;
		timeoutThread = new NewThread("timeout", tipoSocket);
		timeoutThread.start();
	}
	
//	public synchronized static int getStato()
//	{
//		return stato;
//	}
//	
//	public synchronized static void setStato(int stato)
//	{
//		GestioneSocketComandi.stato = stato;
//	}

}// chiuse la classe
