package ish.oncourse.enrol.components.checkout.concession;

import ish.oncourse.enrol.checkout.ConcessionDelegate;
import ish.oncourse.enrol.pages.Checkout;
import ish.oncourse.model.Student;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.util.FormatUtils;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.text.DateFormat;

public class ConcessionList {

	@Parameter(required = true)
	@Property
	private Student student;

	@Property
	private StudentConcession concession;

	@Property
	private Integer index;

	@Property
	private DateFormat dateFormat;

	@Inject
	private Request request;

	@Parameter(required = true)
	private ConcessionDelegate delegate;

	@InjectPage
	private Checkout checkout;




	@SetupRender
	void beforeRender() {
		dateFormat = FormatUtils.getDateFormat(student.getCollege().getTimeZone());
	}

	public Object onActionFromDeleteConcessionLink(Integer index) {
		if (!request.isXHR())
			return null;
		delegate.deleteConcessionBy(index);
		if (checkout.getCheckoutBlock() != null)
			return checkout.getCheckoutBlock();
		return null;
	}
}
