package ish.oncourse.enrol.checkout;

import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Student;
import ish.oncourse.model.StudentConcession;
import org.apache.cayenne.ObjectContext;

import java.util.List;

public abstract class AConcessionDelegate extends ADelegate implements ConcessionDelegate {
	protected StudentConcession studentConcession;
	private ObjectContext objectContext;

	private Contact contact;

	public ObjectContext getObjectContext() {
		return objectContext;
	}

	public void setObjectContext(ObjectContext objectContext) {
		this.objectContext = objectContext;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	@Override
	public Contact getContact() {
		return contact;
	}

	@Override
	public Student getStudent() {
		return contact.getStudent();
	}


	@Override
	public StudentConcession getStudentConcession() {
		return studentConcession;
	}

	@Override
	public List<ConcessionType> getConcessionTypes() {
		return getStudent().getCollege().getActiveConcessionTypes();
	}

	@Override
	public void changeConcessionTypeBy(Integer concessionTypeIndex) {
		if (concessionTypeIndex == -1)
		{
			if (studentConcession != null)
			{
				studentConcession = null;
			}
		}
		else
		{
			if (studentConcession == null)
			{
				studentConcession = objectContext.newObject(StudentConcession.class);
				studentConcession.setStudent(getStudent());
			}
			ConcessionType concessionType = getConcessionTypes().get(concessionTypeIndex);
			studentConcession.setConcessionType((ConcessionType) objectContext.localObject(concessionType.getObjectId(),concessionType));
		}
	}

	@Override
	public void deleteConcessionBy(Integer index) {
	}

	@Override
	public void cancelEditing() {
	}

	@Override
	public void saveConcession() {
	}
}
