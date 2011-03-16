package ish.oncourse.services.textile.validator;

import ish.oncourse.services.textile.TextileType;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.util.ValidationFailureType;

public abstract class AbstractTextileValidator implements IValidator{

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
		if (!tag.matches(textileType.getDetailedRegexp())) {
			errors.addFailure(getFormatErrorMessage(tag), ValidationFailureType.SYNTAX);
		}
		TextileUtil.checkParamsUniquence(tag, errors, textileType.getAttributes());
		specificTextileValidate(tag, errors);
	}
	
	protected abstract void specificTextileValidate(String tag, ValidationErrors errors);
	
}
