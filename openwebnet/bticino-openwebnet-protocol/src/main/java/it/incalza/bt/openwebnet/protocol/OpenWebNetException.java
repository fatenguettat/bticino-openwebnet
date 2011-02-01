package it.incalza.bt.openwebnet.protocol;

public class OpenWebNetException extends Exception
{

	private static final long serialVersionUID = 9064366289517989943L;

	public OpenWebNetException()
	{
		super();
	}
	
	public OpenWebNetException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public OpenWebNetException(String message)
	{
		super(message);
	}

	public OpenWebNetException(Throwable cause)
	{
		super(cause);
	}

}
