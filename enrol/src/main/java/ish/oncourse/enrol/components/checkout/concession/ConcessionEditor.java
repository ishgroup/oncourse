package ish.oncourse.enrol.components.checkout.concession;

import ish.oncourse.enrol.pages.Checkout;
import ish.oncourse.enrol.utils.ConcessionDelegate;
import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.Student;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.util.FormatUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class ConcessionEditor {
	private static final Logger LOGGER = Logger.getLogger(ConcessionEditor.class);

	@Parameter(required = true)
	private ConcessionDelegate delegate;

	@InjectPage
	private Checkout checkout;

	@Inject
	private Request request;

	@Property
	private ConcessionType concessionType;

	@Property
	private Integer concessionTypeIndex;


	@Property
	private Date expiryDateValue;

	@Property
	private DateFormat dateFormat;

	@SetupRender
	void beforeRender() {
		dateFormat = FormatUtils.getDateFormat(getStudent().getCollege().getTimeZone());
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
			delegate.saveConcession(contactId);
		if (checkout.getCheckoutBlock() != null)
			return checkout.getCheckoutBlock();
		return null;
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
