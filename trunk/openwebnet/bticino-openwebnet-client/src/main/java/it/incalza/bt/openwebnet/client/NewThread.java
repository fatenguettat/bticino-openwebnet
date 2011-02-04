package it.incalza.bt.openwebnet.client;

import org.apache.log4j.Logger;

public class NewThread extends Thread{
	
	private static final Logger logger = Logger.getLogger(NewThread.class);
	
	String name;
	int time = 0; //rappresenta lo sleep del thread (15 sec o 30 sec)
	int statoEntrata = 0;
	int tipoSocket; // 0 se la socket è di tipo comandi, 1 se è di tipo monitor
	
	/**
	 * Costruttore
	 * 
	 * @param threadName Nome del Thread
	 * @param numSocket Tipo di socket che richiama il costruttore, 0 se è socket comandi, 1 se è monitor
	 */
	public NewThread (String threadName, int numSocket){
		name = threadName;
		tipoSocket = numSocket;
		if(tipoSocket == 0) statoEntrata = Client.gestSocketComandi.stato;
//		else statoEntrata = GestioneSocketMonitor.statoMonitor;
		logger.info("Thread per il timeout attivato");
	}
	
	/**
	 * Avvia il Thread per gestire il timeout
	 */
	public void run(){
		do{
			time = 30000;
			
			try{
				Thread.sleep(time);
			}catch (InterruptedException e){
				logger.error("Thread timeout interrotto!");
				break;
				//e.printStackTrace();
			}
	
			logger.info("Thread timeout SCADUTO!");
			//chiudo il thread per la ricezione dei caratteri
			if(tipoSocket == 0){
				if (GestioneSocketComandi.readTh != null) GestioneSocketComandi.readTh.interrupt();
			}
//				else{
//				if (GestioneSocketMonitor.readThMon != null) GestioneSocketMonitor.readThMon.interrupt();
//			}
			break;
		}while(true);
	}
}
