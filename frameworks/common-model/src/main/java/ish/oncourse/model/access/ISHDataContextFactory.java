/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.model.access;

import org.apache.cayenne.DataChannel;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.DataContextFactory;
import org.apache.cayenne.access.ObjectStore;


/**
 *
 * @author marek
 */
public class ISHDataContextFactory implements DataContextFactory{

	/**
	 * @see org.apache.cayenne.access.DataContextFactory#createDataContext(org.apache.cayenne.DataChannel, org.apache.cayenne.access.ObjectStore)
	 */
	public DataContext createDataContext(DataChannel parent, ObjectStore objectStore) {
		return new ISHDataContext(parent, objectStore);
	}

}
