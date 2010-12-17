package ish.oncourse.enrol.components;

import ish.oncourse.enrol.pages.EnrolCourses;
import ish.oncourse.model.College;
import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.Student;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.model.services.persistence.ICayenneService;
import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.text.Format;
import java.text.SimpleDateFormat;

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

    @Property
    private StudentConcession studentConcessionItem;

    @Persist
    private ObjectContext context;

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

    @OnEvent(component = "concessionForm", value = "submit")
    Object submitConcession() {
        index = concessionEditor.getCurrentIndex();
        if (concessionEditor.isSavePressed() && !concessionForm.getHasErrors()) {

            Student student = getCurrentStudent();
            ObjectContext newContext = cayenneService.newContext();
            StudentConcession studentConcession = newContext.newObject(StudentConcession.class);
            College college = student.getCollege();
            studentConcession.setCollege((College) newContext
                    .localObject(college.getObjectId(), college));
            ConcessionType concessionType = concessionEditor.getConcessionType();
            studentConcession.setConcessionType((ConcessionType) newContext
                    .localObject(concessionType.getObjectId(), concessionType));
            studentConcession.setConcessionNumber(concessionEditor.getConcessionNumberValue());
            studentConcession.setExpiresOn(concessionEditor.getExpiryDateValue());
            studentConcession.setStudent((Student) newContext
                    .localObject(student.getObjectId(), student));
            newContext.commitChanges();
            concessionEditor.clearEditor();
        }
        return concessionZone.getBody();
    }

    @OnEvent(component = "deleteConcession", value = "action")
    Object deleteConcession(Long id, int currentIndex) {
        index = currentIndex;
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
