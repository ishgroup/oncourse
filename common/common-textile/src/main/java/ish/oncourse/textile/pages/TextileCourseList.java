package ish.oncourse.textile.pages;

import ish.oncourse.model.Course;
import ish.oncourse.model.Tag;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.courseList.PageModel;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class TextileCourseList {

	@Inject
	private Request request;

	@Property
	private Course course;

	@Property
	private int index;

	@Property
	private PageModel model;

	@Inject
	private Block titles, details;

	@Inject
	@Property
	private Block courses;


	@BeginRender
	void beginRender() {
		model = (PageModel) request.getAttribute(TextileUtil.TEXTILE_COURSELIST_MODEL_PARAM);
	}

	public Block getStyleBlock() {
		switch (model.getStyle()) {

			case details:
				return details;
			case titles:
				return titles;
			default:
				throw new IllegalArgumentException();
		}
	}

	public Tag getTag()
	{
		if (model.isShowTags())
			return model.getChildTags().get(index);
		else
			return model.getTag();

	}

}
