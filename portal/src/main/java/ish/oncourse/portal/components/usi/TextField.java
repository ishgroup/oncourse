package ish.oncourse.portal.components.usi;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class TextField {
	@Parameter(required = true)
	@Property
	private boolean hidden;

	@Parameter(required = true)
	@Property
	private String name;

	@Parameter(required = true)
	@Property
	private String displayName;


	@Parameter(required = true)
	@Property
	private String placeholder;

	@Parameter(required = true)
	@Property
	private int maxlength;

}
