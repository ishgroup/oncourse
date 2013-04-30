package ish.oncourse.enrol.components.checkout.concession;

import ish.oncourse.enrol.checkout.ConcessionDelegate;
import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.Student;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.selectutils.ListValueEncoder;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.services.Request;

import java.util.List;

public class ConcessionTypes {
	@Parameter(required = true)
	@Property
	private ConcessionDelegate delegate;

	@Parameter(required = false)
	private Block returnBlock;

	@Parameter(required = false)
	@Property
	private String returnBlockId;

	@Parameter(required = false)
	@Property
	private Form parentForm;

	@Property
	private ConcessionType concessionType;

	@Property
	private Integer concessionTypeIndex;

	@Inject
	private Request request;

	@Inject
	private PropertyAccess propertyAccess;

	private ListValueEncoder<ConcessionType> concessionTypeEncoder;



	public String getFormId()
	{
		return parentForm != null? parentForm.getClientId():"";
	}
	public Student getStudent() {
		return delegate.getStudent();
	}

	public StudentConcession getStudentConcession() {
		return delegate.getStudentConcession();
	}

	public ListValueEncoder<ConcessionType> getConcessionTypeEncoder() {
		if (concessionTypeEncoder == null) {
			concessionTypeEncoder = new ListValueEncoder<>(getConcessionTypes(), ConcessionType.ANGEL_ID_PROPERTY, propertyAccess);
		}
		return concessionTypeEncoder;
	}

	public List<ConcessionType> getConcessionTypes() {
		return getStudent().getCollege().getActiveConcessionTypes();
	}

	public boolean isSelectedConcessionType() {
		return delegate.getStudentConcession().getConcessionType() != null && delegate.getStudentConcession().getConcessionType().getId().equals(concessionType.getId());
	}

	@OnEvent(value = "changeConcessionTypeEvent")
	public Object changeConcessionType(Integer concessionTypeIndex) {
		if (!request.isXHR())
			return null;

		delegate.changeConcessionTypeBy(concessionTypeIndex);
		if (returnBlock != null)
			return returnBlock;
		return null;
	}

}
