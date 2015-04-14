package ish.oncourse.enrol.components.checkout.concession;

import ish.oncourse.enrol.checkout.ConcessionDelegate;
import ish.oncourse.enrol.services.Constants;
import ish.oncourse.model.Student;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.util.FormatUtils;
import ish.oncourse.util.ValidateHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

import java.text.DateFormat;
import java.text.ParseException;

public class ConcessionFieldSet {

	private static final Logger logger = LogManager.getLogger();
	@Parameter(required = true)
	@Property
	private ConcessionDelegate delegate;

	@Parameter(required = true)
	private ValidateHandler validateHandler;

	private DateFormat dateFormat;

	@Property
	private boolean concessionAgree;


	@SetupRender
	void setupRender() {

	}

	public ValidateHandler getValidateHandler()
	{
		if (validateHandler == null)
		{
			validateHandler = new ValidateHandler();
		}
		return validateHandler;
	}

	public DateFormat getDateFormat()
	{
		if (dateFormat == null)
		{
			dateFormat = FormatUtils.getDateFormat(Constants.DATE_FIELD_PARSE_FORMAT,getStudent().getCollege().getTimeZone());
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
            DateFormat format = FormatUtils.getDateFormat(Constants.DATE_FIELD_PARSE_FORMAT,getStudent().getCollege().getTimeZone());
			getStudentConcession().setExpiresOn(value != null ? format.parse(value): null);
		} catch (ParseException e) {
			logger.warn(e);
		}
	}

}
