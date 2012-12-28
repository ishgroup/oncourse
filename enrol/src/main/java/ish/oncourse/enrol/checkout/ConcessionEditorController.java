package ish.oncourse.enrol.checkout;

import ish.oncourse.model.StudentConcession;

public class ConcessionEditorController extends AConcessionDelegate {
	@Override
	public void deleteConcessionBy(Integer index) {
		StudentConcession concession = getStudent().getStudentConcessions().get(index);
		getObjectContext().deleteObject(concession);
	}

	@Override
	public void cancelEditing() {
		studentConcession = null;
		setObjectContext(null);
		getPurchaseController().performAction(new PurchaseController.ActionParameter(PurchaseController.Action.cancelConcessionEditor));
	}

	@Override
	public void saveConcession() {
		PurchaseController.ActionParameter parameter = new PurchaseController.ActionParameter(PurchaseController.Action.addConcession);
		parameter.setErrors(getErrors());
		parameter.setValue(studentConcession);
		getPurchaseController().performAction(parameter);
	}
}
