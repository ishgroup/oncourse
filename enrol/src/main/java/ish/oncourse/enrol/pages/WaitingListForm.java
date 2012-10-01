package ish.oncourse.enrol.pages;

import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Course;
import ish.oncourse.model.WaitingList;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Java class for WaitingListForm.tml.
 * 
 * @author ksenia
 * 
 */
public class WaitingListForm {
	
	private static final DateFormat FORMAT = new SimpleDateFormat("d/M/y");
	
	private static final String REQUIRE_FIELD = "Required";
	private static final String SHOW_FIELD = "Show";
	 
	@Inject
	private Request request;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICourseService courseService;

	@Inject
	private IStudentService studentService;
	
	@Inject
	private PreferenceController preferenceController;

	@Persist
	@Property
	private Course course;

	@Persist
	@Property
	private Contact contact;

	@Persist
	@Property
	private WaitingList waitingList;

	@Property
	@Persist
	private boolean submittedSuccessfully;
	
	@Property
	@Persist
	private boolean showAdditionalDetails;
	
	@Property
	@Persist
	private ObjectContext context;
	
    /**
     * error message template properties
     */

    @Property
    private String suburbErrorMessage;

    @Property
    private String postcodeErrorMessage;

    @Property
    private String stateErrorMessage;

    @Property
    private String homePhoneErrorMessage;

    @Property
    private String mobilePhoneErrorMessage;

    @Property
    private String businessPhoneErrorMessage;

    @Property
    private String faxErrorMessage;

    @Property
    private String birthDateErrorMessage;

	@InjectComponent
	@Property
	private Form waitingListForm;

	@InjectComponent
	private Field waitlistFirstName;

	@InjectComponent
	private Field waitlistLastName;

	@InjectComponent
	private Field waitlistEmail;
	
	@InjectComponent
	private Field numberOfStudents;
	
    @InjectComponent
    private TextField street;

    @InjectComponent
    private TextField suburb;

    @InjectComponent
    private TextField postcode;

    @InjectComponent
    private TextField state;

    @InjectComponent
    private TextField homePhone;

    @InjectComponent
    private TextField mobilePhone;

    @InjectComponent
    private TextField businessPhone;

    @InjectComponent
    private TextField fax;

    @InjectComponent
    private TextField birthDate;

	@SetupRender
	void beforeRender() {
		String courseId = request.getParameter("courseId");
		List<Course> result = courseService.loadByIds(courseId);
		if (!result.isEmpty()) {
			course = result.get(0);
		}
		getNewContact();
		if (waitingList == null) {
			waitingList = new WaitingList();
		}
	}
	
	@AfterRender
	void afterRender() {
		if (submittedSuccessfully) {
			waitingList = null;
			context = null;
			contact = null;
			
			submittedSuccessfully = false;
			showAdditionalDetails = false;
		}
	}
	
	/**
	 * Return contact for waiting list, create if needed.
	 * @return contact for waiting list.need to avoid #13108
	 */
	public Contact getNewContact() {
		if (contact == null) {
			contact = new Contact();
		}
		return contact;
	}

	@OnEvent(component = "waitingListForm", value = "validate")
	void validate() {
		String firstNameErrorMessage = getNewContact().validateGivenName();
		if (firstNameErrorMessage != null) {
			waitingListForm.recordError(waitlistFirstName, firstNameErrorMessage);
		}
		
		String lastNameErrorMessage = getNewContact().validateFamilyName();
		if (lastNameErrorMessage != null) {
			waitingListForm.recordError(waitlistLastName, lastNameErrorMessage);
		}
		
		String emailErrorMessage = getNewContact().validateEmail();
		if (emailErrorMessage != null) {
			waitingListForm.recordError(waitlistEmail, emailErrorMessage);
		}
		
		if (waitingList != null && waitingList.getPotentialStudents() != null) {
			if (!(waitingList.getPotentialStudents() > 0 && waitingList.getPotentialStudents() <= 30)) {
				waitingListForm.recordError(numberOfStudents, 
						"You can only enter numbers from 1 to 30. If you have larger groups please add the details in the notes.");
			}
		}
		
        if (course == null || contact == null || waitingList == null) {
            waitingListForm.recordError("Session has been expired. Please reload the form.");
        }
        
        
        if (showAdditionalDetails) {
        	
            suburbErrorMessage = contact.validateSuburb();
            if (suburbErrorMessage != null) {
                waitingListForm.recordError(suburb, suburbErrorMessage);
            }
            postcodeErrorMessage = contact.validatePostcode();
            if (postcodeErrorMessage != null) {
                waitingListForm.recordError(postcode, postcodeErrorMessage);
            }
            stateErrorMessage = contact.validateState();
            if (stateErrorMessage != null) {
                waitingListForm.recordError(state, stateErrorMessage);
            }
            homePhoneErrorMessage = contact.validateHomePhone();
            if (homePhoneErrorMessage != null) {
                waitingListForm.recordError(homePhone, homePhoneErrorMessage);
            }
            mobilePhoneErrorMessage = contact.validateMobilePhone();
            if (mobilePhoneErrorMessage != null) {
                waitingListForm.recordError(mobilePhone, mobilePhoneErrorMessage);
            }
            businessPhoneErrorMessage = contact.validateBusinessPhone();
            if (businessPhoneErrorMessage != null) {
                waitingListForm.recordError(businessPhone, businessPhoneErrorMessage);
            }
            faxErrorMessage = contact.validateFax();
            if (faxErrorMessage != null) {
                waitingListForm.recordError(fax, faxErrorMessage);
            }
            
            if (getRequireDateOfBirth() || contact.getDateOfBirth() != null) {
            	if (birthDateErrorMessage == null) {
            		birthDateErrorMessage = contact.validateBirthDate();
            	}
            	if (birthDateErrorMessage != null) {
            		waitingListForm.recordError(birthDate, birthDateErrorMessage);
            	}
            }
        	
        	validateRequiredFields();
        }
	}

	@OnEvent(component = "waitingListForm", value = "submit")
	Object submittedSuccessfully() {
		if (!waitingListForm.getHasErrors()) {

			if (context == null || !showAdditionalDetails) {
				context = cayenneService.newContext();
			}
			College college = (College) context.localObject(webSiteService.getCurrentCollege().getObjectId(), null);

			Contact studentContact = studentService.getStudentContact(getNewContact().getGivenName(), getNewContact().getFamilyName(),
					getNewContact().getEmailAddress());
			//add this check to avoid the state when we try to register contact which were previously registered, but not committed yet
			if (studentContact == null && getNewContact().getObjectId() != null) {
				if (getNewContact().getObjectId().isTemporary()) {
					if (getNewContact().getCollege() == null) {
						getNewContact().setCollege(college);
					}
					getNewContact().getObjectContext().commitChanges();
				}
				studentContact = getNewContact();
			}
			if (studentContact != null) {
				studentContact = (Contact) context.localObject(studentContact.getObjectId(), null);
				if (studentContact.getStudent() == null) {
					studentContact.createNewStudent();
				}
			} else {
				context.registerNewObject(getNewContact());
				getNewContact().setCollege(college);
				studentContact = getNewContact();
				studentContact.createNewStudent();
				
				showAdditionalDetails = true;
				
				return null;
			}
			//this check added to prevent #13048.
			if (waitingList.getObjectId() == null) {
				context.registerNewObject(waitingList);
			} else {
				waitingList = (WaitingList) context.localObject(waitingList.getObjectId(), null);
			}
			waitingList.setCollege(college);
			waitingList.setStudent(studentContact.getStudent());
			waitingList.setCourse((Course) context.localObject(course.getObjectId(), null));
			context.commitChanges();
			submittedSuccessfully = true;
		}

		return this;
	}
	
    private void validateRequiredFields() {
    	if (getRequireAddress()) {
    		if (contact.getStreet() == null || "".equals(contact.getStreet())) {
    			waitingListForm.recordError(street, "Address is required.");
    		}
    	}
    	if (getRequireSuburb()) {
    		if (contact.getSuburb() == null || "".equals(contact.getSuburb())) {
    			waitingListForm.recordError(suburb, "Suburb is required.");
    		}
    	}
    	if (getRequireState()) {
    		if (contact.getState() == null || "".equals(contact.getState())) {
    			waitingListForm.recordError(state, "State is required.");
    		}
    	}
    	if (getRequirePostcode()) {
    		if (contact.getPostcode() == null || "".equals(contact.getPostcode())) {
    			waitingListForm.recordError(postcode, "Postcode is required.");
    		}
    	}
    	if (getRequireHomePhone()) {
    		if (contact.getHomePhoneNumber() == null || "".equals(contact.getHomePhoneNumber())) {
    			waitingListForm.recordError(homePhone, "Home phone is required.");
    		}
    	}
    	if (getRequireBusinessPhone()) {
    		if (contact.getBusinessPhoneNumber() == null || "".equals(contact.getBusinessPhoneNumber())) {
    			waitingListForm.recordError(businessPhone, "Business phone is required.");
    		}
    	}
    	if (getRequireFax()) {
    		if (contact.getFaxNumber() == null || "".equals(contact.getFaxNumber())) {
    			waitingListForm.recordError(fax, "Fax is required.");
    		}
    	}
    	if (getRequireMobile()) {
    		if (contact.getMobilePhoneNumber() == null || "".equals(contact.getMobilePhoneNumber())) {
    			waitingListForm.recordError(mobilePhone, "Mobile phone is required.");
    		}
    	}
    	if (getRequireDateOfBirth()) {
    		if (contact.getDateOfBirth() == null) {
    			waitingListForm.recordError(birthDate, "Date of birth is required.");
    		}
    	}
    }
	
	public boolean getShowAddress() {
		String require = preferenceController.getRequireContactAddressWaitingList();
		if (SHOW_FIELD.equals(require) || REQUIRE_FIELD.equals(require) || require == null) {
			return true;
		}
		return false;
	}

	public boolean getRequireAddress() {
		String require = preferenceController.getRequireContactAddressWaitingList();
		if (REQUIRE_FIELD.equals(require)) {
			return true;
		}
		return false;
	}

	public boolean getShowSuburb() {
		String require = preferenceController.getRequireContactSuburbWaitingList();
		if (SHOW_FIELD.equals(require) || REQUIRE_FIELD.equals(require) || require == null) {
			return true;
		}
		return false;
	}

	public boolean getRequireSuburb() {
		String require = preferenceController.getRequireContactSuburbWaitingList();
		if (REQUIRE_FIELD.equals(require)) {
			return true;
		}
		return false;
	}

	public boolean getShowState() {
		String require = preferenceController.getRequireContactStateWaitingList();
		if (SHOW_FIELD.equals(require) || REQUIRE_FIELD.equals(require) || require == null) {
			return true;
		}
		return false;
	}

	public boolean getRequireState() {
		String require = preferenceController.getRequireContactStateWaitingList();
		if (REQUIRE_FIELD.equals(require)) {
			return true;
		}
		return false;
	}

	public boolean getShowPostcode() {
		String require = preferenceController.getRequireContactPostcodeWaitingList();
		if (SHOW_FIELD.equals(require) || REQUIRE_FIELD.equals(require) || require == null) {
			return true;
		}
		return false;
	}

	public boolean getRequirePostcode() {
		String require = preferenceController.getRequireContactPostcodeWaitingList();
		if (REQUIRE_FIELD.equals(require)) {
			return true;
		}
		return false;
	}

	public boolean getShowHomePhone() {
		String require = preferenceController.getRequireContactHomePhoneWaitingList();
		if (SHOW_FIELD.equals(require) || REQUIRE_FIELD.equals(require) || require == null) {
			return true;
		}
		return false;
	}

	public boolean getRequireHomePhone() {
		String require = preferenceController.getRequireContactHomePhoneWaitingList();
		if (REQUIRE_FIELD.equals(require)) {
			return true;
		}
		return false;
	}

	public boolean getShowBusinessPhone() {
		String require = preferenceController.getRequireContactBusinessPhoneWaitingList();
		if (SHOW_FIELD.equals(require) || REQUIRE_FIELD.equals(require) || require == null) {
			return true;
		}
		return false;
	}

	public boolean getRequireBusinessPhone() {
		String require = preferenceController.getRequireContactBusinessPhoneWaitingList();
		if (REQUIRE_FIELD.equals(require)) {
			return true;
		}
		return false;
	}

	public boolean getShowFax() {
		String require = preferenceController.getRequireContactFaxWaitingList();
		if (SHOW_FIELD.equals(require) || REQUIRE_FIELD.equals(require) || require == null) {
			return true;
		}
		return false;
	}

	public boolean getRequireFax() {
		String require = preferenceController.getRequireContactFaxWaitingList();
		if (REQUIRE_FIELD.equals(require)) {
			return true;
		}
		return false;
	}

	public boolean getShowMobile() {
		String require = preferenceController.getRequireContactMobileWaitingList();
		if (SHOW_FIELD.equals(require) || REQUIRE_FIELD.equals(require) || require == null) {
			return true;
		}
		return false;
	}

	public boolean getRequireMobile() {
		String require = preferenceController.getRequireContactMobileWaitingList();
		if (REQUIRE_FIELD.equals(require)) {
			return true;
		}
		return false;
	}

	public boolean getShowDateOfBirth() {
		String require = preferenceController.getRequireContactDateOfBirthWaitingList();
		if (SHOW_FIELD.equals(require) || REQUIRE_FIELD.equals(require) || require == null) {
			return true;
		}
		return false;
	}

	public boolean getRequireDateOfBirth() {
		String require = preferenceController.getRequireContactDateOfBirthWaitingList();
		if (REQUIRE_FIELD.equals(require)) {
			return true;
		}
		return false;
	}
	
    public void setBirthDateProperty(String birthDateProperty) {
        try {
            if (birthDateProperty != null && !"".equals(birthDateProperty)) {
                Date parsedDate = FORMAT.parse(birthDateProperty);
                contact.setDateOfBirth(parsedDate);
            }
        } catch (ParseException e) {
            birthDateErrorMessage = "Please enter a valid date of birth and formatted as indicated: in the form 25/12/2000";
        }
    }
    
    public String getBirthDateProperty() {
        Date dateOfBirth = contact.getDateOfBirth();
        if (dateOfBirth == null) {
            return null;
        }
        return FORMAT.format(dateOfBirth);
    }
}
