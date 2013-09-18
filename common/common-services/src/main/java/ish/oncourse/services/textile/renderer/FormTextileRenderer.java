package ish.oncourse.services.textile.renderer;

import ish.oncourse.model.TextileFormField;
import ish.oncourse.services.textile.TextileType;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.FormTextileAttributes;
import ish.oncourse.services.textile.attrs.PopupListTextileAttributes;
import ish.oncourse.services.textile.attrs.RadioListTextileAttributes;
import ish.oncourse.services.textile.attrs.TextTextileAttributes;
import ish.oncourse.services.textile.validator.FormTextileValidator;
import ish.oncourse.util.IPageRenderer;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormTextileRenderer extends AbstractRenderer {
	private static final int DEFAULT_TEXTAREA_ROWS = 10;
	private IPageRenderer pageRenderer;

	public FormTextileRenderer(IPageRenderer pageRenderer) {
		this.pageRenderer = pageRenderer;
		validator = new FormTextileValidator();
	}

	@Override
	protected String internalRender(String tag) {
		Map<String, Object> parameters = new HashMap<>();
		Map<String, String> tagParams = TextileUtil.getTagParams(tag, FormTextileAttributes.getAttrValues());
		parameters.put(TextileUtil.TEXTILE_FORM_PAGE_NAME_PARAM, tagParams.get(FormTextileAttributes.NAME.getValue()));
		parameters.put(TextileUtil.TEXTILE_FORM_PAGE_EMAIL_PARAM, tagParams.get(FormTextileAttributes.EMAIL.getValue()));
		parameters.put(TextileUtil.TEXTILE_FORM_PAGE_URL_PARAM, tagParams.get(FormTextileAttributes.URL.getValue()));

		List<TextileFormField> fields = new ArrayList<>();
		Pattern textPattern = Pattern.compile(TextileType.FORM_FIELDS_PATTERN);
		Matcher textMatcher = textPattern.matcher(tag);
		String content = tag.replaceAll("\\{form([^}]*)}", StringUtils.EMPTY);
		String result = StringUtils.EMPTY;
		while (textMatcher.find()) {
			String text = textMatcher.group();
			List<String> paramKeys = null;
			boolean isRadio = false;
			boolean isText = false;
			boolean isPopup = false;
			if (text.matches(TextileType.TEXT.getRegexp())) {
				paramKeys = TextTextileAttributes.getAttrValues();
				isText = true;
			}
			if (text.matches(TextileType.RADIOLIST.getRegexp())) {
				paramKeys = RadioListTextileAttributes.getAttrValues();
				isRadio = true;
			}
			if (text.matches(TextileType.POPUPLIST.getRegexp())) {
				paramKeys = PopupListTextileAttributes.getAttrValues();
				isPopup = true;
			}
			Map<String, String> textParams = TextileUtil.getTagParams(text, paramKeys);
			TextileFormField textileFormField = new TextileFormField();
			if (isText) {
				textileFormField.setText(isText);
				textileFormField.setLabel(textParams.get(TextTextileAttributes.LABEL.getValue()));
				textileFormField.setRequired(parseBooleanTextileAttribute(textParams, TextTextileAttributes.REQUIRED.getValue()));
				String linesParam = textParams.get(TextTextileAttributes.LINES.getValue());
				if (linesParam == null) {
					textileFormField.setTextAreaRows(null);
				} else {
					if (parseBooleanTextileAttribute(textParams, TextTextileAttributes.LINES.getValue())) {
						textileFormField.setTextAreaRows(DEFAULT_TEXTAREA_ROWS);
					} else {
						textileFormField.setTextAreaRows(Integer.parseInt(linesParam));
					}
				}
				String maxLengthParam = textParams.get(TextTextileAttributes.MAXLENGTH.getValue());
				textileFormField.setMaxLength((maxLengthParam == null || !maxLengthParam.matches("\\d+")) ? null
						: Integer.parseInt(maxLengthParam));
			}

			if (isRadio) {
				textileFormField.setRadio(isRadio);
				textileFormField.setLabel(textParams.get(RadioListTextileAttributes.LABEL.getValue()));
				textileFormField.setRequired(parseBooleanTextileAttribute(textParams, RadioListTextileAttributes.REQUIRED.getValue()));
				textileFormField.setDefaultValue(textParams.get(RadioListTextileAttributes.DEFAULT.getValue()));
				String options = textParams.get(RadioListTextileAttributes.OPTIONS.getValue());
				if (options != null) {
					textileFormField.setOptions(options.split(","));
				}
			}

			if (isPopup) {
				textileFormField.setPopup(isPopup);
				textileFormField.setLabel(textParams.get(PopupListTextileAttributes.LABEL.getValue()));
				textileFormField.setRequired(parseBooleanTextileAttribute(textParams, PopupListTextileAttributes.REQUIRED.getValue()));
				textileFormField.setDefaultValue(textParams.get(PopupListTextileAttributes.DEFAULT.getValue()));
				String options = textParams.get(PopupListTextileAttributes.OPTIONS.getValue());
				if (options != null) {
					textileFormField.setOptions(options.split(","));
				}
			}
			int startTag = content.indexOf(text);
			result = content.substring(0, startTag);
			content = content.substring(startTag + text.length());
			textileFormField.setBeforeFieldMarkUp(result);

			fields.add(textileFormField);
		}
		parameters.put(TextileUtil.TEXTILE_FORM_PAGE_FIELDS_PARAM, fields);
		parameters.put(TextileUtil.TEXTILE_FORM_PAGE_AFTER_FIELDS_PARAM, content);
		tag = pageRenderer.renderPage(TextileUtil.TEXTILE_FORM_PAGE, parameters);
		// TODO uncomment this when the validation of the form is needed, now we
		// just pass all the text
		// }

		return tag;
	}

}
