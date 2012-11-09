package ish.oncourse.enrol.components.checkout.concession;

import ish.oncourse.enrol.checkout.ConcessionDelegate;
import ish.oncourse.enrol.checkout.ConcessionParser;
import ish.oncourse.enrol.checkout.ValidateHandler;
import ish.oncourse.enrol.pages.Checkout;
import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.Student;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.util.FormatUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.text.DateFormat;
import java.util.Collections;
import java.util.List;

import static ish.oncourse.enrol.pages.Checkout.DATE_FIELD_FORMAT;

public class ConcessionEditor {
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

	@Property
	private ValidateHandler validateHandler = new ValidateHandler();


	@SetupRender
	void beforeRender() {
		dateFormat = FormatUtils.getDateFormat(DATE_FIELD_FORMAT,getStudent().getCollege().getTimeZone());
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

	/**
	 * It is workaround to exclude exception when studentConcession.expiresOn is null
	 */
	public String getExpiresOn()
	{
		if (getStudentConcession().getExpiresOn() == null)
			return FormatUtils.EMPTY_STRING;
		else
			return dateFormat.format(getStudentConcession().getExpiresOn());
	}

	@OnEvent(value = "cancelConcessionEvent")
	public Object cancelConcession() {
		if (!request.isXHR())
			return null;

		validateHandler.setErrors(Collections.EMPTY_MAP);

		if (delegate != null)
			delegate.cancelEditing();
		if (checkout.getCheckoutBlock() != null)
			return checkout.getCheckoutBlock();
		return null;
	}

	@OnEvent(value = "saveConcessionEvent")
	public Object saveConcession() {
		if (!request.isXHR())
			return null;
		if (delegate != null)
		{
			ConcessionParser parser = createParser();
			parser.parse();
			delegate.setErrors(parser.getErrors());
			validateHandler.setErrors(parser.getErrors());

			delegate.saveConcession();
		}
		if (checkout.getCheckoutBlock() != null)
			return checkout.getCheckoutBlock();
		return null;
	}

	private ConcessionParser createParser() {
		ConcessionParser parser = new ConcessionParser();
		parser.setStudentConcession(delegate.getStudentConcession());
		parser.setMessages(messages);
		parser.setRequest(request);
		parser.setDateFormat(FormatUtils.getDateFormat(DATE_FIELD_FORMAT,getStudent().getCollege().getTimeZone()));
		return parser;
	}

	public boolean isSelectedConcessionType()
	{
		return delegate.getStudentConcession() != null && delegate.getStudentConcession().getConcessionType().getId().equals(concessionType.getId());
	}

	@OnEvent(value = "changeConcessionTypeEvent")
	public Object changeConcessionType(Integer concessionTypeIndex)
	{
		if (!request.isXHR())
			return null;

		delegate.changeConcessionTypeBy(concessionTypeIndex);
		if (checkout.getCheckoutBlock() != null)
			return checkout.getCheckoutBlock();
		return null;
	}
}
