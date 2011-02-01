package it.incalza.bt.openwebnet.protocol;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

public enum OpenWebNetValidation
{

	ACK(Pattern.compile("(\\*#\\*1##)")),
	NACK(Pattern.compile("(\\*#\\*0##)")),
  NORMAL(Pattern.compile("^\\*([0-9]+)\\*([0-9#]+)\\*([0-9#]+)##$")),
	REQUEST_STATUS(Pattern.compile("^\\*#([0-9]+)\\*([0-9#]+)##$")),
	REQUEST_SIZES(Pattern.compile("^\\*#([0-9]+)\\*([0-9#]+)?\\*([0-9]+)##$")),
	READY_SIZES(Pattern.compile("^\\*#([0-9]+)\\*([0-9#]+)?\\*([0-9]+(\\*[0-9]+)*)\\*?##$")),
	WRITE_SIZES(Pattern.compile("^\\*#([0-9]+)\\*([0-9#]+)?\\*#([0-9]+(\\*[0-9]+)*)\\*?##$"));
	
	
	private static final Logger logger = Logger.getLogger(OpenWebNetValidation.class);
	private Pattern pattern;
	private OpenWebNetValidation(Pattern pattern)
	{
		this.pattern = pattern;
	}
	
	public Pattern getPattern()
	{
		return this.pattern;
	}
	
	public String getRegEx()
	{
		return this.pattern.pattern();
	}
	
	public boolean match(String comandOpen)
	{
		return this.getPattern().matcher(comandOpen).matches();
	}
	
	public Matcher getMatcher(String comandOpen)
	{
		return this.getPattern().matcher(comandOpen);
	}
	
	public String[] getGroupArray(String comandOpen) throws OpenWebNetException
	{
		logger.debug("## Search Group in to ".concat(comandOpen)
																				 .concat(" ").concat(this.name())
																				 .concat("__regex = ".concat(getRegEx())));
		String[] groups = {};
		if (match(comandOpen))
		{
			Matcher m = getMatcher(comandOpen);
			if (m.find())
			{
				logger.debug("## Group count " + m.groupCount());
				for (int i = 0; i <= m.groupCount(); i++)
				{	
					logger.debug("## Group ("+i+") = ".concat(m.group(i)));
					if (comandOpen.equals(m.group(i))) continue;
					groups = (String[]) ArrayUtils.add(groups, m.group(i));
					logger.debug("## GroupAdding ("+i+") = ".concat(m.group(i)));
				}
			}
		}
		else throw new OpenWebNetException("OpenWebNetValidation Not found groups!");
		return groups;
	}
	
	public static OpenWebNetValidation getEnum(String comandOpen) throws OpenWebNetException
	{
		for (OpenWebNetValidation validation : values())
		{
			if (validation.match(comandOpen)) return validation;
		}
		throw new OpenWebNetException("Comand Open not search " + comandOpen);
	}
	
}
