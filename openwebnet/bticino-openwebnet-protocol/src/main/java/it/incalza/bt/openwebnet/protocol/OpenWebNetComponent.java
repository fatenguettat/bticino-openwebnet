package it.incalza.bt.openwebnet.protocol;

import it.incalza.bt.openwebnet.protocol.tag.TagSize;
import it.incalza.bt.openwebnet.protocol.tag.TagWhat;
import it.incalza.bt.openwebnet.protocol.tag.TagWhere;
import it.incalza.bt.openwebnet.protocol.tag.TagWho;

public interface OpenWebNetComponent
{
	
		public static final String START_COMAND = "*";
		public static final String SEPARATOR_COMAND = "*";
		public static final String SEPARATOR_COMAND_WRITE = "*#";
		public static final String START_COMAND_READ_OR_WRITE = "*#";
		public static final String END_COMAND = "##";
	
    public abstract TagWhat getWhat() throws OpenWebNetException;   
    public abstract TagWhere getWhere() throws OpenWebNetException;
    public abstract TagWho getWho() throws OpenWebNetException;
    public abstract TagSize getSize() throws OpenWebNetException;
    public abstract OpenWebNetValidation getValidation() throws OpenWebNetException;
    public abstract String getComand() throws OpenWebNetException;
    
    public abstract boolean validate() throws OpenWebNetException;
    
}
