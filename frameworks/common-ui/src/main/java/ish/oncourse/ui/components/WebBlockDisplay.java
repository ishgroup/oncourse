package ish.oncourse.ui.components;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.model.WebContent;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.util.ValidationException;

public class WebBlockDisplay {

	@Inject
	private ITextileConverter textileConverter;

	@Property
	@Parameter
	private WebContent displayedBlock;

	public String getContent() {
		ValidationErrors errors = new ValidationErrors();
		String content = displayedBlock.getContent();
		Pattern pattern = Pattern.compile(TextileUtil.TEXTILE_REGEXP);
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = textileConverter.convertCustomTextile(content, errors);
		}
		if (errors.hasFailures()) {
			try {
				throw new ValidationException(errors);
			} catch (ValidationException e) {
				e.printStackTrace();
			}
		}
		return content;
	}
}
