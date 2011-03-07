package it.incalza.bt.openwebnet.protocol;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public enum OpenWebNetTagValidation
{
	
	
	WHO(Pattern.compile("([0-7]|[9]|1[356]|1001|1004|1013)")), 
	WHAT_AUTOMATISM(Pattern.compile("([012])")), 
	WHAT_LIGTH(Pattern.compile("([012]?[0-9]|[3][01]|[01][#](1[0-9][0-9]|2[0-4][0-9]|25[0-5])|31#([1-9][0-9]?[0]?)#(1[0-9][0-9]|2[0-4][0-9]|25[0-5]))")),
	WHAT(Pattern.compile("([0-9#]*)")), 
	WHERE(Pattern.compile("([1-9][1-9]|[#][1-9]|[0-9]|)([#]([43])[#]([0-9]+))?")), 
	SIZE_LIGHT_GO_UP_LEVEL_WHIT_SPEED(Pattern.compile("1\\*([1][0-9][0-9]|200)\\*([1-9]?[0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])")), 
	SIZE_LIGHT_TIMING(Pattern.compile("(2)\\*([1-9]?[0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\*([1-5]?[0-9])\\*([1-5]?[0-9])")),
	SIZE_VALUE(Pattern.compile("\\d+((\\*([\\d]+))*)\\*?")),
	SIZES(Pattern.compile("([0-9]+)((\\*([\\d]+))*)?\\*?"));

	private static final Logger logger = Logger.getLogger(OpenWebNetTagValidation.class);
	private Pattern pattern;

	private OpenWebNetTagValidation(Pattern pattern)
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
					logger.debug("## Group ("+i+") = ".concat(StringUtils.defaultIfEmpty(m.group(i), "")));
					if (StringUtils.isBlank(m.group(i))) continue;
					else if (comandOpen.equals(m.group(i)) && StringUtils.contains(comandOpen, '*')) continue;
//					else if (m.group(i).equals("*") || m.group(i).startsWith("*")) continue;
					groups = (String[]) ArrayUtils.add(groups, m.group(i));
					logger.debug("## GroupAdding ("+i+") = ".concat(m.group(i)));
				}
			}
		}
		else throw new OpenWebNetException("OpenWebNetTagValidation Not found groups!");
		return groups;
	}

	public static OpenWebNetTagValidation getEnum(String comandOpen) throws OpenWebNetException
	{
		for (OpenWebNetTagValidation validation : values())
		{
			if (validation.match(comandOpen)) return validation;
		}
		throw new OpenWebNetException("OpenWebNetTagValidation not search " + comandOpen);
	}

}
