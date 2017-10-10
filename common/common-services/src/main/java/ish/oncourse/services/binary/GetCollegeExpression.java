/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.binary;

import ish.oncourse.model.College;
import ish.oncourse.model.Document;
import org.apache.cayenne.exp.Expression;

import static ish.common.types.AttachmentInfoVisibility.PRIVATE;
import static ish.common.types.AttachmentInfoVisibility.PUBLIC;
import static ish.oncourse.model.auto._Document.WEB_VISIBILITY;

/**
 * User: akoiro
 * Date: 11/07/2016
 */
public class GetCollegeExpression {
	private College college;
	private boolean hidePrivateAttachments;
	private boolean isStudentLoggedIn;


	public GetCollegeExpression(College college, boolean hidePrivateAttachments, boolean isStudentLoggedIn) {
		this.college = college;
		this.hidePrivateAttachments = hidePrivateAttachments;
		this.isStudentLoggedIn = isStudentLoggedIn;
	}

	public Expression get() {
		Expression expression = Document.COLLEGE.eq(college);

		if (hidePrivateAttachments) {
			if (isStudentLoggedIn) {
				expression = expression.andExp(WEB_VISIBILITY.ne(PRIVATE));
			} else {
				expression = expression.andExp(WEB_VISIBILITY.eq(PUBLIC));
			}
		}
		expression = expression.andExp(Document.IS_REMOVED.eq(Boolean.FALSE));
		return expression;
	}
}
