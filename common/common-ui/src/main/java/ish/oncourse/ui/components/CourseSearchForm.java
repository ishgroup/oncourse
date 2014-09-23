package ish.oncourse.ui.components;

import ish.oncourse.model.Tag;
import ish.oncourse.selectutils.ListSelectModel;
import ish.oncourse.selectutils.ListValueEncoder;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.util.URLUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.services.Request;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CourseSearchForm {

    private static final String URL_PATH_SEARCH_PATTERN = "/courses?s=%s" +
            "&subject=%s" +
            "&near=%s" +
            "&price=%s" +
            "&time=%s" +
            "&day=%s";

	@Inject
	private ITagService tagService;

	@Inject
	private PropertyAccess access;

	@Inject
	private Request request;

	@Property
	private String s;

	@Property
	private String searchNear;

	@Property
	private String searchPrice;

	@Property
	private String day;

	@Property
	private String time;

	@Property
	private Tag subject;

	private List<Tag> subjectTagChildTags;

	@Property
	private ListSelectModel<Tag> tagModel;

	@Property
	private ListValueEncoder<Tag> tagEnc;

	@SetupRender
	void beforeRender() {

        Tag subjectsTag = tagService.getSubjectsTag();
        if (subjectsTag == null)
            throw new IllegalArgumentException(String.format("college \"%s\" does not contains \"Subjects\" tag.", request.getServerName()));
		subjectTagChildTags = subjectsTag.getWebVisibleTags();

		Collections.sort(subjectTagChildTags, new Comparator<Tag>() {
			public int compare(Tag tag1, Tag tag2) {
				return tag1.getName().compareTo(tag2.getName());
			}
		});

		this.tagModel = new ListSelectModel<>(subjectTagChildTags, Tag.NAME_PROPERTY, access);
		this.tagEnc = new ListValueEncoder<>(subjectTagChildTags, "id", access);
	}

	URL onActionFromSearch() {
		try {
			String urlPath = String.format(URL_PATH_SEARCH_PATTERN,
                    (s == null ? StringUtils.EMPTY : s),
                    (subject == null ? StringUtils.EMPTY : subject.getDefaultPath()),
                    (searchNear == null ? StringUtils.EMPTY : searchNear),
                    (searchPrice == null ? StringUtils.EMPTY : searchPrice),
                    (time == null ? StringUtils.EMPTY : time),
                    (day == null ? StringUtils.EMPTY : day)
            );
			return URLUtils.buildURL(request, urlPath, false);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
