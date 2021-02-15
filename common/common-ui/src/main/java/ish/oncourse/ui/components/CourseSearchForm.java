package ish.oncourse.ui.components;

import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.model.Tag;
import ish.oncourse.selectutils.ListSelectModel;
import ish.oncourse.selectutils.ListValueEncoder;
import ish.oncourse.services.tag.ITagService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CourseSearchForm extends ISHCommon {
	private static final Logger logger = LogManager.getLogger();

	static final String URL_PATH_SEARCH_PATTERN = "/courses?s=%s" +
            "&subject=%s" +
            "&near=%s" +
            "&price=%s" +
            "&time=%s" +
            "&day=%s";

	@Inject
	private ITagService tagService;

	@Inject
	private PropertyAccess access;

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
        if (subjectsTag == null) {
			subjectTagChildTags = new ArrayList<>();
		} else {
			subjectTagChildTags = subjectsTag.getWebVisibleTags();
		}

		Collections.sort(subjectTagChildTags, new Comparator<Tag>() {
			public int compare(Tag tag1, Tag tag2) {
				return tag1.getName().compareTo(tag2.getName());
			}
		});

		this.tagModel = new ListSelectModel<>(subjectTagChildTags, Tag.NAME_PROPERTY, access);
		this.tagEnc = new ListValueEncoder<>(subjectTagChildTags, "id", access);
	}

//	URL onActionFromSearch() {
//		try {
//			String urlPath = String.format(URL_PATH_SEARCH_PATTERN,
//                    (s == null ? StringUtils.EMPTY : s),
//                    (subject == null ? StringUtils.EMPTY : subject.getDefaultPath()),
//                    (searchNear == null ? StringUtils.EMPTY : searchNear),
//                    (searchPrice == null ? StringUtils.EMPTY : searchPrice),
//                    (time == null ? StringUtils.EMPTY : time),
//                    (day == null ? StringUtils.EMPTY : day)
//            );
//			return URLUtils.buildURL(request, urlPath, false);
//		} catch (Exception e) {
//			logger.error(e);
//			throw new RuntimeException(e);
//		}
//	}
}
