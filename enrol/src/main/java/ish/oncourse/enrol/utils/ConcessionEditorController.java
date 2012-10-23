package ish.oncourse.enrol.utils;

import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Student;
import ish.oncourse.model.StudentConcession;
import org.apache.cayenne.ObjectContext;

import java.util.Date;
import java.util.List;

public class ConcessionEditorController implements ConcessionDelegate {

	private ObjectContext objectContext;

	private PurchaseController purchaseController;
	private Contact contact;

	private StudentConcession studentConcession;

	@Override
	public Contact getContact() {
		return contact;
	}

	@Override
	public Student getStudent() {
		return contact.getStudent();
	}

	@Override
	public void deleteConcessionBy(Integer index) {
		StudentConcession concession = getStudent().getStudentConcessions().get(index);
		objectContext.deleteObject(concession);
	}

	@Override
	public void cancelEditing(Long contactId) {
		studentConcession = null;
		objectContext = null;
		purchaseController.performAction(new PurchaseController.ActionParameter(PurchaseController.Action.CANCEL_CONCESSION_EDITOR));
	}

	@Override
	public void saveConcession(Long contactId) {
		studentConcession.setStudent(getStudent());
		objectContext.commitChangesToParent();
		studentConcession = null;
	}

	@Override
	public List<ConcessionType> getConcessionTypes() {
		return getStudent().getCollege().getActiveConcessionTypes();
	}

	@Override
	public StudentConcession getStudentConcession() {
		return studentConcession;
	}

	@Override
	public void changeConcessionTypeBy(Integer concessionTypeIndex) {
		if (concessionTypeIndex == -1)
		{
			if (studentConcession != null)
			{
				objectContext.deleteObject(studentConcession);
				studentConcession = null;
			}
		}
		else
		{
			if (studentConcession == null)
			{
				studentConcession = objectContext.newObject(StudentConcession.class);
			}
			ConcessionType concessionType = getConcessionTypes().get(concessionTypeIndex);
			studentConcession.setConcessionType((ConcessionType) objectContext.localObject(concessionType.getObjectId(),concessionType));
		}
	}

	@Override
	public void fieldsChanged(String number, Date expiry) {
		ConcessionType type = studentConcession.getConcessionType();
		if (type.getHasConcessionNumber())
			studentConcession.setConcessionNumber(number);
		if (type.getHasExpiryDate())
		{
			studentConcession.setExpiresOn(expiry);
		}
	}

	public void setPurchaseController(PurchaseController purchaseController) {
		this.purchaseController = purchaseController;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public ObjectContext getObjectContext() {
		return objectContext;
	}

	public void setObjectContext(ObjectContext objectContext) {
		this.objectContext = objectContext;
	}
}
