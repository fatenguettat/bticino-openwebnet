package it.incalza.bt.openwebnet.protocol.tag;

import it.incalza.bt.openwebnet.protocol.OpenWebNetTag;
import it.incalza.bt.openwebnet.protocol.OpenWebNetException;
import it.incalza.bt.openwebnet.protocol.OpenWebNetTagValidation;
import java.io.Serializable;
import org.apache.commons.lang.StringUtils;

public class TagWhere implements OpenWebNetTag, Serializable
{
	private static final long serialVersionUID = 791756861704165367L;

	public static final int GENERAL = 0;
	public static final int EMPTY = -1;
	public static final int LEVEL_BUS_LOCAL = 4;
	public static final int LEVEL_BUS_PRIVATE = 3;
	private OpenWebNetTagValidation validation;
	
	private String where;
	private String level;
	private String interfaces;
	
	public TagWhere()
	{
		validation = OpenWebNetTagValidation.WHERE;
		this.where = Integer.toString(EMPTY);
	}
	
	public TagWhere(int where)
	{
		validation = OpenWebNetTagValidation.WHERE;
		this.where = Integer.toString(where);
	}
	public TagWhere(String where)
	{
		validation = OpenWebNetTagValidation.WHERE;
		this.where = where;
	}
	
	public String getWhere()
	{
		return where;
	}

	public void setWhereGeneral()
	{
		setWhere(GENERAL);
	}
	
	public void setWhereGroup(int group)
	{
		setWhere("#".concat(Integer.toString(group)));
	}
	
	public void setWherRoomAndPointLight(int room, int pointLight)
	{
		setWhere(Integer.toString(room).concat(Integer.toString(pointLight)));
	}
	
	public void setWhereLevelInterface(String where, int level, int _interface)
	{
		setWhere(where.concat("#").concat(Integer.toString(level)).concat("#").concat(Integer.toString(_interface)));
	}
	public void setWhereGroupLevelInterface(int group, int level, int _interface)
	{
		setWhere("#".concat(Integer.toString(group)).concat("#").concat(Integer.toString(level)).concat("#").concat(Integer.toString(_interface)));
	}
	
	public void setWhere(String where)
	{
		this.where = where;
	}
	
	public void setWhere(int where)
	{
		this.where = Integer.toString(where);
	}
	
	@Override
	public boolean validate() throws OpenWebNetException
	{
		if (this.where.equals(Integer.toString(EMPTY)))return true;
		if (StringUtils.isEmpty(this.where)) throw new OpenWebNetException("Tag WHERE is Empty!");
		if (getValidation().match(this.where)) return true;
		else throw new OpenWebNetException("Tag Where is not validate! WHERE = ".concat(this.where));
	}

	@Override
	public String getTagValidated() throws OpenWebNetException
	{
		return validate() ? this.where.equals(Integer.toString(EMPTY)) ? "" : this.where : null;
	}

	public OpenWebNetTagValidation getValidation()
	{
		return validation;
	}

	public void setLevel(String level)
	{
		this.level = level;
	}

	public String getLevel()
	{
		return level;
	}

	public void setInterfaces(String interfaces)
	{
		this.interfaces = interfaces;
	}

	public String getInterfaces()
	{
		return interfaces;
	}

}
