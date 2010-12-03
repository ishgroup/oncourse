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

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.services.Request;

public class CourseSearchForm {

	@Inject
	private ITagService tagService;
	
	@Inject
	private PropertyAccess access;

	@Inject
	private Request request;

	@Property
	@Persist
	private String s;

	@Property
	@Persist
	private String searchNear;

	@Property
	@Persist
	private String searchPrice;

	@Property
	@Persist
	private String day;

	@Property
	@Persist
	private String time;

	@Property
	@Persist
	private Tag subject;

	@Persist
	private List<Tag> subjectTagChildTags;

	@Property
	@Persist
	private ListSelectModel<Tag> tagModel;
	
	@Property
	@Persist
	private ListValueEncoder<Tag> tagEnc;

	@SetupRender
	void beforeRender() {
		subjectTagChildTags = tagService.getRootTag().getWebVisibleTags();

		Collections.sort(subjectTagChildTags, new Comparator<Tag>() {
			public int compare(Tag tag1, Tag tag2) {
				return tag1.getName().compareTo(tag2.getName());
			}
		});

		this.tagModel = new ListSelectModel<Tag>(subjectTagChildTags, Tag.NAME_PROPERTY, access); 
		this.tagEnc = new ListValueEncoder<Tag>(subjectTagChildTags, "id", access);
	}

	URL onActionFromSearch() {
		try {
			String url = "http://" + request.getServerName() + "/courses?s="
					+ (s == null ? "" : s) + "&subject="
					+ (subject == null ? "" : subject.getDefaultPath())
					+ "&near=" + (searchNear == null ? "" : searchNear)
					+ "&price=" + (searchPrice == null ? "" : searchPrice)
					+ "&time=" + (time == null ? "" : time) + "&day="
					+ (day == null ? "" : day);
			return new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
