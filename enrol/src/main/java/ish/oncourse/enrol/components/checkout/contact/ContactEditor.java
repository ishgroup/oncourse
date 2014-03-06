package ish.oncourse.enrol.components.checkout.contact;

import ish.oncourse.enrol.checkout.ConcessionParser;
import ish.oncourse.enrol.checkout.contact.ContactEditorDelegate;
import ish.oncourse.enrol.checkout.contact.ContactEditorParser;
import ish.oncourse.enrol.pages.Checkout;
import ish.oncourse.enrol.services.Constants;
import ish.oncourse.model.Contact;
import ish.oncourse.services.preference.ContactFieldHelper;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.util.FormatUtils;
import ish.oncourse.util.ValidateHandler;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.text.DateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static ish.oncourse.enrol.services.Constants.COMPONENT_submitContact;

public class ContactEditor {

	@Parameter(required = true)
	@Property
	private ContactEditorDelegate delegate;

    @Parameter
    @Property
    private boolean showCancelLink;

	@Parameter
	private Object returnPage;

	@Parameter
	@Property
	private Block returnBlock;

	@Inject
	private PreferenceController preferenceController;

	@Inject
	private ICountryService countryService;

	@Inject
	@Property
	private Request request;

	@Property
	private ValidateHandler validateHandler;

	@InjectComponent
	private AvetmissEditor avetmissEditor;

	@InjectComponent
	private ContactEditorFieldSet contactEditorFieldSet;

	@Inject
	@Id("concession")
	@Property
	private Block concessionBlock;

	@InjectComponent
	@Property
	private Form contactEditorForm;


	@SetupRender
	void setupRender() {
		validateHandler = new ValidateHandler();
		validateHandler.setErrors(delegate.getErrors());
	}

	public boolean isFillRequiredProperties() {
		return delegate.isFillRequiredProperties();
	}

	public ContactFieldHelper getContactFieldHelper()
	{
		return delegate.getContactFieldHelper();
	}

	public Contact getContact() {
		return delegate.getContact();
	}

	@OnEvent(component = COMPONENT_submitContact, value = "selected")
	public  Object submitContact() {

		if (delegate != null)
		{
			ContactEditorParser contactEditorParser = new ContactEditorParser();
			contactEditorParser.setCountryService(countryService);
			contactEditorParser.setRequest(request);
			contactEditorParser.setContact(delegate.getContact());
			contactEditorParser.setContactFieldHelper(getContactFieldHelper());
			contactEditorParser.setMessages(contactEditorFieldSet.getMessages());
			contactEditorParser.setVisibleFields(delegate.getVisibleFields());
			contactEditorParser.parse();
			Map<String,String> errors = new HashMap<>(contactEditorParser.getErrors());

			avetmissEditor.save();
			errors.putAll(avetmissEditor.getErrors());

			if (delegate.isActiveConcessionTypes())
			{
				if (delegate.getConcessionDelegate().getStudentConcession().getConcessionType() != null)
				{
					ConcessionParser concessionParser = ConcessionParser.newInstance(request,
							delegate.getConcessionDelegate().getStudentConcession(),delegate.getContact().getCollege().getTimeZone()
					);
					concessionParser.parse();
					errors.putAll(concessionParser.getErrors());
				}
				else
					//important! we should delete new concession entity if an user did not select concessionType
					delegate.getConcessionDelegate().cancelEditing();
			}

			delegate.setErrors(errors);
			delegate.saveContact();
		}
		return returnPage;
	}

	public DateFormat getDateFormat()
	{
		return  FormatUtils.getDateFormat(Constants.DATE_FIELD_SHOW_FORMAT, getContact().getCollege().getTimeZone());
	}

    @OnEvent(value = "cancelContact")
	public Object cancelContact() {
		if (delegate != null)
		{
			delegate.setErrors(Collections.EMPTY_MAP);
			delegate.cancelContact();
		}
		return returnPage;
	}

    public boolean isStudentEditor()
    {
        return returnPage instanceof Checkout;
    }
}
