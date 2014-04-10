package ish.oncourse.enrol.checkout;

import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Student;
import ish.oncourse.model.StudentConcession;
import org.apache.cayenne.ObjectContext;

import java.util.List;

public abstract class AConcessionDelegate extends ADelegate implements ConcessionDelegate {
	private StudentConcession studentConcession;
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
		if(studentConcession == null)
		{
			studentConcession = objectContext.newObject(StudentConcession.class);
			studentConcession.setStudent(getStudent());
			studentConcession.setCollege(contact.getCollege());
		}
		return studentConcession;
	}

	protected void setStudentConcession(StudentConcession studentConcession)
	{
		this.studentConcession = studentConcession;
	}

	@Override
	public List<ConcessionType> getConcessionTypes() {
		return getStudent().getCollege().getActiveConcessionTypes();
	}

	@Override
	public void changeConcessionTypeBy(Integer concessionTypeIndex) {
		if (concessionTypeIndex == -1)
		{
			getObjectContext().deleteObject(studentConcession);
			this.studentConcession = null;
		}
		else
		{
			ConcessionType concessionType = getConcessionTypes().get(concessionTypeIndex);
			getStudentConcession().setConcessionType((ConcessionType) objectContext.localObject(concessionType.getObjectId(),concessionType));
		}
	}

	@Override
	public void deleteConcessionBy(Integer index) {
	}

	@Override
	public void cancelEditing() {
		getObjectContext().deleteObject(getStudentConcession());
	}

	@Override
	public boolean saveConcession() {
        return false;
	}
}
