package ish.oncourse.enrol.components.checkout.contact;

import ish.oncourse.model.Contact;
import org.apache.tapestry5.annotations.Parameter;

public class AvetmissEditor {

	@Parameter(required = true)
	private Contact contact;

	public String value(String fieldName)
	{
		return contact.getStudent().readProperty(fieldName).toString();
	}


	public enum Field
	{

	}
}
