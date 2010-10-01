package ish.oncourse.ui.components;

import ish.oncourse.model.Tag;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.ui.utils.GenericSelectModel;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.services.Request;

public class SearchInputs {

	@Inject
	private ITagService tagService;

	@Inject
	private PropertyAccess access;

	@Inject
	private Request request;

	@Property
	@Persist
	private String advKeyword;

	@Persist
	private List<Tag> subjectTagChildTags;

	@Property
	@Persist
	private SelectModel tagModelEnc;

	@Persist
	@Property
	private Tag browseTagLevel2Ancestor;

	@Property
	@Persist
	private String searchNear;

	@Property
	@Persist
	private String searchPrice;

	@Property
	@Persist
	private boolean daytime;

	@Property
	@Persist
	private boolean evening;

	@Property
	@Persist
	private boolean weekday;

	@Property
	@Persist
	private boolean weekend;

	@SetupRender
	void beforeRender() {
		subjectTagChildTags = tagService.getRootTag().getWebVisibleTags();

		Collections.sort(subjectTagChildTags, new Comparator<Tag>() {
			public int compare(Tag tag1, Tag tag2) {
				return tag1.getName().compareTo(tag2.getName());
			}
		});
		tagModelEnc = new GenericSelectModel<Tag>(subjectTagChildTags,
				Tag.class, "name", "id", access);
		Tag browseTag = (Tag) request.getAttribute("browseTag");
		if (browseTag != null) {
			browseTagLevel2Ancestor = browseTag.getLevel2Ancestor();
		}
		// TODO define the default value for searchNear and searchPrice
		// searchNear= CourseClassInMemoryFilter.searchingNearForContext(context());
		// searchPrice=context().request().stringFormValueForKey( "price" );
	}

	URL onActionFromSearch2() {
		try {
			String url = "http://"
					+ request.getServerName()
					+ "/courses?s=" + (advKeyword == null ? "" : advKeyword)
					+ "&subject=" + (browseTagLevel2Ancestor == null ? ""
							: browseTagLevel2Ancestor.getDefaultPath())
					+ "&near=" + (searchNear == null ? "" : searchNear)
					+ "&price=" + (searchPrice == null ? "" : searchPrice)
					+ "&time="
					+ (daytime ? "daytime" : (evening ? "evening" : ""))
					+ "&day="
					+ (weekday ? "weekday" : (weekend ? "weekend" : ""));
			return new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
