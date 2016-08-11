/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components.certificate;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

/**
 * User: akoiro
 * Date: 8/08/2016
 */
public class Header {
	@Property
	@Parameter(required = true)
	private String title;
}
