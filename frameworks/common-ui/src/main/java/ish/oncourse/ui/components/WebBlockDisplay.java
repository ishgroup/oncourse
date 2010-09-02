package ish.oncourse.ui.components;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.model.WebBlock;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.util.ValidationException;

public class WebBlockDisplay {

	@Inject
	private ITextileConverter textileConverter;
	
	@Property
	@Parameter
	private WebBlock displayedBlock;
	
	public String getContent() {
		ValidationErrors errors = new ValidationErrors();
		String content = textileConverter.convert(displayedBlock.getContent(), errors);
		if(errors.hasFailures()){
			try {
				throw new ValidationException(errors);
			} catch (ValidationException e) {
				e.printStackTrace();
			}
		}
		return content;
	}
}
