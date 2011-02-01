package it.incalza.bt.openwebnet.protocol;

import it.incalza.bt.openwebnet.protocol.tag.TagSize;
import it.incalza.bt.openwebnet.protocol.tag.TagWhat;
import it.incalza.bt.openwebnet.protocol.tag.TagWhere;
import it.incalza.bt.openwebnet.protocol.tag.TagWho;

public interface OpenWebNet extends OpenWebNetComponent
{
	//general 
	public void createComandOpen(String comandOpen)throws OpenWebNetException;
	
	public void createComandOpen(TagWho who, TagWhat what, TagWhere where) throws OpenWebNetException;
	public void createComandOpen(String who, String what, String where)throws OpenWebNetException;
	public void createComandOpen(String who, String what,	String where, int level, int _interface)throws OpenWebNetException;
	//richiesta stato
	public void createComandOpenState(TagWho who,TagWhere where)throws OpenWebNetException;
	public void createComandOpenState(String who, String where)throws OpenWebNetException;
	public void createComandOpenState(String who, String where, int level, int _interface)throws OpenWebNetException;
	//richiesta grandezza
	public void createComandOpenDimension(TagWho who,TagWhere where, TagSize size)throws OpenWebNetException;
	public void createComandOpenDimension(String who, String where, String size)throws OpenWebNetException;
	public void createComandOpenDimension(String who, String where, int level, int _interface, String size)throws OpenWebNetException;
	//scrittura grandezza
	public void createComandOpenWriteDimension(TagWho who, TagWhere where, TagSize size)throws OpenWebNetException;
	public void createComandOpenWriteDimension(String who, String where, String size) throws OpenWebNetException;
	public void createComandOpenWriteDimension(String who, String where, String size, String value[])throws OpenWebNetException;
	public void createComandOpenWriteDimension(String who, String where, int level, int _interface, String size, String value[])throws OpenWebNetException;

}
