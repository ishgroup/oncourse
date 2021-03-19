package ish.oncourse.ui.components;

import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.model.SearchParam;
import ish.oncourse.model.Tag;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.util.URLUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.*;

import static ish.oncourse.model.SearchParam.near;
import static ish.oncourse.model.SearchParam.s;
import static java.lang.String.format;
import static java.net.URLEncoder.encode;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class SearchInputs extends ISHCommon {
	private static final Logger logger = LogManager.getLogger();

	private static final String SEARCH_TAG_NAMES_SEPARATOR = ";";
	@Inject
	private ITagService tagService;

	@Property
	private String advKeyword;

	@Property
	private String searchNear;

	@Property
	private String km;

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

	@Property
	@Parameter
	private String searchTagNames;

	@Property
	@Parameter
	private Boolean showSubjectsAllTag;

	@Property
	private List<String> tagGroups;

	@Property
	private String tagGroup;

	@Property
	private Map<String, List<Tag>> tagGroupMap;

	@Property
	private Map<String, List<String>> tagGroupModelMap;

	@Property
	private Map<String, String> selectedTagMap;

	public List<String> getTagGroupModel() {
		return tagGroupModelMap.get(tagGroup);
	}

	/**
	 * Methods added for old template backward compatibility
	 *
	 * @return Subjects sub tag names.
	 */
	@Deprecated
	public List<String> getTagNames() {
		return tagGroupModelMap.get(Tag.SUBJECTS_TAG_NAME);
	}

	/**
	 * Methods added for old template backward compatibility
	 *
	 * @return Subjects selected sub tag name.
	 */
	@Deprecated
	public String getTagName() {
		return selectedTagMap.get(Tag.SUBJECTS_TAG_NAME);
	}

	/**
	 * Methods added for old template backward compatibility
	 *
	 * @param value - Subjects selected sub tag name
	 */
	@Deprecated
	public void setTagName(String value) {
		if (selectedTagMap == null) {
			fillTagGroups(false);
		}
		selectedTagMap.put(Tag.SUBJECTS_TAG_NAME, value);
	}

	/**
	 * Getter to receive the default mapped value for match of browseTag.
	 * Used to access values from map by dynamic tagGroup on component render.
	 *
	 * @return default mapped value
	 */
	public String getSelectedTagValue() {
		return selectedTagMap.get(tagGroup);
	}

	/**
	 * Setter for write values in map when submit pressed.
	 *
	 * @param value - drop down selection values.
	 */
	public void setSelectedTagValue(String value) {
		if (selectedTagMap == null) {
			fillTagGroups(false);
		}
		selectedTagMap.put(tagGroup, value);
	}

	/**
	 * Init tag groups, tag map, tag model and selected tag map by searchTagNames parameter.
	 *
	 * @param checkBrowseTag - describe the root search elements.
	 */
	private void fillTagGroups(boolean checkBrowseTag) {
		//if no parameter passed we should use Subjects as in previous implementtation.
		if (StringUtils.trimToNull(searchTagNames) == null) {
			searchTagNames = Tag.SUBJECTS_TAG_NAME;
		}
		String[] requestedTagGroups = searchTagNames.split(SEARCH_TAG_NAMES_SEPARATOR);
		tagGroups = new ArrayList<>();
		tagGroupMap = new HashMap<>();
		tagGroupModelMap = new HashMap<>();
		selectedTagMap = new HashMap<>();
		//extract currently applied tag to have ability to set it as default selection for corresponding tag group
		Tag browseTag = tagService.getBrowseTag();
		for (String requestedTagGroup : requestedTagGroups) {
			List<String> tagModel = new ArrayList<>();
			tagGroups.add(requestedTagGroup);
			Tag requestedTag = tagService.getTagGroupByName(requestedTagGroup);
			List<Tag> childTags = requestedTag != null ? requestedTag.getWebVisibleTags() : Collections.EMPTY_LIST;
			Collections.sort(childTags, new Comparator<Tag>() {
				public int compare(Tag tag1, Tag tag2) {
					return tag1.getName().compareTo(tag2.getName());
				}
			});
			tagGroupMap.put(requestedTagGroup, childTags);

			for (Tag childTag : childTags) {
				tagModel.add(childTag.getName());
			}

			tagGroupModelMap.put(requestedTagGroup, tagModel);
			if (showSubjectsAllTag == null || !showSubjectsAllTag) {
				String value = null;
				if (checkBrowseTag && browseTag != null && tagModel.contains(browseTag.getName())) {
					value = browseTag.getName();
				}
				selectedTagMap.put(requestedTagGroup, value);
			}
		}
	}

	/**
	 * We need to use onPrepare() tapestry lifecycle method to be sure that tagGroups will be intitalize for
	 * any request including form submit.
	 */
	void onPrepare() {
		fillTagGroups(true);
	}

	URL onActionFromSearch2() {
		try {
			return BuildURL.valueOf(this).build();
		} catch (Exception e) {
			logger.error(e);
			throw new RuntimeException(e);
		}
	}


	public static class BuildURL {
		private Request request;

		private String browseTag;
		private List<String> tags;
		private Map<SearchParam, String> searchParams;

		private List<String> queries = new ArrayList<>();

		public URL build() throws Exception {
			for (SearchParam key : SearchParam.values()) {
				if (isNotBlank(searchParams.get(key))) {
					queries.add(format("%s=%s", key.name(), encode(searchParams.get(key), CharEncoding.UTF_8)));
				}
			}

			for (String tag : tags) {
				queries.add(format("%s=%s", SearchParam.tag.name(), encodePath(tag)));
			}

			String path = "/courses";
			if (browseTag != null) {
				path += encodePath(browseTag);
			}
			if (queries.size() > 0) {
				path += "?" + StringUtils.join(queries, "&");
			}
			return URLUtils.buildURL(request, path, false);
		}

		private String encodePath(String path) {
			try {
				String[] items = StringUtils.split(path, "/");
				for (int i = 0; i < items.length; i++) {
					items[i] =  encode(items[i], CharEncoding.UTF_8);
				}
				return new StringBuilder("/").append(StringUtils.join(items, '/')).toString();
			} catch (UnsupportedEncodingException e) {
				throw new IllegalArgumentException(e);
			}
		}

		private void initSearchParams(SearchInputs form) {
			searchParams = new HashMap<>();
			searchParams.put(s, form.advKeyword);
			searchParams.put(near, form.searchNear);
			searchParams.put(SearchParam.km, form.km);
			searchParams.put(SearchParam.price, form.searchPrice);
			searchParams.put(SearchParam.time, (form.daytime ? "daytime" : (form.evening ? "evening" : StringUtils.EMPTY)));
			searchParams.put(SearchParam.day, (form.weekday ? "weekday" : (form.weekend ? "weekend" : StringUtils.EMPTY)));
		}

		private void initTags(SearchInputs form) {
			this.tags = new ArrayList<>();
			for (String tagName : form.tagGroups) {
				String value = form.selectedTagMap.get(tagName);
				for (Tag tag : form.tagGroupMap.get(tagName)) {
					if (isNotBlank(value)) {
						if (value.equals(tag.getName())) {
							//add selected tag if something selected
							if (browseTag == null) {
								browseTag = tag.getDefaultPath();
							} else {
								tags.add(tag.getDefaultPath());
							}
							break;
						}
					}
				}
			}
		}


		public static BuildURL valueOf(SearchInputs form) {
			BuildURL result = new BuildURL();
			result.initSearchParams(form);
			result.initTags(form);
			result.request = form.request;
			return result;
		}

		public static BuildURL valueOf(Request request,
				String browseTag,
				List<String> tags,
				Map<SearchParam, String> searchParams) {
			BuildURL buildURL = new BuildURL();
			buildURL.request = request;
			buildURL.browseTag = browseTag;
			buildURL.tags = tags;
			buildURL.searchParams =searchParams;
			return buildURL;
		}

	}
}
