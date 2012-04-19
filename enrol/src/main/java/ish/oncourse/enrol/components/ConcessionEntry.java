package ish.oncourse.enrol.components;

import ish.oncourse.enrol.pages.EnrolCourses;
import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.Student;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.services.persistence.ICayenneService;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.cayenne.ObjectContext;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

public class ConcessionEntry {
	@SuppressWarnings("all")
	private static final Logger LOGGER = Logger.getLogger(ConcessionEntry.class);

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

    @InjectPage
    private EnrolCourses enrolCourses;

    @Property
    @Parameter
    private int index;

    @Property
    private StudentConcession studentConcessionItem;

    @Persist
    private ObjectContext context;

    @SuppressWarnings("all")
    @Persist
    @Property
    private Format dateFormat;

    @SetupRender
    void beforeRender() {
        context = cayenneService.newContext();
        dateFormat = new SimpleDateFormat("EEE dd MMM yyyy");
    }

    @OnEvent(component = "concessionForm", value = "validate")
    void validateNewConcession() {
        if (concessionEditor.isSavePressed()) {
            concessionEditor.validateConcession();
        } else {
            concessionForm.clearErrors();
        }
    }
    
    /**
     * @see ish.oncourse.enrol.pages.EnrolCourses#isPersistCleared()
     */
	Object onException(Throwable cause) {
		return enrolCourses.handleUnexpectedException(cause);
	}

    @OnEvent(component = "concessionForm", value = "submit")
    Object submitConcession() {
        index = concessionEditor.getCurrentIndex();
        if (concessionEditor.isSavePressed() && !concessionForm.getHasErrors()) {

            Student student = getCurrentStudent();
            Student localStudent = (Student) cayenneService.newContext().localObject(student.getObjectId(), student);
            StudentConcession studentConcession = localStudent.getObjectContext().newObject(StudentConcession.class);
            studentConcession.setCollege(localStudent.getCollege());
            ConcessionType concessionType = concessionEditor.getConcessionType();
            studentConcession.setConcessionType((ConcessionType) localStudent.getObjectContext().localObject(concessionType.getObjectId(), concessionType));
            studentConcession.setConcessionNumber(concessionEditor.getConcessionNumberValue());
            studentConcession.setExpiresOn(concessionEditor.getExpiryDateValue());
            studentConcession.setStudent(localStudent);
            localStudent.setModified(new Date());//this peace of code is just for sure that student will be enqueued on student concession create
            localStudent.getObjectContext().commitChanges();
            if (enrolCourses.hasSuitableClasses(studentConcession)) {
                return enrolCourses;
            }
        }
        return concessionZone.getBody();
    }

    @OnEvent(component = "deleteConcession", value = "action")
    Object deleteConcession(Long id, int currentIndex) {
        index = currentIndex;
        boolean shouldReload = false;
        for (StudentConcession sc : getStudent().getStudentConcessions()) {
            if (sc.getId().equals(id)) {
                shouldReload = enrolCourses.hasSuitableClasses(sc);
                getStudent().removeFromStudentConcessions(sc);
                context.deleteObject(sc);
                context.commitChanges();
                break;
            }
        }
        if (shouldReload) {
            return enrolCourses;
        }
        return concessionZone.getBody();
    }

    public Student getStudent() {
        Student student = getCurrentStudent();
        return (Student) context.localObject(student.getObjectId(), student);

    }

    private Student getCurrentStudent() {
        return ((EnrolCourses) componentResources.getPage()).getContacts().get(index).getStudent();
    }

    public Object[] getDeleteConcessionContext() {
        return new Object[]{studentConcessionItem.getId(), index};
    }
}
