/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.binary;

import ish.common.types.AttachmentInfoVisibility;
import ish.oncourse.model.College;
import ish.oncourse.model.Document;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.parser.ASTNotEqual;
import org.apache.cayenne.exp.parser.ASTObjPath;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * User: akoiro
 * Date: 5/08/2016
 */
public class GetCollegeExpressionTest {
	@Test
	public void testIsStudentLoggedIn() {
		College college = Mockito.mock(College.class);
		Expression expression = new GetCollegeExpression(college, true, true).get();
		Assert.assertEquals(Document.WEB_VISIBILITY.getName(), ((ASTObjPath) ((ASTNotEqual) expression.getOperand(1)).getOperand(0)).getPath());
		Assert.assertEquals(AttachmentInfoVisibility.PRIVATE, ((ASTNotEqual) expression.getOperand(1)).getOperand(1));
	}
}
