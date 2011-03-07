package it.incalza.bt.openwebnet.protocol;

import it.incalza.bt.openwebnet.protocol.tag.TagSize;
import it.incalza.bt.openwebnet.protocol.tag.TagWhat;
import it.incalza.bt.openwebnet.protocol.tag.TagWhere;
import it.incalza.bt.openwebnet.protocol.tag.TagWho;

public interface OpenWebNet extends OpenWebNetComponent
{
	public OpenWebNet createComandOpen(String comandOpen)throws OpenWebNetException;
	
	public OpenWebNet createComandOpen(TagWho who, TagWhat what, TagWhere where) throws OpenWebNetException;
	public OpenWebNet createComandOpen(String who, String what, String where)throws OpenWebNetException;
	public OpenWebNet createComandOpen(String who, String what,	String where, int level, int _interface)throws OpenWebNetException;
	//richiesta stato
	public OpenWebNet createComandOpenState(TagWho who,TagWhere where)throws OpenWebNetException;
	public OpenWebNet createComandOpenState(String who, String where)throws OpenWebNetException;
	public OpenWebNet createComandOpenState(String who, String where, int level, int _interface)throws OpenWebNetException;
	//richiesta grandezza
	public OpenWebNet createComandOpenDimension(TagWho who,TagWhere where, TagSize size)throws OpenWebNetException;
	public OpenWebNet createComandOpenDimension(String who, String where, String size)throws OpenWebNetException;
	public OpenWebNet createComandOpenDimension(String who, String where, int level, int _interface, String size)throws OpenWebNetException;
	//scrittura grandezza
	public OpenWebNet createComandOpenWriteDimension(TagWho who, TagWhere where, TagSize size)throws OpenWebNetException;
	public OpenWebNet createComandOpenWriteDimension(String who, String where, String size) throws OpenWebNetException;
	public OpenWebNet createComandOpenWriteDimension(String who, String where, String size, String value[])throws OpenWebNetException;
	public OpenWebNet createComandOpenWriteDimension(String who, String where, int level, int _interface, String size, String value[])throws OpenWebNetException;

}
