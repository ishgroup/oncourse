package ish.oncourse.enrol.components;

import ish.oncourse.enrol.pages.Checkout;
import ish.oncourse.enrol.utils.PurchaseController;
import ish.oncourse.enrol.utils.PurchaseModel;
import ish.oncourse.enrol.utils.PurchaseController.Action;
import ish.oncourse.enrol.utils.PurchaseController.ActionParameter;
import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.Student;
import ish.oncourse.model.StudentConcession;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;

public class CheckoutConcessionEntry {
	@SuppressWarnings("all")
	private static final Logger LOGGER = Logger.getLogger(CheckoutConcessionEntry.class);

    @InjectComponent
    @Property
    private Form concessionForm;

    @InjectComponent
    private Zone concessionZone;

    @InjectComponent
    private ConcessionEditor concessionEditor;

    @InjectPage
    private Checkout checkoutPage;

    @Property
    @Parameter
    private int index;

    @Property
    private StudentConcession studentConcessionItem;

    @SuppressWarnings("all")
    @Persist
    @Property
    private Format dateFormat;
    
    private PurchaseController getController() {
		return checkoutPage.getController();
	}
    
    private PurchaseModel getModel() {
		return getController().getModel();
	}

    @SetupRender
    void beforeRender() {
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
     * @see ish.oncourse.enrol.pages.Checkout#handleUnexpectedException(Throwable)
     */
	Object onException(Throwable cause) {
		return checkoutPage.handleUnexpectedException(cause);
	}

    @OnEvent(component = "concessionForm", value = "submit")
    Object submitConcession() {
        index = concessionEditor.getCurrentIndex();
        if (concessionEditor.isSavePressed() && !concessionForm.getHasErrors()) {
        	Student localStudent = getStudent();
            StudentConcession studentConcession = localStudent.getObjectContext().newObject(StudentConcession.class);
            studentConcession.setCollege(localStudent.getCollege());
            ConcessionType concessionType = concessionEditor.getConcessionType();
            studentConcession.setConcessionType(concessionType);
            studentConcession.setConcessionNumber(concessionEditor.getConcessionNumberValue());
            studentConcession.setExpiresOn(concessionEditor.getExpiryDateValue());
            studentConcession.setStudent(localStudent);
            localStudent.setModified(new Date());//this peace of code is just for sure that student will be enqueued on student concession create
            ActionParameter actionParameter = new ActionParameter(Action.ADD_CONCESSION);
            actionParameter.setValue(studentConcession);
            getController().performAction(actionParameter);
            if (getController().hasSuitableClasses(studentConcession)) {
                return checkoutPage;
            }
        }
        return concessionZone.getBody();
    }

    @OnEvent(component = "deleteConcession", value = "action")
    Object deleteConcession(Long id, Long concessionTypeId,  int currentIndex) {
        index = currentIndex;
        boolean shouldReload = false;
        Student student = getStudent();
        for (StudentConcession sc : student.getStudentConcessions()) {
        	if ((id != null && sc.getId().equals(id)) || (id == null && sc.getConcessionType().getId().equals(concessionTypeId))) {
        		shouldReload = getController().hasSuitableClasses(sc);
                student.removeFromStudentConcessions(sc);
                ActionParameter actionParameter = new ActionParameter(Action.REMOVE_CONCESSION);
                actionParameter.setValue(student.getContact());
                actionParameter.setValue(sc.getConcessionType());
                getController().performAction(actionParameter);
                break;
        	}
        }
        if (shouldReload) {
            return checkoutPage;
        }
        return concessionZone.getBody();
    }

    public Student getStudent() {
    	return getModel().getContacts().get(index).getStudent();
    }

    public Object[] getDeleteConcessionContext() {
    	//for not yet persisted concessions we may check the student concession only by type id
        return new Object[]{studentConcessionItem.getId(), 
        	studentConcessionItem.getId() == null ? studentConcessionItem.getConcessionType().getId() : null, index};
    }
}
