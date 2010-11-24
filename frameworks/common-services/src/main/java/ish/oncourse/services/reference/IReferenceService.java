/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.services.reference;

import java.util.List;


/**
 *
 * @author Marek Wawrzyczny
 */
public interface IReferenceService<T> {

	public static final String ISH_VERSION_PROPERTY = "ishVersion";


	/**
	 * Find updated records since a version.
	 *
	 * <p>The matching criteria is any records with ishVersion greater than the
	 * parameter.</p>
	 *
	 * @param ishVersion
	 * @return list of matching records, empty list if none found
	 */
	List<T> getForReplication(Long ishVersion);

	/**
	 * Find the maximum value of ish version field for the given Entity.
	 *
	 * @return maximum and thus latest version of Entity data or null if not
	 * present
	 */
	Long findMaxIshVersion();

}
