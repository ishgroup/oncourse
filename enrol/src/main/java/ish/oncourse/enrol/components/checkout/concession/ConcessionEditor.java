package ish.oncourse.enrol.components.checkout.concession;

import ish.oncourse.enrol.checkout.ConcessionDelegate;
import ish.oncourse.enrol.checkout.ConcessionParser;
import ish.oncourse.enrol.pages.Checkout;
import ish.oncourse.model.Student;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.util.ValidateHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.Collections;

public class ConcessionEditor {
	private static final Logger logger = LogManager.getLogger();

	@Parameter(required = true)
	private ConcessionDelegate delegate;

	@InjectPage
	@Property
	private Checkout checkout;

	@Inject
	private Request request;

	@Inject
	private Messages messages;

	@Property
	private ValidateHandler validateHandler = new ValidateHandler();


	@SetupRender
	void beforeRender() {
	}

	public ConcessionDelegate getDelegate()
	{
		return delegate;
	}

	public Student getStudent()
	{
		return delegate.getStudent();
	}

	public StudentConcession getStudentConcession()
	{
		return delegate.getStudentConcession();
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
			ConcessionParser parser = ConcessionParser.newInstance(request,delegate.getStudentConcession(),
					delegate.getStudent().getCollege().getTimeZone());
			parser.parse();
			delegate.setErrors(parser.getErrors());
			validateHandler.setErrors(parser.getErrors());
            //we reset all fields when validation is failed.
            boolean result = delegate.saveConcession();
			if (parser.getErrors().isEmpty() && !result)
                delegate.changeConcessionTypeBy(-1);
		}
		if (checkout.getCheckoutBlock() != null)
			return checkout.getCheckoutBlock();
		return null;
	}
}
