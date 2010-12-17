package ish.oncourse.enrol.components;

import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.Student;
import ish.oncourse.selectutils.ListSelectModel;
import ish.oncourse.selectutils.ListValueEncoder;

import java.util.Date;
import java.util.List;

import org.apache.tapestry5.Field;
import org.apache.tapestry5.ValidationTracker;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.*;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;

public class ConcessionEditor {

    @Property
    private static final String SEPARATOR = "_";

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

    @Property
    private boolean hasCertifiedConcession;

    private String concessionNumberValue;

    private Date expiryDateValue;

    @Property
    @Parameter
    private boolean showSaveButton;

    private boolean savePressed;

    @InjectComponent
    @Property
    private Hidden savePressedHiddenField;

    @Property
    private String concessionNumberErrorMessage;

    @Property
    private String expiryDateErrorMessage;

    @InjectComponent
    private TextField concessionNumber;

    @InjectComponent
    private DateField expiryDate;

    @InjectComponent
    private Select concessionTypeSelect;

    @Property
    @Parameter
    private int index;

    @SetupRender
    void beginRender() {
        if (student != null) {
            List<ConcessionType> activeConcessionTypes = student.getCollege()
                    .getActiveConcessionTypes();
            activeConcessionTypesModel = new ListSelectModel<ConcessionType>(activeConcessionTypes,
                    ConcessionType.NAME_PROPERTY, propertyAccess);
            concessionTypesEncoder = new ListValueEncoder<ConcessionType>(activeConcessionTypes,
                    ConcessionType.ANGEL_ID_PROPERTY, propertyAccess);
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
     *
     * @return
     */
    public boolean isConcessionTypeRefreshed() {
        return !isSavePressed();
    }

    public void setSavePressed(boolean savePressed) {
        this.savePressed = savePressed;
    }


    public void validateConcession() {
        concessionNumberErrorMessage = validateConcessionNumber();
        if (concessionNumberErrorMessage != null) {
            parentForm.recordError(concessionNumber, concessionNumberErrorMessage);
        }
        expiryDateErrorMessage = validateExpiresDate();
        if (expiryDateErrorMessage != null) {
            parentForm.recordError(expiryDate, expiryDateErrorMessage);
        }
        if (!hasCertifiedConcession) {
            parentForm.recordError(messages.get("certificationRequiredMessage"));
        }
    }

    /**
     * Return the index of currently editing component.
     * <p/>
     * The concessionTypeSelect contains "name = concessionType_${index}" that contains this index, while the index parameter is
     * changed in loop iterations
     *
     * @return
     */
    public int getCurrentIndex() {
        return Integer.parseInt(concessionTypeSelect.getControlName().split(SEPARATOR)[1]);
    }

    /**
     * If this concession's type has a concession number, will only validate if
     * the concession number is not empty
     *
     * @return error message
     */
    public String validateConcessionNumber() {

        if (concessionType != null && concessionType.getHasConcessionNumber()) {
            if (concessionNumberValue == null || concessionNumberValue.length() == 0) {
                return String.format("A %s concession requires a card number.", concessionType
                        .getName());
            }
        }
        return null;
    }

    /**
     * If this concession's type has an expiry date, will only validate if the
     * expiry date is not empty
     *
     * @return error message
     */
    public String validateExpiresDate() {
        if (concessionType != null && concessionType.getHasExpiryDate()) {
            if (expiryDateValue == null) {
                return String.format("A %s concession requires an expiry date.",
                        concessionType.getName());
            }
            if (new Date().compareTo(expiryDateValue) > 0) {
                return "Expiry date shouldn't be at the past.";
            }
        }
        return null;
    }

    public void clearEditor(){
        setConcessionType(null);
        setConcessionNumberValue(null);
        setExpiryDateValue(null);
        hasCertifiedConcession=false;
    }

    public void setConcessionNumberValue(String concessionNumberValue) {
        if (concessionNumberValue != null) {
            concessionNumberValue = concessionNumberValue.trim();
        }
        this.concessionNumberValue = concessionNumberValue;
    }

    public String getConcessionNumberValue() {
        return concessionNumberValue;
    }


    public Date getExpiryDateValue() {
        return expiryDateValue;
    }

    public void setExpiryDateValue(Date expiryDateValue) {
        this.expiryDateValue = expiryDateValue;
    }


    public ConcessionType getConcessionType() {
        return concessionType;
    }

    public void setConcessionType(ConcessionType concessionType) {
        this.concessionType = concessionType;
    }
}
