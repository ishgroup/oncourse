package ish.oncourse.enrol.components.checkout.concession;

import ish.oncourse.enrol.checkout.ConcessionDelegate;
import ish.oncourse.enrol.checkout.ValidateHandler;
import ish.oncourse.enrol.pages.Checkout;
import ish.oncourse.model.Student;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.util.FormatUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

import java.text.DateFormat;
import java.text.ParseException;

public class ConcessionFieldSet {

	private static final Logger LOGGER = Logger.getLogger(ConcessionEditor.class);
	@Parameter(required = true)
	@Property
	private ConcessionDelegate delegate;

	@Parameter(required = true)
	@Property
	private ValidateHandler validateHandler;

	private DateFormat dateFormat;

	@Property
	private boolean concessionAgree;

	@SetupRender
	void setupRender() {
	}

	public DateFormat getDateFormat()
	{
		if (dateFormat == null)
		{
			dateFormat = FormatUtils.getDateFormat(Checkout.DATE_FIELD_FORMAT,getStudent().getCollege().getTimeZone());
		}
		return dateFormat;
	}

	public Student getStudent() {
		return delegate.getStudent();
	}

	public StudentConcession getStudentConcession()
	{
		return delegate.getStudentConcession();
	}

	/**
	 * It is workaround to exclude exception when studentConcession.expiresOn is null
	 */
	public String getExpiresOn()
	{
		if (getStudentConcession().getExpiresOn() == null)
			return FormatUtils.EMPTY_STRING;
		else
			return getDateFormat().format(getStudentConcession().getExpiresOn());
	}

	public void setExpiresOn(String value)
	{
		try {
			getStudentConcession().setExpiresOn(value != null ? getDateFormat().parse(value): null);
		} catch (ParseException e) {
			LOGGER.warn(e);
		}
	}

}
