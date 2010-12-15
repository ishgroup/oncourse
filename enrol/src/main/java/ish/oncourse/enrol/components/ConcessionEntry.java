package ish.oncourse.enrol.components;

import ish.oncourse.enrol.pages.EnrolCourses;
import ish.oncourse.model.Student;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.model.services.persistence.ICayenneService;
import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

public class ConcessionEntry {

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private ComponentResources componentResources;

	@InjectComponent
	@Property
	private Form concessionForm;

	@InjectComponent
	private Zone concessionZone;

	@InjectComponent
	private ConcessionEditor concessionEditor;

	@Property
	@Parameter
	private int index;

	@Persist
	private EnrolCourses enrolCoursesPage;

	@Property
	private StudentConcession studentConcessionItem;

	@Persist
	private ObjectContext context;

	@SetupRender
	void beforeRender() {
		enrolCoursesPage = (EnrolCourses) componentResources.getPage();
		context = cayenneService.newContext();
	}

	@OnEvent(component = "concessionForm", value = "submit")
	Object submitConcession() {
		if (concessionEditor.isSavePressed()) {
			StudentConcession studentConcession = concessionEditor.getStudentConcession();
			Student student = enrolCoursesPage.getContacts().get(index).getStudent();
			studentConcession.setStudent((Student) studentConcession.getObjectContext()
					.localObject(student.getObjectId(), student));
			studentConcession.getObjectContext().commitChanges();
		}
		return concessionZone.getBody();
	}

	@OnEvent(component = "deleteConcession", value = "action")
	Object deleteConcession(Long id) {
		for (StudentConcession sc : getStudent().getStudentConcessions()) {
			if (sc.getId().equals(id)) {
				getStudent().removeFromStudentConcessions(sc);
				context.deleteObject(sc);
				context.commitChanges();
				break;
			}
		}
		return concessionZone.getBody();
	}

	public Student getStudent() {
		Student student = enrolCoursesPage.getContacts().get(index).getStudent();
		return (Student) context.localObject(student.getObjectId(), student);

	}

	public ObjectContext getEditorContext() {
		return cayenneService.newContext();
	}
}
