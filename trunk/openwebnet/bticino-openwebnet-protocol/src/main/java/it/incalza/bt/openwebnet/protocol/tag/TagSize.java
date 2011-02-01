package it.incalza.bt.openwebnet.protocol.tag;

import it.incalza.bt.openwebnet.protocol.OpenWebNetException;
import it.incalza.bt.openwebnet.protocol.OpenWebNetTag;
import it.incalza.bt.openwebnet.protocol.OpenWebNetTagValidation;
import java.io.Serializable;
import org.apache.commons.lang.StringUtils;

public class TagSize implements OpenWebNetTag, Serializable
{
	private static final long serialVersionUID = 791756861704165367L;
	private OpenWebNetTagValidation validation;
	private String size;
	private String[] values;
	

	public TagSize(int size, String[] values, OpenWebNetTagValidation validation)
	{
		this.size = Integer.toString(size);
		this.values = values;
		this.validation = validation;
	}
	
	public TagSize(TagWho who, String size) throws OpenWebNetException
	{
		this.size = getSize(size);
		this.values = getValues(size);
		setValidation(who);
	}
	
	public TagSize(TagWho who, String size, String[] value) throws OpenWebNetException
	{
		this.size = size;
		this.values = value;
		setValidation(who);
	}
	
	public TagSize(String size, String[] values, OpenWebNetTagValidation validation)
	{
		this.size = size;
		this.values = values;
		this.validation = validation;
	}
	
	public String getSize()
	{
		return size;
	}

	public void setSize(String size)
	{
		this.size = size;
	}
	
	public void setSize(int size)
	{
		this.size = Integer.toString(size);
	}
	
	public String getValuesToString()
	{
		StringBuilder builder = new StringBuilder("");
		if (values != null)
		{	
			for (int i=0; i < values.length; i++)
			{
				if (StringUtils.isEmpty(values[i])) continue;
				builder.append(SEPARATOR_COMAND).append(values[i]);
			}
		}
		return builder.toString();
	}
	
	public static String[] getValues(String size) throws OpenWebNetException
	{
		String[] tempValues = OpenWebNetTagValidation.SIZE_VALUE.getGroupArray(size);
		return tempValues.length > 0 ? (!tempValues[0].equals(size) ? tempValues[0].split("\\*") : new String[]{}) : new String[]{}; 
	}
	
	public static String getSize(String size) throws OpenWebNetException
	{
			return OpenWebNetTagValidation.SIZES.getGroupArray(size)[0];
	}
	
	@Override
	public boolean validate() throws OpenWebNetException
	{
		if (StringUtils.isEmpty(getSize())) throw new OpenWebNetException("Tag Size is empty!");
		if (this.validation == null) throw new OpenWebNetException("OpenWebNetValidation is null in to Tag Size!");
		String tempSize = this.size.concat(getValuesToString());
		if (this.validation.match(tempSize)) return true;
		else throw new OpenWebNetException("Tag Size is not validate! SIZE = ".concat(this.size));
	}

	@Override
	public String getTagValidated() throws OpenWebNetException
	{
		if (validate())
		{
			return this.size.concat(getValuesToString());
		}
		return null;
	}

	public void setValidation(OpenWebNetTagValidation validation)
	{
		this.validation = validation;
	}
	
	public void setValidation(TagWho who) throws OpenWebNetException
	{
		switch (Integer.valueOf(who.getTagValidated()))
		{
			case TagWho.LIGHTS:
				switch (Integer.parseInt(this.size))
				{
					case 1:
						this.validation = OpenWebNetTagValidation.SIZE_LIGHT_GO_UP_LEVEL_WHIT_SPEED;
						break;
					case 2:
						this.validation = OpenWebNetTagValidation.SIZE_LIGHT_TIMING;
						break;
					default:
						throw new OpenWebNetException("Size not valid for this who!");
				}
				break;
			default:
				this.validation = OpenWebNetTagValidation.SIZES;
		}
	}

	public OpenWebNetTagValidation getValidation()
	{
		return validation;
	}

	public void setValues(String[] values)
	{
		this.values = values;
	}

	public String[] getValues()
	{
		return values;
	}

}
