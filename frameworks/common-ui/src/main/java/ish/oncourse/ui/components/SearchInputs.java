package ish.oncourse.ui.components;

import ish.oncourse.model.Tag;
import ish.oncourse.selectutils.ListSelectModel;
import ish.oncourse.selectutils.ListValueEncoder;
import ish.oncourse.services.tag.ITagService;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
	private String advKeyword;

	private List<Tag> subjectTagChildTags;

	@Property
	private ListSelectModel<Tag> tagModel;
	
	@Property
	private ListValueEncoder<Tag> tagEnc;

	@Property
	private Tag browseTagLevel2Ancestor;

	@Property
	private String searchNear;

	@Property
	private String searchPrice;

	@Property
	private boolean daytime;

	@Property
	private boolean evening;

	@Property
	private boolean weekday;

	@Property
	private boolean weekend;

	@SetupRender
	void beforeRender() {
		subjectTagChildTags = tagService.getSubjectsTag().getWebVisibleTags();

		Collections.sort(subjectTagChildTags, new Comparator<Tag>() {
			public int compare(Tag tag1, Tag tag2) {
				return tag1.getName().compareTo(tag2.getName());
			}
		});
		
		this.tagModel = new ListSelectModel<Tag>(subjectTagChildTags, Tag.NAME_PROPERTY, access);
		this.tagEnc = new ListValueEncoder<Tag>(subjectTagChildTags, "id", access);
		
		Tag browseTag = (Tag) request.getAttribute("browseTag");
		if (browseTag != null) {
			browseTagLevel2Ancestor = browseTag.getLevel2Ancestor();
		}
	}

	URL onActionFromSearch2() {
		try {
			String url = "http://"
					+ request.getServerName()
					+ "/courses?s="
					+ (advKeyword == null ? "" : advKeyword)
					+ "&subject="
					+ (browseTagLevel2Ancestor == null ? ""
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
