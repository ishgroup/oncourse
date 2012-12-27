package ish.oncourse.enrol.checkout;

import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.StudentConcession;

import java.util.Date;
import java.util.List;

public class ActionAddConcession extends APurchaseAction {
	private StudentConcession studentConcession;

	@Override
	protected void makeAction() {
		studentConcession.getObjectContext().commitChangesToParent();
		getModel().addConcession(getModel().localizeObject(studentConcession));
		getController().recalculateEnrolmentInvoiceLines();
		getController().setConcessionEditorController(null);
		getController().setState(PurchaseController.State.editCheckout);
	}

	@Override
	protected void parse() {
		if (getParameter() != null) {
			studentConcession = getParameter().getValue(StudentConcession.class);
		}
	}

	@Override
	protected boolean validate() {
		String number = studentConcession.getConcessionNumber();
		ConcessionType concessionType = studentConcession.getConcessionType();
		Date date = studentConcession.getExpiresOn();

		List<StudentConcession> studentConcessions = studentConcession.getStudent().getStudentConcessions();
		for (StudentConcession concession : studentConcessions) {
			if (concession == studentConcession)
				continue;

			if (concession.getConcessionType() == concessionType &&
					isSameNumber(number, concession))
			{
				getController().addError(PurchaseController.Message.concessionAlreadyAdded, concession);
				return false;
			}
		}

		return true;
	}

	private boolean isSameNumber(String number, StudentConcession concession)
	{
		return (number == null && concession.getConcessionNumber() == null) ||
				(number != null && number.equals(concession.getConcessionNumber()));
	}

	private boolean isSameExpiresOn(Date expiresOn, StudentConcession concession)
	{
		return (expiresOn == null && concession.getExpiresOn() == null) ||
				(expiresOn != null && expiresOn.equals(concession.getExpiresOn()));
	}

}
