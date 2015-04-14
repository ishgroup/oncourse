package ish.oncourse.services.textile.validator;

import ish.oncourse.services.textile.TextileType;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.util.ValidationFailureType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractTextileValidator implements IValidator{
	protected static final Logger logger = LogManager.getLogger();
	protected TextileType textileType;
	
	protected abstract void initValidator();
	
	public AbstractTextileValidator() {
		initValidator();
	}
	
	/**
	 * {@inheritDoc}
	 * @see ish.oncourse.services.textile.validator.IValidator#validate(java.lang.String, ish.oncourse.util.ValidationErrors)
	 */
	@Override
	public void validate(String tag, ValidationErrors errors) {
		Pattern pattern = Pattern.compile(textileType.getDetailedRegexp(), Pattern.DOTALL);
		Matcher matcher = pattern.matcher(tag);
		if (!matcher.find()) {
			errors.addFailure(getFormatErrorMessage(tag), ValidationFailureType.SYNTAX);
		}
		//checks the parameters of the first {tag} definition, don't check subtags
		TextileUtil.checkParamsUniquence(tag.substring(0,tag.indexOf("}")+1), errors, textileType.getAttributes());
		specificTextileValidate(tag, errors);
	}
	
	protected abstract void specificTextileValidate(String tag, ValidationErrors errors);
	
	protected String getSyntaxErrorMessage(String name) {
		return "There's incorrect syntax for textile with the name: " + name;
	}
	
}
