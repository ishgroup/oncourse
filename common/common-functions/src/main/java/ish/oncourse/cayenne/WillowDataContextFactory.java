/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.cayenne;

import ish.oncourse.services.persistence.ISHObjectContext;
import org.apache.cayenne.DataChannel;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.ObjectStore;
import org.apache.cayenne.configuration.server.DataContextFactory;

/**
 * User: akoiro
 * Date: 8/12/17
 */
public class WillowDataContextFactory extends DataContextFactory {

	protected DataContext newInstance(DataChannel parent, ObjectStore objectStore) {
		ISHObjectContext context = new ISHObjectContext(parent, objectStore);
		context.setRecordQueueingEnabled(true);
		return context;
	}
}
