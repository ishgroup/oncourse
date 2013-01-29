package ish.oncourse.enrol.checkout;

import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Student;
import ish.oncourse.model.StudentConcession;

import java.util.List;

public interface ConcessionDelegate extends IDelegate{

	Contact getContact();

	Student getStudent();

	void deleteConcessionBy(Integer index);

	void cancelEditing();

	public List<ConcessionType> getConcessionTypes();

	/**
	 * @return editable student concession
	 */
	public StudentConcession getStudentConcession();

	public void changeConcessionTypeBy(Integer concessionTypeIndex);

	public boolean saveConcession();
}
