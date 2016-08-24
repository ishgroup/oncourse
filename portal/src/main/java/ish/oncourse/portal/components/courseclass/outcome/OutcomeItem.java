/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components.courseclass.outcome;


import ish.oncourse.model.Outcome;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;


public class OutcomeItem {

	public static final String COMPETENT_LABEL = "COMPETENT";
	public static final String NOT_YET_COMPETENT_LABEL = "NOT YET COMPETENT";
	public static final String WITHDARAWN_LABEL = "WITHDARAWN";
	public static final String NO_RESULT_LABEL = "NO RESULT";

	private static final String COMPETENT_CLASS = "competent-marking";
	private static final String NOT_YET_COMPETENT_CLASS = "not-yet-competent-marking";
	private static final String WITHDARAWN_CLASS = "withdrawn-marking";
	
	
	@Parameter
	@Property
	private Outcome outcome;

	@Property
	private String outcomeLable;

	@Property
	private String outcomeClass;

	@Property
	private boolean vet;


	@SetupRender
	public void setupRender() {
		vet = outcome.getModule() != null;
		switch (outcome.getStatus()) {
			case STATUS_ASSESSABLE_PASS:
			case STATUS_NON_ASSESSABLE_COMPLETED:
				outcomeLable = COMPETENT_LABEL;
				outcomeClass = COMPETENT_CLASS;
				break; 
			case STATUS_ASSESSABLE_FAIL:
			case STATUS_NON_ASSESSABLE_NOT_COMPLETED:
				outcomeLable = NOT_YET_COMPETENT_LABEL;
				outcomeClass = NOT_YET_COMPETENT_CLASS;
				break;
			case STATUS_ASSESSABLE_WITHDRAWN:
				outcomeLable = WITHDARAWN_LABEL;
				outcomeClass = WITHDARAWN_CLASS;
				break;
			case STATUS_NOT_SET:
				outcomeLable = NO_RESULT_LABEL;
				outcomeClass = StringUtils.EMPTY;
				break;
			default:
				outcomeLable = NOT_YET_COMPETENT_LABEL;
				outcomeClass = StringUtils.EMPTY;
		}
	} 
}
