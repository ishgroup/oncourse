/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components.courseclass.outcome;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class OutcomeEditor {
	
	@Parameter
	@Property
	private boolean vet;

	@Parameter
	@Property
	private String outcomeId;
}
