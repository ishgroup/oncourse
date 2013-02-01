package ish.oncourse.enrol.checkout;

import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.StudentConcession;

import java.util.Date;
import java.util.List;

public class ActionAddConcession extends APurchaseAction {
	private StudentConcession studentConcession;
    private StudentConcession deletingConcession;

	@Override
	protected void makeAction() {
		studentConcession.getObjectContext().commitChangesToParent();
		getModel().addConcession(getModel().localizeObject(studentConcession));
        if (deletingConcession != null)
		    getModel().getObjectContext().deleteObject(getModel().localizeObject(deletingConcession));

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
			if (concession.getConcessionType() == concessionType)
			{
                Date expiredDate = concession.getExpiresOn();
                Date now = new Date();
                if (date != null && (expiredDate == null || expiredDate.before(now)))
                {
                    deletingConcession = concession;
                    return true;
                }
                getController().addError(PurchaseController.Message.concessionAlreadyAdded, studentConcession);
                return false;
			}
		}
		return true;
	}
}
