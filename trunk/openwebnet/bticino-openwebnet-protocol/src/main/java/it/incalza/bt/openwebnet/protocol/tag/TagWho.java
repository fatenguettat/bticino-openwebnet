package it.incalza.bt.openwebnet.protocol.tag;

import it.incalza.bt.openwebnet.protocol.OpenWebNetException;
import it.incalza.bt.openwebnet.protocol.OpenWebNetTag;
import it.incalza.bt.openwebnet.protocol.OpenWebNetTagValidation;
import java.io.Serializable;
import org.apache.commons.lang.StringUtils;

public class TagWho implements OpenWebNetTag, Serializable
{

	private static final long serialVersionUID = -8757723920199965466L;
	public static final int SCENARIOS = 0;
	public static final int LIGHTS = 1;
	public static final int AUTOMATISMS = 2;
	public static final int LOADS_CONTROL = 3;
	public static final int THERMOREGULATION = 4;
	public static final int BURGLAR_ALARM = 5;
	public static final int BASIC_VIDEO = 6;
	public static final int NAVIGATION_VIDEO = 7;
	public static final int AUXILIAR_SIGNAL = 9;
	public static final int DEVICE_MANAGMENT = 13;
	public static final int CEN_MESSAGES = 13;
	public static final int SOUND_DIFFUSION = 16;
	public static final int AUTOMATIONS_DIAGNOSTIC = 1001;
	public static final int THERMOREGULATION_DIAGNOSTIC = 1004;
	public static final int DEVICE_DIAGNOSTIC = 1013;
	
	
	private String who;
	private OpenWebNetTagValidation validation; 
	
	public TagWho()
	{
		this.validation = OpenWebNetTagValidation.WHO;
	}
	
	public TagWho(int who)
	{
		this.validation = OpenWebNetTagValidation.WHO;
		this.who = Integer.toString(who);
	}
	
	public TagWho(String who)
	{
		this.validation = OpenWebNetTagValidation.WHO;
		this.who = who;
	}

	public String getWho()
	{
		return who;
	}

	public void setWho(int who)
	{
		this.who = Integer.toString(who);
	}
	
	public void setWho(String who)
	{
		this.who = who;
	}

	@Override
	public String getTagValidated() throws OpenWebNetException
	{
		return validate() ? this.who : null;
	}

	@Override
	public boolean validate() throws OpenWebNetException
	{
		if (StringUtils.isEmpty(this.who)) throw new OpenWebNetException("Tag WHO is Empty!");
		
		if (getValidation().match(this.who)) return true;
		
		else throw new OpenWebNetException("Tag WHO unmanaged! WHO = ".concat(this.who));
	}

	public OpenWebNetTagValidation getValidation()
	{
		return validation;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof TagWho)
		{
			try
			{
				boolean who = ((TagWho)obj).getWho().equalsIgnoreCase(this.getWho());
				return who;
			}
			catch (Exception e){}
		}
		return false;
	}
		

}
