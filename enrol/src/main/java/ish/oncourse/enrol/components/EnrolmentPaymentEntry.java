package ish.oncourse.enrol.components;

import ish.common.types.CreditCardType;
import ish.math.Money;
import ish.oncourse.enrol.pages.EnrolCourses;
import ish.oncourse.enrol.utils.EnrolCoursesController;
import ish.oncourse.enrol.utils.EnrolCoursesModel;
import ish.oncourse.enrol.utils.EnrolmentValidationUtil;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Enrolment;
import ish.oncourse.selectutils.ISHEnumSelectModel;
import ish.oncourse.selectutils.ListSelectModel;
import ish.oncourse.selectutils.ListValueEncoder;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.util.FormatUtils;
import ish.persistence.CommonPreferenceController;
import org.apache.log4j.Logger;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.ValidationTracker;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Select;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class EnrolmentPaymentEntry {
	public static final Logger LOGGER = Logger.getLogger(EnrolmentPaymentEntry.class);
	/**
	 * Credit card expire date interval
	 */
	private static final int EXPIRE_YEAR_INTERVAL = 15;
	
	private static final String MESSAGE_NO_PLACES = "noPlaces";

	/**
	 * tapestry services
	 */
	@Inject
	private PropertyAccess propertyAccess;

	@Inject
	private Messages messages;

	/**
	 * ish services
	 */
	@Inject
	private PreferenceController preferenceService;

	/**
	 * Parameters
	 */
	/**
	 * Auxiliary properties
	 */

	@SuppressWarnings("all")
	@Property
	@Persist
	private ListSelectModel<Contact> payersModel;

	@SuppressWarnings("all")
	@Property
	@Persist
	private ListValueEncoder<Contact> payersEncoder;

	@SuppressWarnings("all")
	@Property
	@Persist
	private ISHEnumSelectModel cardTypeModel;

	@Property
	private String cardNumberErrorMessage;

	@Property
	private String ccExpiryMonth;

	@Property
	private Integer ccExpiryYear;

	@Property
	@Persist
	private List<Integer> years;

	@Property
	@Persist
	private boolean userAgreed;

	@Property
	@Persist
	private String enrolmentDisclosure;

	/**
	 * Components
	 */
	@InjectComponent
	private Zone paymentZone;

	@InjectComponent
	@Property
	private Form paymentDetailsForm;

	@InjectComponent
	private Select cardTypeSelect;

	@InjectComponent
	private TextField cardName;

	@InjectComponent
	private TextField cardNumber;

	@InjectComponent
	private TextField cardcvv;

	@InjectPage
	private EnrolCourses enrolCourses;

	//@Parameter
	//private List<Enrolment> enrolments;

	@SuppressWarnings("all")
	private final ReentrantLock lock = new ReentrantLock();

	/**
	 * Indicates if the submit was because of pressing the submit button.
	 */
	private boolean isSubmitted;
	
	/**
	 * @return the controller
	 */
	public EnrolCoursesController getController() {
		return enrolCourses.getController();
	}
	
	public EnrolCoursesModel getModel() {
		return getController().getModel();
	}

	@SetupRender
	void beforeRender() {
		//initial fill the data
		getController().getEnrolmentsList();
		getController().getTotalIncGst();
		
		initYears();
		initEnrolmentDisclosure();

		initPayers();

		payersModel = new ListSelectModel<Contact>(getModel().getContacts(), "fullName", propertyAccess);

		payersEncoder = new ListValueEncoder<Contact>(getModel().getContacts(), "id", propertyAccess);

		cardTypeModel = new ISHEnumSelectModel(CreditCardType.class, messages, CommonPreferenceController
				.getCCAvailableTypes(preferenceService).values().toArray(new CreditCardType[] {}));

		userAgreed = false;
	}

	private void initYears() {
		years = new ArrayList<Integer>();
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);

		for (int i = 0; i < EXPIRE_YEAR_INTERVAL; i++) {
			years.add(currentYear + i);
		}
	}

	private void initEnrolmentDisclosure() {

		enrolmentDisclosure = preferenceService.getFeatureEnrolmentDisclosure();

		if ("".equals(enrolmentDisclosure)) {
			enrolmentDisclosure = null;
		}
	}

	private void initPayers() {
		List<Contact> localPayers = new ArrayList<Contact>(getModel().getContacts().size());
		for (Contact payer : getModel().getContacts()) {
			localPayers.add((Contact) getModel().getPayment().getObjectContext().localObject(payer.getObjectId(), payer));
		}
		getModel().setContacts(localPayers);
		getModel().getPayment().setContact(getModel().getContacts().get(0));
	}

	public boolean isZeroPayment() {
		return getModel().getTotalIncGst().isZero();
	}

	public boolean isHasConcessionsCollege() {
		return preferenceService.getFeatureConcessionsInEnrolment();
	}

	public boolean isHasConcessionsEnrolment() {
		for (Enrolment enrolment : getModel().getEnrolmentsList()) {
			if (!enrolment.getCourseClass().getConcessionDiscounts().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public boolean isHasStudentWithoutConcessions() {
		for (Enrolment enrolment : getModel().getEnrolmentsList()) {
			if (enrolment.getStudent().getStudentConcessions().isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public String getSubmitButtonText() {
		return isZeroPayment() ? messages.get("submit.button.text.enrol") : messages.get("submit.button.text.payment");
	}

	public String getCardTypeClass() {
		return getInputSectionClass(cardTypeSelect);
	}

	public String getCardNameInputClass() {
		return getInputSectionClass(cardName);
	}

	public String getCardNumberInputClass() {
		return getInputSectionClass(cardNumber);
	}

	public String getCardcvvInputClass() {
		return getInputSectionClass(cardcvv);
	}

	private String getInputSectionClass(Field field) {
		ValidationTracker defaultTracker = paymentDetailsForm.getDefaultTracker();
		return defaultTracker == null || !defaultTracker.inError(field) ? messages.get("validInput") : messages.get("validateInput");
	}

    /**
     * @see ish.oncourse.enrol.pages.EnrolCourses#handleUnexpectedException(Throwable)
     */
	Object onException(Throwable cause) {
		return enrolCourses.handleUnexpectedException(cause);
	}

	@OnEvent(component = "paymentDetailsForm", value = "success")
	Object submitted() {
		
		Object resultPage = enrolCourses;

		if (isSubmitted)
        {
            synchronized (getController().getContext())
            {

                if (getController().canStartPaymentProcess())
                {
                    /**
                     * after we set the property to true EnrolmentPaymentProcessing.performGateway method is called.
                     */
                	getController().setCheckoutResult(true);
                    //enrolCourses.setCheckoutResult(true);
                }
                else
                {
                    resultPage = paymentZone.getBody();
                }
            }
        }
        else
        {
			resultPage = paymentZone.getBody();
		}

		return resultPage;
	}


    @OnEvent(component = "paymentDetailsForm", value = "failure")
	Object submitFailed() {
		return paymentZone.getBody();
	}

	@OnEvent(component = "paymentSubmit", value = "selected")
	void submitButtonPressed() {
		isSubmitted = true;
	}

	public boolean isNotEmptyPayer() {
		return !getModel().getContacts().isEmpty() && getModel().getPayment().getContact() != null;
	}

	/**
	 * Invokes when the {@link #paymentDetailsForm} is attempted to submit.
	 */
	@OnEvent(component = "paymentDetailsForm", value = "validate")
	void validate() {
		if (!isSubmitted) {
			return;
		}
        /**
         * The test shows error message for an user if he try to make a payment from several tabs
         */
        synchronized (getController().getContext())
        {
            if (!getController().canStartPaymentProcess())
            {
                paymentDetailsForm.recordError(cardTypeSelect, messages.get("twoOrMoreOpenedPaymentPagesErrorMessage"));
            }
        }

		if (!isZeroPayment()) {
			if (!isNotEmptyPayer()) {
				paymentDetailsForm.recordError(messages.get("emptyPayerErrorMessage"));
			}
			if (!getModel().getPayment().validateCCType()) {
				paymentDetailsForm.recordError(cardTypeSelect, messages.get("cardTypeErrorMessage"));
			}
			if (!getModel().getPayment().validateCCName()) {
				paymentDetailsForm.recordError(cardName, messages.get("cardNameErrorMessage"));
			}
			cardNumberErrorMessage = getModel().getPayment().validateCCNumber();
			if (cardNumberErrorMessage != null) {
				paymentDetailsForm.recordError(cardNumber, cardNumberErrorMessage);
			}

			String creditCardCVV = getModel().getPayment().getCreditCardCVV();
			if (creditCardCVV == null || creditCardCVV.equals("")) {
				paymentDetailsForm.recordError(cardcvv, messages.get("cardcvv"));
			}

			boolean hasErrorInDate = false;
			if (ccExpiryMonth == null || ccExpiryYear == null) {
				hasErrorInDate = true;
			} else {
				// don't check the format of ccExpiryMonth and ccExpiryYear
				// because
				// they are filled with dropdown lists with predefined
				// values
				getModel().getPayment().setCreditCardExpiry(ccExpiryMonth + "/" + ccExpiryYear);
			}

			hasErrorInDate = !getModel().getPayment().validateCCExpiry();
			if (hasErrorInDate) {
				paymentDetailsForm.recordError(messages.get("expiryDateError"));
			}


		}
		if (!userAgreed) {
			paymentDetailsForm.recordError(messages.get("agreeErrorMessage"));
		}
		
		if (!EnrolmentValidationUtil.isPlacesLimitExceeded(getController().getEnrolmentsToPersist(getModel().getEnrolmentsList()))) {
			paymentDetailsForm.recordError(messages.get(MESSAGE_NO_PLACES));
		}

	}

	public Zone getPaymentZone() {
		return paymentZone;
	}

	/**
	 * Checks if it is need to show or hide the submit button.
	 * 
	 * @return true if there is at least one enrolment selected(show submit
	 *         button), false id there no any enrolments selected(hide submit
	 *         button).
	 */
	public boolean isHasAnyEnrolmentsSelected() {
		for (Enrolment enrolment : getModel().getEnrolmentsList()) { 
			if (enrolment.getInvoiceLine() != null) {
				return true;
			}
		}
		return false;
	}

	public Money getTotalIncGst() {
		getModel().setMoneyFormat(FormatUtils.chooseMoneyFormat(getModel().getTotalIncGst()));
		return getModel().getTotalIncGst();
	}

    /**
     * Iterates through all the enrolments selected(ie which has the related
     * invoiceLine) and checks if the related class has any available places for
     * enrolling.
     *
     * @return true if all the selected classes are available for enrolling.
     */
    public boolean isAllEnrolmentsAvailable() {
        return getController().isAllEnrolmentsAvailable(getModel().getEnrolmentsList());
    }


}
