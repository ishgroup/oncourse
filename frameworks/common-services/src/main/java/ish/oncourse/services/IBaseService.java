/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.services;

import java.util.List;
import org.apache.cayenne.exp.Expression;


/**
 *
 * @author marek
 */
public interface IBaseService<T> {

    public static final String ID_PK_COLUMN = "id";

	
	/**
	 * Find record by ID.
	 *
	 * @param willowId
	 * @return matching record if found, null otherwise
	 */
	T findById(Long willowId);

	List<T> findByQualifier(Expression qualifier);
	
	Class<T> getEntityClass();
	
}
