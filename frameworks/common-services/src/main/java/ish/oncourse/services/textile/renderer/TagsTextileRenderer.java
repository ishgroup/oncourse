package ish.oncourse.services.textile.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ish.oncourse.model.Tag;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.validator.TagsTextileValidator;
import ish.oncourse.util.IPageRenderer;
import ish.oncourse.util.ValidationErrors;

/**
 * Displays a tree of tags links
 * 
 * <pre>
 * Example: 
 * 
 * {tags entityType:&quot;Course&quot; maxLevels:&quot;3&quot; showtopdetail:&quot;false&quot; 
 *  isHidingTopLevelTags:&quot;true&quot; isFiltered:&quot;false&quot; name:&quot;name&quot; } 
 * 
 * The parameters are as follows: 
 * 
 * entityType: the name of the entity you want to get the tags tree for. Default is Course.
 * 
 * maxLevels: parameter that shows how deep we want to render the tree 
 * 
 * showtopdetail: if true, the top level tags will display their detail text if they have
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
// TODO implement filtered attribute
public class TagsTextileRenderer extends AbstractRenderer {

	private ITagService tagService;
	
	private IPageRenderer pageRenderer;

	public TagsTextileRenderer(ITagService tagService, IPageRenderer pageRenderer) {
		this.tagService = tagService;
		validator = new TagsTextileValidator(tagService);
		this.pageRenderer = pageRenderer;
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
			Map<String, String> tagParams = TextileUtil.getTagParams(tag,
					TextileUtil.TAGS_ENTITY_TYPE_PARAM,
					TextileUtil.TAGS_MAX_LEVELS_PARAM,
					TextileUtil.TAGS_SHOW_DETAIL_PARAM,
					TextileUtil.TAGS_HIDE_TOP_LEVEL,
					TextileUtil.TAGS_FILTERED_PARAM, TextileUtil.PARAM_NAME);
			String entityType = tagParams
					.get(TextileUtil.TAGS_ENTITY_TYPE_PARAM);
			//String maxLevels = tagParams.get(TextileUtil.TAGS_MAX_LEVELS_PARAM);
			String showDetails = tagParams
					.get(TextileUtil.TAGS_SHOW_DETAIL_PARAM);
			//String hideTopLevel = tagParams
				//	.get(TextileUtil.TAGS_HIDE_TOP_LEVEL);
			String filteredParam = tagParams
					.get(TextileUtil.TAGS_FILTERED_PARAM);
			String paramName = tagParams.get(TextileUtil.PARAM_NAME);

			Tag parentTag = null;
			Tag rootTag = tagService.getRootTag();
			if (paramName != null) {
				parentTag = tagService.getSubTagByName(paramName);
				/*if (hideTopLevel == null) {
					hideTopLevel = "false";
				}*/
			} else {
				parentTag = rootTag;
				//hideTopLevel = "true";
			}
			if (parentTag != null) {
				/*return getResult(parentTag, entityType,
						maxLevels != null ? Integer.valueOf(maxLevels) : 1,
						showDetails != null ? Boolean.valueOf(showDetails)
								: null, Boolean.valueOf(hideTopLevel),
						filteredParam != null ? Boolean.valueOf(filteredParam)
								: null, 0);*/
				Map<String, Object> parameters=new HashMap<String, Object>();
				List<Tag> textileTags = new ArrayList<Tag>();
				if(parentTag.equals(rootTag)){
					textileTags.addAll(parentTag.getWebVisibleTags());
				}else{
					textileTags.add(parentTag);
				}
				parameters.put("textileTags", textileTags);
				parameters.put("textileTagsEntityType", entityType);
				parameters.put("textileTagsShowDetails", showDetails);
				return pageRenderer.renderPage("ui/TextileTags", parameters);
				
			}
		}
		return "";
	}

	@Deprecated
	public String getResult(Tag parentTag, String entityType,
			Integer maxLevels, Boolean showDetails, Boolean hideTopLevel,
			Boolean filteredParam, Integer level) {
		String result = "";
		if (!hideTopLevel) {
			result += "<div class=\"taggroup\"><ul>"
					+ listOpening(entityType, parentTag, true);
		}
		result += "<div class=\"taggroup\"><ul>";
		for (Tag subTag : parentTag.getWebVisibleTags()) {
			result += listOpening(entityType, subTag, hideTopLevel
					&& level == 0);
			if (Boolean.TRUE.equals(showDetails) && subTag.getDetail() != null) {
				result += "<div class=\"taggroup_detail\">"
						+ subTag.getDetail() + "</div>";
			}
			if (!subTag.getWebVisibleTags().isEmpty()
					&& (maxLevels == null || maxLevels > 0)) {
				result += getResult(subTag, entityType,
						maxLevels == null ? null : maxLevels - 1, showDetails,
						true, filteredParam, level + 1);
			}
			result += "</li>";
		}

		result += "</ul></div>";
		if (!hideTopLevel) {
			result += "</li></ul></div>";
		}
		return result;
	}
	@Deprecated
	private String listOpening(String entityType, Tag tag, Boolean inHeaders) {
		String result = "<li id=\"t" + tag.getId() + "\">";
		if (inHeaders) {
			result += "<h2>";
		}
		result += "<a href=\"" + tag.getLink(entityType) + "\">"
				+ tag.getName() + "</a>";
		if (inHeaders) {
			result += "</h2>";
		}
		return result;
	}

	
}
