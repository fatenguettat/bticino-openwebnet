package it.incalza.bt.openwebnet.protocol;

import java.io.Serializable;

public interface OpenWebNetTag extends Serializable
{
	public static final String START_COMAND = "*";
	public static final String SEPARATOR_COMAND = "*";
	public static final String SEPARATOR_COMAND_WRITE = "*#";
	public static final String START_COMAND_READ_OR_WRITE = "*#";
	public static final String END_COMAND = "##";
	public abstract String getTagValidated() throws OpenWebNetException;
	public abstract boolean validate() throws OpenWebNetException;
	
}
