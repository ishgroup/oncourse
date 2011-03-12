package ish.oncourse.services.textile.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ish.oncourse.model.Tag;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.TagsTextileAttributes;
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

	public TagsTextileRenderer(ITagService tagService,
			IPageRenderer pageRenderer) {
		this.tagService = tagService;
		this.pageRenderer = pageRenderer;
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
			Map<String, String> tagParams = TextileUtil.getTagParams(tag,
					TagsTextileAttributes.getAttrValues());
			String entityType = tagParams
					.get(TagsTextileAttributes.TAGS_ENTITY_TYPE_PARAM
							.getValue());
			// String maxLevels =
			// tagParams.get(TextileUtil.TAGS_MAX_LEVELS_PARAM);
			String showDetails = tagParams
					.get(TagsTextileAttributes.TAGS_SHOW_DETAIL_PARAM
							.getValue());
			// String hideTopLevel = tagParams
			// .get(TextileUtil.TAGS_HIDE_TOP_LEVEL);
			String filteredParam = tagParams
					.get(TagsTextileAttributes.TAGS_FILTERED_PARAM.getValue());
			String paramName = tagParams
					.get(TagsTextileAttributes.TAGS_PARAM_NAME.getValue());

			Tag parentTag = null;
			Tag rootTag = tagService.getSubjectsTag();
			if (paramName != null) {
				parentTag = tagService.getSubTagByName(paramName);
			} else {
				parentTag = rootTag;
			}
			if (parentTag != null) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				
				parameters.put(TextileUtil.TEXTILE_TAGS_PAGE_ROOT_TAG_PARAM,
						parentTag);
				parameters.put(TextileUtil.TEXTILE_TAGS_PAGE_ENTITY_PARAM,
						entityType);
				parameters.put(TextileUtil.TEXTILE_TAGS_PAGE_DETAILS_PARAM,
						showDetails);
				tag = pageRenderer.renderPage(TextileUtil.TEXTILE_TAGS_PAGE,
						parameters);

			}
		}
		return tag;
	}

}
