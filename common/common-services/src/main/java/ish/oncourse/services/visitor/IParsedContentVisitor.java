/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.visitor;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.visitor.IVisitor;
import ish.oncourse.util.ValidationErrors;

public interface IParsedContentVisitor extends IVisitor<String> {
	
	String visitWebContent(WebContent block);
	String visitWebContent(WebContent block, ValidationErrors errors);
}
