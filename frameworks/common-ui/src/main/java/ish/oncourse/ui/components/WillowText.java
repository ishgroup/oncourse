package ish.oncourse.ui.components;

import java.text.Format;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.services.format.FormatName;
import ish.oncourse.services.format.IFormatService;

public class WillowText {

	@Property
	@Parameter
	private Object value;

	@Property
	@Parameter
	private boolean escapingHTML;

	@Inject
	private IFormatService formatService;

	public Format getFormat() {
		return formatService.formatter(FormatName.TO_STRING);
	}
}
