package it.incalza.bt.openwebnet.protocol.impl;

import it.incalza.bt.openwebnet.protocol.OpenWebNet;
import it.incalza.bt.openwebnet.protocol.OpenWebNetException;
import it.incalza.bt.openwebnet.protocol.OpenWebNetValidation;
import it.incalza.bt.openwebnet.protocol.tag.TagSize;
import it.incalza.bt.openwebnet.protocol.tag.TagWhat;
import it.incalza.bt.openwebnet.protocol.tag.TagWhere;
import it.incalza.bt.openwebnet.protocol.tag.TagWho;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class OpenWebNetImpl implements OpenWebNet
{
	private static final Logger logger = Logger.getLogger(OpenWebNetImpl.class);
	private TagWho who;
	private TagWhat what;
	private TagWhere where;
	private TagSize size;
	private String comand;
	private OpenWebNetValidation validation;

	public static OpenWebNet newOpenWebNet()
	{
		return new OpenWebNetImpl();
	}
	
	@Override
	public TagWhat getWhat() throws OpenWebNetException
	{
		if (what == null) throw new OpenWebNetException("TagWhat is null!");
		return what;
	}

	@Override
	public TagWhere getWhere() throws OpenWebNetException
	{
		if (where == null) throw new OpenWebNetException("TagWhere is null!");
		return where;
	}

	@Override
	public TagWho getWho() throws OpenWebNetException
	{
		if (who == null) throw new OpenWebNetException("TagWho is null!");
		return who;
	}

	@Override
	public TagSize getSize() throws OpenWebNetException
	{
		if (size == null) throw new OpenWebNetException("TagSize is null!");
		return size;
	}

	@Override
	public String getComand() throws OpenWebNetException
	{
		return validate() ? this.comand : null;
	}
	
	private void compiledComand() throws OpenWebNetException
	{
		StringBuilder builder = new StringBuilder();
		logger.debug("Compiling Comand is " + getValidation());
		switch (getValidation())
		{
			case NORMAL:
				builder.append(START_COMAND)
							 .append(this.who.getTagValidated())
							 .append(SEPARATOR_COMAND)
							 .append(this.what.getTagValidated())
							 .append(SEPARATOR_COMAND)
							 .append(this.where.getTagValidated())
							 .append(END_COMAND);
				break;
			case REQUEST_STATUS:
				builder.append(START_COMAND_READ_OR_WRITE)
							 .append(this.who.getTagValidated())
							 .append(SEPARATOR_COMAND)
							 .append(this.where.getTagValidated())
							 .append(END_COMAND);
				break;	
			case REQUEST_SIZES:
			case READY_SIZES:
				builder.append(START_COMAND_READ_OR_WRITE)
							 .append(this.who.getTagValidated())
							 .append(SEPARATOR_COMAND)
							 .append(this.where.getTagValidated())
							 .append(SEPARATOR_COMAND)
							 .append(this.size.getTagValidated())
							 .append(END_COMAND);
				break;		
			case WRITE_SIZES:
				builder.append(START_COMAND_READ_OR_WRITE)
							 .append(this.who.getTagValidated())
							 .append(SEPARATOR_COMAND)
							 .append(this.where.getTagValidated())
							 .append(SEPARATOR_COMAND_WRITE)
							 .append(this.size.getTagValidated())
							 .append(END_COMAND);
				break;					
		}
		logger.debug("The comand Compiled is " + builder.toString());
		this.comand = builder.toString();
	}
	

	@Override
	public boolean validate() throws OpenWebNetException
	{
		compiledComand();
		if (getValidation().match(this.comand)) return true;
		throw new OpenWebNetException("Comand Open is not valitaded!!");
	}

	@Override
	public OpenWebNet createComandOpen(String comandOpen) throws OpenWebNetException
	{
		if (StringUtils.isEmpty(comandOpen)) throw new OpenWebNetException("Comand Open is Empty");
		this.validation = OpenWebNetValidation.getEnum(comandOpen);
		String[] groups = getValidation().getGroupArray(comandOpen);
		switch (getValidation())
		{
			case NORMAL:
				createComandOpen(groups[0], groups[1], groups[2]);
				break;
			case REQUEST_STATUS:
				createComandOpenState(groups[0], groups[1]);
				break;
			case REQUEST_SIZES:
			case READY_SIZES:
				createComandOpenDimension(groups[0], groups[1], groups[2]);
				break;
			case WRITE_SIZES:
				createComandOpenWriteDimension(groups[0], groups[1], groups[2]); 
				break;			
			default:
				throw new OpenWebNetException("ComandOpen unmanaged!");
		}
		return this;
	}

	@Override
	public OpenWebNet createComandOpen(TagWho who, TagWhat what, TagWhere where) throws OpenWebNetException
	{
		this.who = who;
		this.where = where;
		this.what = what;
		this.size = null;
		this.validation = OpenWebNetValidation.NORMAL;
		return this;
	}

	@Override
	public OpenWebNet createComandOpen(String who, String what, String where) throws OpenWebNetException
	{
		TagWho tempWho = new TagWho(who);
		return createComandOpen(tempWho, new TagWhat(tempWho, what), new TagWhere(where));
		
	}

	@Override
	public OpenWebNet createComandOpen(String who, String what, String where, int level, int _interface) throws OpenWebNetException
	{
		TagWho tempWho = new TagWho(who);
		TagWhere tempWhere = new TagWhere();
		tempWhere.setWhereLevelInterface(where, level, _interface);
		return createComandOpen(tempWho, new TagWhat(tempWho, what), new TagWhere(where));
	}

	@Override
	public OpenWebNet createComandOpenState(TagWho who, TagWhere where) throws OpenWebNetException
	{
		this.who = who;
		this.where = where;
		this.what = null;
		this.size = null;
		this.validation = OpenWebNetValidation.REQUEST_STATUS;
		return this;
	}

	@Override
	public OpenWebNet createComandOpenState(String who, String where) throws OpenWebNetException
	{
		return createComandOpenState(new TagWho(who), new TagWhere(where));
	}

	@Override
	public OpenWebNet createComandOpenState(String who, String where, int level, int _interface) throws OpenWebNetException
	{
		TagWho tempWho = new TagWho(who);
		TagWhere tempWhere = new TagWhere();
		tempWhere.setWhereLevelInterface(where, level, _interface);
		return createComandOpenState(tempWho, tempWhere);
	}

	@Override
	public OpenWebNet createComandOpenDimension(TagWho who, TagWhere where, TagSize size) throws OpenWebNetException
	{
		this.who = who;
		this.where = where;
		this.size = size;
		this.what = null;
		this.validation = (this.validation!=null && this.validation.equals(OpenWebNetValidation.REQUEST_SIZES)) ? this.validation : OpenWebNetValidation.READY_SIZES;
		return this;
	}

	@Override
	public OpenWebNet createComandOpenDimension(String who, String where, String size) throws OpenWebNetException
	{
		TagWho tempWho = new TagWho(who);
		return createComandOpenDimension(tempWho, new TagWhere(where), new TagSize(tempWho, size));
	}

	@Override
	public OpenWebNet createComandOpenDimension(String who, String where, int level, int _interface, String size) throws OpenWebNetException
	{
		TagWho tempWho = new TagWho(who);
		TagWhere tempWhere = new TagWhere();
		tempWhere.setWhereLevelInterface(where, level, _interface);
		return createComandOpenDimension(tempWho, tempWhere, new TagSize(tempWho, size));
	}

	@Override
	public OpenWebNet createComandOpenWriteDimension(TagWho who, TagWhere where, TagSize size) throws OpenWebNetException
	{
		this.who = who;
		this.where = where;
		this.size = size;
		this.what = null;
		this.validation = OpenWebNetValidation.WRITE_SIZES;
		return this;
	}
	
	@Override
	public OpenWebNet createComandOpenWriteDimension(String who, String where, String size) throws OpenWebNetException
	{
		TagWho tempWho = new TagWho(who);
		return createComandOpenWriteDimension(tempWho, new TagWhere(where), new TagSize(tempWho, size)); 
	}

	@Override
	public OpenWebNet createComandOpenWriteDimension(String who, String where, String size, String[] values) throws OpenWebNetException
	{
		TagWho tempWho = new TagWho(who);
		return createComandOpenWriteDimension(tempWho, new TagWhere(where), new TagSize(tempWho, size, values)); 
	}

	@Override
	public OpenWebNet createComandOpenWriteDimension(String who, String where, int level, int _interface, String size, String[] values) throws OpenWebNetException
	{
		TagWho tempWho = new TagWho(who);
		TagWhere tempWhere = new TagWhere();
		tempWhere.setWhereLevelInterface(where, level, _interface);
		return createComandOpenWriteDimension(tempWho, tempWhere, new TagSize(tempWho, size, values)); 
	}

	@Override
	public OpenWebNetValidation getValidation() throws OpenWebNetException
	{
		if (validation == null) throw new OpenWebNetException("The Validation is null");
		return validation;
	}

}
