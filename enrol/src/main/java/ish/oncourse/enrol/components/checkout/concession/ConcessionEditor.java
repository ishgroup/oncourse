package ish.oncourse.enrol.components.checkout.concession;

import ish.oncourse.enrol.checkout.ConcessionDelegate;
import ish.oncourse.enrol.checkout.ConcessionValidator;
import ish.oncourse.enrol.pages.Checkout;
import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.Student;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.util.FormatUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConcessionEditor {
	public static final String DATE_FIELD_FORMAT = "MM/dd/yyyy";
	private static final Logger LOGGER = Logger.getLogger(ConcessionEditor.class);

	@Parameter(required = true)
	private ConcessionDelegate delegate;

	@Property
	private ConcessionType concessionType;

	@Property
	private Integer concessionTypeIndex;

	@InjectPage
	private Checkout checkout;

	@Inject
	private Request request;

	@Inject
	private Messages messages;

	@Property
	private DateFormat dateFormat;

	private Map<String,String> errors;

	@SetupRender
	void beforeRender() {
		dateFormat = FormatUtils.getDateFormat(DATE_FIELD_FORMAT,getStudent().getCollege().getTimeZone());
		errors = new HashMap<String, String>();
	}

	public ConcessionDelegate getDelegate()
	{
		return delegate;
	}

	public Student getStudent() {
		return delegate.getStudent();
	}

	public StudentConcession getStudentConcession()
	{
		return delegate.getStudentConcession();
	}

	public List<ConcessionType> getConcessionTypes()
	{
		return getStudent().getCollege().getActiveConcessionTypes();
	}


	public Object onActionFromCancelConcessionLink(Long contactId) {
		if (!request.isXHR())
			return null;

		if (delegate != null)
			delegate.cancelEditing(contactId);
		if (checkout.getCheckoutBlock() != null)
			return checkout.getCheckoutBlock();
		return null;
	}

	public Object onActionFromSaveConcessionLink(Long contactId) {
		if (!request.isXHR())
			return null;
		if (delegate != null)
		{
			ConcessionValidator validator = createValidator();
			validator.validate();

			errors = validator.getErrors();

			if (errors.isEmpty())
			{
				delegate.fieldsChanged(validator.getNumber(), validator.getExpiry());
				delegate.saveConcession(contactId);
			}
		}
		if (checkout.getCheckoutBlock() != null)
			return checkout.getCheckoutBlock();
		return null;
	}

	private ConcessionValidator createValidator() {
		ConcessionValidator validator = new ConcessionValidator();
		validator.setConcessionType(delegate.getStudentConcession().getConcessionType());
		validator.setMessages(messages);
		validator.setRequest(request);
		validator.setDateFormat(FormatUtils.getDateFormat(DATE_FIELD_FORMAT,getStudent().getCollege().getTimeZone()));
		return validator;
	}


	public boolean isSelectedConcessionType()
	{
		return delegate.getStudentConcession() != null && delegate.getStudentConcession().getConcessionType().getId().equals(concessionType.getId());
	}

	public Object onActionFromSelectConcessionTypeLink(Integer concessionTypeIndex)
	{
		if (!request.isXHR())
			return null;

		delegate.changeConcessionTypeBy(concessionTypeIndex);
		if (checkout.getCheckoutBlock() != null)
			return checkout.getCheckoutBlock();
		return null;
	}
}
