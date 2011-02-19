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
	private TagWho who;
	private TagWhat what;
	private TagWhere where;
	private TagSize size;
	private String comand;
	private OpenWebNetValidation validation;

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
	public void createComandOpen(String comandOpen) throws OpenWebNetException
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
	}

	@Override
	public void createComandOpen(TagWho who, TagWhat what, TagWhere where) throws OpenWebNetException
	{
		this.who = who;
		this.where = where;
		this.what = what;
		this.size = null;
		this.validation = OpenWebNetValidation.NORMAL;
	}

	@Override
	public void createComandOpen(String who, String what, String where) throws OpenWebNetException
	{
		TagWho tempWho = new TagWho(who);
		createComandOpen(tempWho, new TagWhat(tempWho, what), new TagWhere(where));
	}

	@Override
	public void createComandOpen(String who, String what, String where, int level, int _interface) throws OpenWebNetException
	{
		TagWho tempWho = new TagWho(who);
		TagWhere tempWhere = new TagWhere();
		tempWhere.setWhereLevelInterface(where, level, _interface);
		createComandOpen(tempWho, new TagWhat(tempWho, what), new TagWhere(where));
	}

	@Override
	public void createComandOpenState(TagWho who, TagWhere where) throws OpenWebNetException
	{
		this.who = who;
		this.where = where;
		this.what = null;
		this.size = null;
		this.validation = OpenWebNetValidation.REQUEST_STATUS;
	}

	@Override
	public void createComandOpenState(String who, String where) throws OpenWebNetException
	{
		createComandOpenState(new TagWho(who), new TagWhere(where));
	}

	@Override
	public void createComandOpenState(String who, String where, int level, int _interface) throws OpenWebNetException
	{
		TagWho tempWho = new TagWho(who);
		TagWhere tempWhere = new TagWhere();
		tempWhere.setWhereLevelInterface(where, level, _interface);
		createComandOpenState(tempWho, tempWhere);
	}

	@Override
	public void createComandOpenDimension(TagWho who, TagWhere where, TagSize size) throws OpenWebNetException
	{
		this.who = who;
		this.where = where;
		this.size = size;
		this.what = null;
		this.validation = (this.validation!=null && this.validation.equals(OpenWebNetValidation.REQUEST_SIZES)) ? this.validation : OpenWebNetValidation.READY_SIZES;
	}

	@Override
	public void createComandOpenDimension(String who, String where, String size) throws OpenWebNetException
	{
		TagWho tempWho = new TagWho(who);
		createComandOpenDimension(tempWho, new TagWhere(where), new TagSize(tempWho, size));
	}

	@Override
	public void createComandOpenDimension(String who, String where, int level, int _interface, String size) throws OpenWebNetException
	{
		TagWho tempWho = new TagWho(who);
		TagWhere tempWhere = new TagWhere();
		tempWhere.setWhereLevelInterface(where, level, _interface);
		createComandOpenDimension(tempWho, tempWhere, new TagSize(tempWho, size));
	}

	@Override
	public void createComandOpenWriteDimension(TagWho who, TagWhere where, TagSize size) throws OpenWebNetException
	{
		this.who = who;
		this.where = where;
		this.size = size;
		this.what = null;
		this.validation = OpenWebNetValidation.WRITE_SIZES;
	}
	
	@Override
	public void createComandOpenWriteDimension(String who, String where, String size) throws OpenWebNetException
	{
		TagWho tempWho = new TagWho(who);
		createComandOpenWriteDimension(tempWho, new TagWhere(where), new TagSize(tempWho, size)); 
	}

	@Override
	public void createComandOpenWriteDimension(String who, String where, String size, String[] values) throws OpenWebNetException
	{
		TagWho tempWho = new TagWho(who);
		createComandOpenWriteDimension(tempWho, new TagWhere(where), new TagSize(tempWho, size, values)); 
	}

	@Override
	public void createComandOpenWriteDimension(String who, String where, int level, int _interface, String size, String[] values) throws OpenWebNetException
	{
		TagWho tempWho = new TagWho(who);
		TagWhere tempWhere = new TagWhere();
		tempWhere.setWhereLevelInterface(where, level, _interface);
		createComandOpenWriteDimension(tempWho, tempWhere, new TagSize(tempWho, size, values)); 
	}

	@Override
	public OpenWebNetValidation getValidation() throws OpenWebNetException
	{
		if (validation == null) throw new OpenWebNetException("The Validation is null");
		return validation;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof OpenWebNet)
		{
			try
			{
				boolean who = false;
				boolean where = false;
				boolean what = false;
				boolean size = false;
				OpenWebNet own = (OpenWebNet) obj;
				if (own.getWho() != null)
				{
					who = own.getWho().equals(this.getWho());
				}
				if (own.getWhere() != null)
				{
					where = own.getWhere().equals(this.getWhere());
				}
				if (own.getWhat() != null)
				{
					where = own.getWhat().equals(this.getWhat());
				}
				if (own.getSize() != null)
				{
					size = own.getSize().equals(this.getSize());
				}
				if (who && where && (what || size)) return true;
			}
			catch (Exception nonGestito){}
		}
		return false;
	}
	
	

}
