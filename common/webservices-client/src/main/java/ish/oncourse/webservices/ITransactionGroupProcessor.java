/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices;

import ish.oncourse.webservices.util.GenericReplicatedRecord;
import ish.oncourse.webservices.util.GenericTransactionGroup;

import java.util.List;

/**
 * On willow or angel side of replication. Takes one group of Full/Deleted stubs which are from one-* Cayenne commits and replays data changes.
 * @author anton
 */
public interface ITransactionGroupProcessor {
	/**
	 * 
	 * @param transactionGroup
	 * @return
	 */
	List<GenericReplicatedRecord> processGroup(GenericTransactionGroup transactionGroup);
}
