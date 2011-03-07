package it.incalza.bt.openwebnet.protocol.tag;

import it.incalza.bt.openwebnet.protocol.OpenWebNetTag;
import it.incalza.bt.openwebnet.protocol.OpenWebNetException;
import it.incalza.bt.openwebnet.protocol.OpenWebNetTagValidation;
import java.io.Serializable;
import org.apache.commons.lang.StringUtils;

public class TagWhat implements OpenWebNetTag, Serializable
{
	private static final long serialVersionUID = 791756861704165367L;
	private OpenWebNetTagValidation validation;
	private String what;
	

	public TagWhat(int what, OpenWebNetTagValidation validation)
	{
		this.what = Integer.toString(what);
		this.validation = validation;
	}
	
	public TagWhat(TagWho who, String what) throws OpenWebNetException
	{
		this.what = what;
		setValidation(who);
	}
	
	public TagWhat(String what, OpenWebNetTagValidation validation)
	{
		this.what = what;
		this.validation = validation;
	}
	
	public String getWhat()
	{
		return what;
	}

	public void setWhat(String what)
	{
		this.what = what;
	}
	
	public void setWhat(int what)
	{
		this.what = Integer.toString(what);
	}
	
	@Override
	public boolean validate() throws OpenWebNetException
	{
		if (StringUtils.isEmpty(getWhat())) throw new OpenWebNetException("Tag What is empty!");
		if (this.validation == null) throw new OpenWebNetException("OpenWebNetValidation is null in to Tag What!");
		if (this.validation.match(this.what)) return true;
		else throw new OpenWebNetException("Tag What is not validate! WHAT = ".concat(this.what));
	}

	@Override
	public String getTagValidated() throws OpenWebNetException
	{
		return validate() ? this.what : null;
	}

	public void setValidation(OpenWebNetTagValidation validation)
	{
		this.validation = validation;
	}
	
	public void setValidation(TagWho who) throws OpenWebNetException
	{
		switch (Integer.valueOf(who.getTagValidated()))
		{
			case TagWho.AUTOMATISMS:
				this.validation = OpenWebNetTagValidation.WHAT_AUTOMATISM;
				break;

			case TagWho.LIGHTS:
				this.validation = OpenWebNetTagValidation.WHAT_LIGTH;
				break;
			default:
				this.validation = OpenWebNetTagValidation.WHAT;
				break;
		}
	}

	public OpenWebNetTagValidation getValidation()
	{
		return validation;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof TagWhat)
		{
			try
			{
				boolean what = ((TagWhat)obj).getWhat().equalsIgnoreCase(this.getWhat());
				return what;
			}
			catch (Exception e){}
		}
		return false;
	}
}
