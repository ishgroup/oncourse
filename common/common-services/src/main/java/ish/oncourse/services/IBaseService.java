/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.services;

import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.Expression;

import java.util.List;


/**
 *
 * @author marek
 */
public interface IBaseService<T extends Persistent> {

    public static final String ID_PK_COLUMN = "id";

	
	/**
	 * @return Class of the Entity handled by the service
	 */
	Class<T> getEntityClass();

	/**
	 * Generic lookup of records by Willow ID
	 *
	 * @param willowId
	 * @return record matching the Willow ID or null otherwise
	 */
	T findById(Long willowId);

	/**
	 * Generic lookup of records using a qualifier
	 *
	 * @param qualifier query expression as Cayenne object
	 * @return Records matching the qualifier or empty list otherwise
	 */
	List<T> findByQualifier(Expression qualifier);
	
}
