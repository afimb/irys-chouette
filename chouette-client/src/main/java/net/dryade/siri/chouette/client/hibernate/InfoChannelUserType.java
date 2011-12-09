/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.dryade.siri.chouette.client.hibernate;

/**
 *
 * @author marc
 */

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import net.dryade.siri.sequencer.model.type.InfoChannel;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;



//----------------------------------------------------------------------------
/**
 * Type de donne bas sur l'numration BoardingAlightingPossibility. <p>
 * 
 * La structure implmente l'interface des dfinitions de type UserType.
 *  
 * @author : Chao ZHANG
 * 
 * @version : $Revision: 1.3 $
 */
//----------------------------------------------------------------------------
public class InfoChannelUserType implements UserType
{
	//------------------------------------------------------------
	/**************** Variables d'instance **********************/
	private static final int[] SQL_TYPES = {Types.VARCHAR};
	
	//-------------------------------------------------------------------
	/************** Mthodes de l'interface UserType *******************/

	/**
	 * @see UserType#deepCopy(java.lang.Object)
	 */
	public Object deepCopy(Object arg0) throws HibernateException {
		return arg0;
	}
	/**
	 * @see UserType#equals(java.lang.Object, java.lang.Object)
	 */
	public boolean equals(Object arg0, Object arg1) throws HibernateException {
		return arg0 == arg1;
	}
	/**
	 * @see UserType#isMutable()
	 */
	public boolean isMutable() {
		return false;
	}
	/**
	 * @see UserType#assemble(java.io.Serializable, java.lang.Object)
	 */
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
       return cached;
   }

	/**
	 * @see UserType#disassemble(java.lang.Object)
	 */
   public Serializable disassemble(Object value) throws HibernateException {
       return (Serializable)value;
   }
	
	/**
	 * @see UserType#replace(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
   public Object replace(Object original, Object target, Object owner) throws HibernateException {
       return original;
   }
	/**
	 * @see UserType#hashCode(java.lang.Object)
	 */
   public int hashCode(Object x) throws HibernateException {
       return x.hashCode();
   }
	/**
	 * @see UserType#nullSafeGet(java.sql.ResultSet, java.lang.String[], java.lang.Object)
	 */
	public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner)
			throws HibernateException, SQLException 
	{
		String name = resultSet.getString(names[0]);
		return resultSet.wasNull() ? null : InfoChannel.fromValue(name);	
	}
	/**
	 * @see UserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object, int)
	 */
	public void nullSafeSet(PreparedStatement statement, Object value, int index)
			throws HibernateException, SQLException 
	{
		if (value == null) 
		{
			statement.setNull(index, Types.VARCHAR);
		} else {
			statement.setString(index, value.toString());
		}
	}
	/**
	 * @see UserType#returnedClass()
	 */
	@SuppressWarnings("rawtypes")
	public Class returnedClass() {
		return InfoChannel.class;
	}
	/**
	 * @see UserType#sqlTypes()
	 */
	public int[] sqlTypes() {
		return SQL_TYPES;
	}
}
