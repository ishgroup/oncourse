package ish.oncourse.enrol.components;

import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.Student;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.selectutils.ListSelectModel;
import ish.oncourse.selectutils.ListValueEncoder;

import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.ValidationTracker;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.DateField;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Hidden;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;

public class ConcessionEditor {

	@Inject
	private PropertyAccess propertyAccess;

    @Inject
	private Messages messages;

	@Property
	@Persist
	private ListSelectModel<ConcessionType> activeConcessionTypesModel;

	@Property
	@Persist
	private ListValueEncoder<ConcessionType> concessionTypesEncoder;

	@Parameter
	@Property
	private Student student;

	@Parameter
	@Property
	private Form parentForm;

	private ConcessionType concessionType;

	@Persist
	private StudentConcession studentConcession;

	@Property
	private boolean hasCertifiedConcession;

	@Property
	@Parameter
	private boolean showSaveButton;

	private boolean savePressed;

	@InjectComponent
	@Property
	private Hidden savePressedHiddenField;

	@Parameter
	private ObjectContext context;

    @Property
    private String concessionNumberErrorMessage;

    @Property
    private String expiryDateErrorMessage;

    @InjectComponent
    private TextField concessionNumber;

    @InjectComponent
    private DateField expiryDate;

	@SetupRender
	void beginRender() {
		if (student != null) {
			List<ConcessionType> activeConcessionTypes = student.getCollege()
					.getActiveConcessionTypes();
			activeConcessionTypesModel = new ListSelectModel<ConcessionType>(activeConcessionTypes,
					ConcessionType.NAME_PROPERTY, propertyAccess);
			concessionTypesEncoder = new ListValueEncoder<ConcessionType>(activeConcessionTypes,
					ConcessionType.ANGEL_ID_PROPERTY, propertyAccess);
			studentConcession = context.newObject(StudentConcession.class);
		}
	}

	@OnEvent(component = "saveConcession", value = "selected")
	void saveButtonPressed() {
		savePressed = true;
	}

	public String getConcessionNumberInputClass() {
		return getInputSectionClass(concessionNumber);
	}

    public String getExpiryDateInputClass() {
       return getInputSectionClass(expiryDate);
    }
	private String getInputSectionClass(Field field) {
		ValidationTracker defaultTracker = parentForm.getDefaultTracker();
		return defaultTracker == null || !defaultTracker.inError(field) ? messages
				.get("validInput") : messages.get("validateInput");
	}
	public boolean isSavePressed() {
		return savePressed;
	}

    /**
     * indicates that submit of form was performed because of concessionType refresh
     * @return
     */
    public boolean isConcessionTypeRefreshed(){
        return !isSavePressed();
    }

	public void setSavePressed(boolean savePressed) {
		this.savePressed = savePressed;
	}

	public StudentConcession getStudentConcession() {
		return studentConcession;
	}

	public void setStudentConcession(StudentConcession studentConcession) {
		this.studentConcession = studentConcession;
	}

	public ConcessionType getConcessionType() {
		return concessionType;
	}

	public void setConcessionType(ConcessionType concessionType) {
		this.concessionType = concessionType;
		studentConcession.setConcessionType((ConcessionType) studentConcession.getObjectContext()
				.localObject(concessionType.getObjectId(), concessionType));
	}


    public void validateConcession(){
        concessionNumberErrorMessage=studentConcession.validateConcessionNumber();
        if(concessionNumberErrorMessage!=null){
            parentForm.recordError(concessionNumber, concessionNumberErrorMessage);
        }
        expiryDateErrorMessage =studentConcession.validateExpiresDate();
        if(expiryDateErrorMessage !=null){
            parentForm.recordError(expiryDate, expiryDateErrorMessage);
        }
        if(!hasCertifiedConcession){
            parentForm.recordError(messages.get("certificationRequiredMessage"));
        }
    }
}
