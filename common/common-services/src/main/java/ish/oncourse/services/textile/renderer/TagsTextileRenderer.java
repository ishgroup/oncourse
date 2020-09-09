package ish.oncourse.services.textile.renderer;

import ish.oncourse.model.Tag;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.CustomTemplateDefinition;
import ish.oncourse.services.IReachtextConverter;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.TagsTextileAttributes;
import ish.oncourse.services.textile.validator.TagsTextileValidator;
import ish.oncourse.util.IPageRenderer;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Displays a tree of tags links
 * <p/>
 * <pre>
 * <p>Example:</p>
 * <br/>
 * {tags maxLevels:&quot;3&quot; isShowDetail:&quot;false&quot;
 *  hideTopLevel:&quot;true&quot; name:&quot;/Subjects/tag&quot; }
 *
 *
 * <p>Description:</p>
 * <ul>
 * <li>name - Optional. Defines the path to a tag. The full path to the tag must be specified. e.g. "/Subjects/Leisure/Arts and Craft" and will display all child tags including the specified tag.</li>
 * <li>maxLevels - Optional. Defining this option will limit how many levels of the tag tree will be displayed. For example, "1" will only show the top level tag and nothing else.</li>
 * <li>showDetail - [true, false] Optional. If true, a tag's description will also be displayed. The default option is false.</li>
 * <li>hideTopLevel - [true, false] Optional. If true, the top level tag is not displayed. The default option is false.</li>
 * </ul>
 * If none of the attributes is specified, it displays the whole tree.
 * </pre>
 */
public class TagsTextileRenderer extends AbstractRenderer {

	private ITagService tagService;
	private IPageRenderer pageRenderer;
	private IReachtextConverter converter;

	public TagsTextileRenderer(final ITagService tagService, final IPageRenderer pageRenderer, final IReachtextConverter converter) {
		this.tagService = tagService;
		this.pageRenderer = pageRenderer;
		this.converter = converter;
		validator = new TagsTextileValidator(tagService);
	}

	@Override
	protected String internalRender(String tag) {
		Map<String, String> tagParams = TextileUtil.getTagParams(tag,
				TagsTextileAttributes.getAttrValues());
		String paramName = tagParams.get(TagsTextileAttributes.TAGS_PARAM_NAME.getValue());
		String maxLevels = tagParams
				.get(TagsTextileAttributes.TAGS_MAX_LEVELS_PARAM.getValue());
		String showDetails = tagParams.get(TagsTextileAttributes.TAGS_SHOW_DETAIL_PARAM
				.getValue());
		String hideTopLevel = tagParams.get(TagsTextileAttributes.TAGS_HIDE_TOP_LEVEL
				.getValue());
		String templateFileName = tagParams.get(TagsTextileAttributes.TAGS_TEMPLATE_FILE_NAME.getValue());

		String multiSelectTags = tagParams.get(TagsTextileAttributes.TAGS_MULTI_SELECT.getValue());

		Tag parentTag = null;
		Tag rootTag = tagService.getSubjectsTag();
		if (paramName != null) {
			parentTag = tagService.getTagByFullPath(paramName);
		} else {
			parentTag = rootTag;
		}
		if (parentTag != null) {
			Map<String, Object> parameters = new HashMap<>();

			parameters.put(TextileUtil.TEXTILE_TAGS_PAGE_ROOT_TAG_PARAM, parentTag);
			parameters.put(TextileUtil.TEXTILE_TAGS_PAGE_MAX_LEVEL_PARAM,
					maxLevels == null ? null : Integer.valueOf(maxLevels));
			parameters.put(TextileUtil.TEXTILE_TAGS_PAGE_DETAILS_PARAM,
					Boolean.valueOf(showDetails));
			parameters.put(TextileUtil.TEXTILE_TAGS_PAGE_HIDE_TOP_PARAM,
					Boolean.valueOf(hideTopLevel));

			if (Boolean.valueOf(multiSelectTags)) {
				CustomTemplateDefinition
						ctd = new CustomTemplateDefinition();
				ctd.setTemplateClassName("TagItem");
				ctd.setTemplateFileName("MultiSelectTagItem.tml");
				parameters.put(TextileUtil.CUSTOM_TEMPLATE_DEFINITION, ctd);
				parameters.put(TextileUtil.TEXTILE_TAGS_PAGE_MULTISELECT_TAGS_PARAM, "true");
			}

			if (templateFileName != null) {
				CustomTemplateDefinition
						ctd = new CustomTemplateDefinition();
				ctd.setTemplateClassName("TagItem");
				ctd.setTemplateFileName(templateFileName);
				parameters.put(TextileUtil.CUSTOM_TEMPLATE_DEFINITION, ctd);
			}

			tag = pageRenderer.renderPage(TextileUtil.TEXTILE_TAGS_PAGE, parameters);
			//if any additional compilation required need to analyze this and apply.
			Pattern pattern = Pattern.compile(TextileUtil.TEXTILE_REGEXP, Pattern.DOTALL);
			Matcher matcher = pattern.matcher(tag);
			if (matcher.find() && !getErrors().hasFailures()) {
				tag = converter.convertCustomText(tag, getErrors());
			}
		} else {
			tag = null;
		}
		return tag;
	}

}
