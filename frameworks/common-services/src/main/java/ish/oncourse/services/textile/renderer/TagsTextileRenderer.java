package ish.oncourse.services.textile.renderer;

import ish.oncourse.model.Tag;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.validator.TagsTextileValidator;
import ish.oncourse.util.ValidationErrors;

/**
 * Displays a tree of tags links
 * 
 * <pre>
 * Example: 
 * 
 * {course entityType:&quot;Course&quot; maxLevels:&quot;3&quot; isShowDetail:&quot;false&quot; 
 *  isHidingTopLevelTags:&quot;true&quot; isFiltered:&quot;false&quot; name:&quot;name&quot; } 
 * 
 * The parameters are as follows: 
 * 
 * entityType: the name of the entity you want to get the tags tree for. Default is Course.
 * 
 * maxLevels: parameter that shows how deep we want to render the tree 
 * 
 * isShowDetail: if true, the top level tags will display their detail text if they have
 * any.
 * 
 * isHidingTopLevelTags: if true, the top level tag is not displayed
 * 
 * isFiltered: If true tags will only be shown if they or any of their child tags have
 * visible linked items (such as classes)
 * 
 * name: allows you to query for a named branch "WITHIN" the "Subjects" tree and display that.
 * 
 * If none of the attributes is specified, it displays the whole tree with Course entity type.
 * </pre>
 */
// TODO implement all the attributes - now only the {tags} form works
public class TagsTextileRenderer extends AbstractRenderer {

	private ITagService tagService;

	public TagsTextileRenderer(ITagService tagService) {
		this.tagService = tagService;
		validator = new TagsTextileValidator(tagService);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ish.oncourse.services.textile.renderer.AbstractRenderer#render(java.lang
	 * .String, ish.oncourse.util.ValidationErrors)
	 */
	@Override
	public String render(String tag, ValidationErrors errors) {
		tag = super.render(tag, errors);
		if (!errors.hasFailures()) {
			Tag parentTag = tagService.getRootTag();
			return getResult(parentTag);

		}
		return "";
	}

	public String getResult(Tag parentTag) {
		String result = "";
		result += "<div class=\"tagGroup\"><ul>";
		for (Tag subTag : parentTag.getTags()) {
			result += "<li id=\"" + subTag.getId() + "\"><h2><a href=\""
					+ getLink(subTag) + "\">" + subTag.getName() + "</a></h2>";
			if (!subTag.getTags().isEmpty()) {
				result += getResult(subTag);
			}
			result += "</li>";
		}
		result += "</ul></div>";
		return result;
	}

	public String getLink(Tag subTag) {
		String link = "";
		while (subTag.getParent() != null) {
			String shortName = subTag.getShortName();
			String name = shortName != null ? shortName : subTag.getName();
			link = "/" + name.replaceAll(" ", "+").replaceAll("/", "|") + link;
			subTag = subTag.getParent();
		}
		link = "/page?p=" + link;
		return link;
	}
}
