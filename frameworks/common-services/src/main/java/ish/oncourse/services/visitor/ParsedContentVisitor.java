package ish.oncourse.services.visitor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.visitor.BaseVisitor;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.util.ValidationException;

public class ParsedContentVisitor extends BaseVisitor<String> {

	private ITextileConverter textileConverter;

	public ParsedContentVisitor(ITextileConverter textileConverter) {
		this.textileConverter = textileConverter;
	}

	@Override
	public String visitWebContent(WebContent block) {
		String text = block.getContent();

		Pattern pattern = Pattern.compile(TextileUtil.TEXTILE_REGEXP);

		Matcher matcher = pattern.matcher(text);

		if (matcher.find()) {

			ValidationErrors errors = new ValidationErrors();
			text = textileConverter.convertCustomTextile(text, errors);

			if (errors.hasFailures()) {
				try {
					throw new ValidationException(errors);
				} catch (ValidationException e) {
					e.printStackTrace();
				}
			}
		}

		return text;
	}

}
