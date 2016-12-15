/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.pages;

import ish.oncourse.linktransform.PageLinkTransformer;
import ish.oncourse.model.Tag;
import ish.oncourse.model.WebContent;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.visitor.IParsedContentVisitor;
import ish.oncourse.util.ValidationErrors;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.internal.test.TestableRequest;
import org.apache.tapestry5.ioc.annotations.Inject;

public class TagsTextile {

	@Inject
	private IParsedContentVisitor visitor;

	@Inject
	private TestableRequest request;

	@Inject
	private ICayenneService cayenneService;

	@SetupRender
	public void setRequest() {
		Tag tag = SelectById.query(Tag.class, 3l).selectOne(cayenneService.newContext());
		request.setAttribute(PageLinkTransformer.ATTR_coursesTag, tag);
		request.setPath("/courses/It");
	}
	
	public String getTagsTextile() {
		WebContent tag = ObjectSelect.query(WebContent.class).selectOne(cayenneService.newContext());
		return visitor.visitWebContent(tag, new ValidationErrors());
	}

}
