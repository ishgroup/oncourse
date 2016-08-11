/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components.certificate;

import ish.oncourse.portal.certificate.Model;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * User: akoiro
 * Date: 11/08/2016
 */
public class Title {
	@Parameter(required = true)
	@Property
	private Model model;

	@Inject
	private Block without;

	@Inject
	private Block skillset;

	@Inject
	private Block qualification;

	@Property
	private Block current;

	@SetupRender
	public void setupRender() {
		if (model.getQualification() != null) {
			switch (model.getQualification().getType()) {
				case QUALIFICATION_TYPE:
				case COURSE_TYPE:
				case HIGHER_TYPE:
					current = qualification;
					break;
				case SKILLSET_TYPE:
				case SKILLSET_LOCAL_TYPE:
					current = skillset;
					break;
				default:
					throw new IllegalArgumentException();
			}
		} else {
			current = without;
		}

	}
}
