package ish.oncourse.ui.components;

import ish.oncourse.model.Tag;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.util.URLUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class SearchInputs {

    private static final String URL_PATH_SEARCH_PATTERN = "/courses?s=%s" +
            "%s" +
            "&near=%s" +
            "&price=%s" +
            "&time=%s" +
            "&day=%s";
	private static final String SEARCH_TAG_NAMES_SEPARATOR = ";";
	@Inject
	private ITagService tagService;
	
	@Inject
    @Property
	private Request request;

	@Property
	private String advKeyword;

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

	@Property
	@Parameter
	private String searchTagNames;

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
	 * @return Subjects sub tag names.
	 */
	@Deprecated
	public List<String> getTagNames() {
		return tagGroupModelMap.get(Tag.SUBJECTS_TAG_NAME);
	}

	/**
	 * Methods added for old template backward compatibility
	 * @return Subjects selected sub tag name.
	 */
	@Deprecated
	public String getTagName() {
		return selectedTagMap.get(Tag.SUBJECTS_TAG_NAME);
	}

	/**
	 * Methods added for old template backward compatibility
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
	 * @return default mapped value
	 */
	public String getSelectedTagValue() {
		return selectedTagMap.get(tagGroup);
	}

	/**
	 * Setter for write values in map when submit pressed.
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
			String value = null;
			if (checkBrowseTag && browseTag != null && tagModel.contains(browseTag.getName())) {
				value = browseTag.getName();
			}
			selectedTagMap.put(requestedTagGroup, value);
		}
	}

	/**
	 * We need to use onPrepare() tapestry lifecycle method to be sure that tagGroups will be intitalize for
	 * any request including form submit.
	 */
	void onPrepare()
	{
		fillTagGroups(true);
	}

	URL onActionFromSearch2() {
		StringBuilder tags = new StringBuilder();
		for (String tagName : tagGroups) {
			String value = selectedTagMap.get(tagName);
			for (Tag tag : tagGroupMap.get(tagName)) {
				if (StringUtils.trimToNull(value) != null) {
					if (value.equals(tag.getName())) {
						//add selected tag if something selected
						tags.append("&tag=").append(tag.getDefaultPath());
						break;
					}
				}
				//this code commented because in old implementation (before #20305) when we select all option, we skip tag filtering
				//but with new implementation this option may be required
				/* else {
					//add tag group if selected all
					tags.append("&tag=").append(tagName);
					break;
				}*/
			}
		}
		try {
            String path = String.format(URL_PATH_SEARCH_PATTERN,
                    (advKeyword == null ? StringUtils.EMPTY : advKeyword),
                    (tags.length() > 0 ? tags.toString() : StringUtils.EMPTY),
                    (searchNear == null ? StringUtils.EMPTY : searchNear),
                    (searchPrice == null ? StringUtils.EMPTY : searchPrice),
                    (daytime ? "daytime" : (evening ? "evening" : StringUtils.EMPTY)),
                    (weekday ? "weekday" : (weekend ? "weekend" : StringUtils.EMPTY)));
			return URLUtils.buildURL(request, path, false);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
