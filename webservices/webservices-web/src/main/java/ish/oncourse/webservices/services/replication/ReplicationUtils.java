/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.services.replication;

import ish.oncourse.model.Queueable;

/**
 * @author anton
 */
public class ReplicationUtils {

	public static String getEntityName(Class<? extends Queueable> clazz) {
		int index = clazz.getName().lastIndexOf(".") + 1;
		return clazz.getName().substring(index);
	}
}
