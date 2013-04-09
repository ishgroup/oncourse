package ish.oncourse.enrol.pages;

import ish.oncourse.enrol.checkout.contact.AddContactParser;
import ish.oncourse.enrol.checkout.contact.ContactEditorParser;
import ish.oncourse.enrol.components.checkout.contact.ContactEditorFieldSet;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.enrol.waitinglist.WaitingListController;
import ish.oncourse.model.College;
import ish.oncourse.model.Course;
import ish.oncourse.model.WaitingList;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.ui.pages.Courses;
import ish.oncourse.util.HTMLUtils;
import ish.oncourse.util.ValidateHandler;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ish.oncourse.services.preference.PreferenceController.ContactFiledsSet.waitinglist;

/**
 * Java class for WaitingListForm.tml.
 *
 * @author ksenia
 */
public class WaitingListForm {

	private static final Logger LOGGER = Logger.getLogger(WaitingListForm.class);

	public static final String KEY_ERROR_potentialStudent = "message-potentialStudent";

	@Property
	@Persist
	private WaitingListController controller;

	@Property
	@Persist
	private ValidateHandler validateHandler;

	@Inject
	private Request request;

	@Inject
	private HttpServletRequest httpRequest;

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

	@Inject
	private ICountryService countryService;

	@Inject
	@Property
	private Messages messages;

	@InjectComponent
	private ContactEditorFieldSet contactEditorFieldSet;

	@Property
	@Persist
	private boolean expired;

	@Property
	@Persist
	private String refererUrl;


	@Property
	private boolean unknownCourse;

	@SetupRender
	void beforeRender() {


		if (expired)
			return;

		synchronized (this) {
			if (controller == null) {
				String courseId = request.getParameter("courseId");
				List<Course> result = courseService.loadByIds(courseId);
				if (result.isEmpty()) {
					unknownCourse = true;
					return;
				}

				ObjectContext context = cayenneService.newContext();
				controller = new WaitingListController();
				controller.setContactFiledsSet(waitinglist);
				controller.setPreferenceController(preferenceController);
				controller.setStudentService(studentService);
				controller.setObjectContext(context);
				controller.setMessages(messages);
				controller.setCollege((College) context.localObject(webSiteService.getCurrentCollege().getObjectId(), null));
				controller.setCourse((Course) context.localObject(result.get(0).getObjectId(), null));
				controller.init();

				refererUrl = request.getHeader("referer");
			}
		}
		validateHandler = new ValidateHandler();
		validateHandler.setErrors(controller.getErrors());
	}

	@OnEvent(component = "addWaitingList", value = "selected")
	public Object addWaitingList() {
		if (controller.isAddContact()) {
			AddContactParser addContactValidator = new AddContactParser();
			addContactValidator.setContactCredentials(controller.getContactCredentials());
			addContactValidator.setRequest(request);
			addContactValidator.parse();
			controller.setErrors(addContactValidator.getErrors());
		} else if (controller.isEditContact()) {
			ContactEditorParser parser = new ContactEditorParser();
			parser.setCountryService(countryService);
			parser.setContact(controller.getContact());
			parser.setContactFieldHelper(controller.getContactFieldHelper());
			parser.setMessages(contactEditorFieldSet.getMessages());
			parser.setDateFormat(contactEditorFieldSet.getDateFormat());
			parser.setRequest(request);
			parser.setVisibleFields(controller.getVisibleFields());
			parser.parse();
			controller.setErrors(parser.getErrors());
		}


		String value = StringUtils.trimToNull(request.getParameter(WaitingList.POTENTIAL_STUDENTS_PROPERTY));
		if (StringUtils.isNumeric(value)) {
			controller.getWaitingList().setPotentialStudents(Integer.valueOf(value));
		}

        Integer amount = controller.getWaitingList().getPotentialStudents();
        if (amount == null || controller.getWaitingList().getPotentialStudents() < 1 || controller.getWaitingList().getPotentialStudents() > 30)
        {
            controller.addError(WaitingList.POTENTIAL_STUDENTS_PROPERTY, messages.format(KEY_ERROR_potentialStudent));
            controller.getWaitingList().setPotentialStudents(1);
        }

        value = StringUtils.trimToNull(request.getParameter(WaitingList.DETAIL_PROPERTY));
		if (value != null)
			controller.getWaitingList().setDetail(value);
		controller.addWaitingList();
		return this;
	}


	public void resetPersistProperties() {
		expired = false;
		controller = null;
		refererUrl = null;
	}


	public String getCoursesLink() {
		return (refererUrl != null) ? refererUrl : HTMLUtils.getUrlBy(request.getServerName(), Courses.class);
	}


	@AfterRender
	void afterRender() {
		if (controller != null && controller.isFinished()) {
			resetPersistProperties();
		}
		expired = false;
	}

	public Object onException(Throwable cause) {
		if (controller == null) {
			LOGGER.warn("", cause);
			expired = true;
		} else {
			if (controller != null) {
				controller.getObjectContext().rollbackChanges();
			}
			resetPersistProperties();
			throw new IllegalArgumentException(cause);
		}
		return this;
	}

}