package ish.oncourse.enrol.pages;

import ish.oncourse.enrol.checkout.HTMLUtils;
import ish.oncourse.enrol.checkout.ValidateHandler;
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
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.ui.pages.Courses;
import ish.oncourse.util.FormatUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.List;

/**
 * Java class for WaitingListForm.tml.
 * 
 * @author ksenia
 * 
 */
public class WaitingListForm {

	private static final Logger LOGGER = Logger.getLogger(WaitingListForm.class);

	public static final String KEY_ERROR_potentialStudent = "error-potentialStudent";
	
	@Property
	@Persist
	private WaitingListController controller;

	@Property
	@Persist
	private ValidateHandler validateHandler;

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

	@Inject
	@Property
	private Messages messages;

	@InjectComponent
	private ContactEditorFieldSet contactEditorFieldSet;

	@Inject
	@Id("waitingListBlock")
	private Block waitingListBlock;

	@Property
	private String error;

	@Property
	@Persist
	private Throwable unexpectedThrowable;

	@Property
	@Persist
	private boolean expired;

	@Property
	private boolean unknownCourse;

	@SetupRender
	void beforeRender() {

		synchronized (this)
		{
			if (unexpectedThrowable != null)
				handleUnexpectedThrowable();

			if (controller == null)
			{
				String courseId = request.getParameter("courseId");
				List<Course> result = courseService.loadByIds(courseId);
				if (!result.isEmpty()) {
					ObjectContext context = cayenneService.newContext();
					controller = new WaitingListController();
					controller.setPreferenceController(preferenceController);
					controller.setStudentService(studentService);
					controller.setObjectContext(context);
					controller.setMessages(messages);
					controller.setCollege((College)context.localObject(webSiteService.getCurrentCollege().getObjectId(), null));
					controller.setCourse((Course)context.localObject(result.get(0).getObjectId(), null));
					controller.init();

					validateHandler = new ValidateHandler();
				}
				else
					unknownCourse = true;

			}
		}
	}
	
	public String resetController() {
		if (controller.isFinished()) {
			controller = null;
		}
		return FormatUtils.EMPTY_STRING;
	}


	@OnEvent(value = "addWaitingListEvent")
	public Object addWaitingList()
	{
		if (!request.isXHR())
			return null;

		if (controller.isAddContact())
		{
			AddContactParser addContactValidator = new AddContactParser();
			addContactValidator.setContactCredentials(controller.getContactCredentials());
			addContactValidator.setRequest(request);
			addContactValidator.parse();
			controller.setErrors(addContactValidator.getErrors());
		} else if (controller.isEditContact())
		{
			ContactEditorParser parser = new ContactEditorParser();
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
		if (StringUtils.isNumeric(value) )
		{
			controller.getWaitingList().setPotentialStudents(Integer.valueOf(value));
			if (controller.getWaitingList().getPotentialStudents() < 0 || controller.getWaitingList().getPotentialStudents() > 30)
					controller.addError(WaitingList.POTENTIAL_STUDENTS_PROPERTY, messages.format(KEY_ERROR_potentialStudent));
		}
		value = StringUtils.trimToNull(request.getParameter(WaitingList.DETAIL_PROPERTY));
		if (value != null)
			controller.getWaitingList().setDetail(value);

		validateHandler.setErrors(controller.getErrors());
		controller.addWaitingList();
		return waitingListBlock;
	}


	public Object onException(Throwable cause) {
		if (controller != null) {
			unexpectedThrowable = cause;
		} else {
			expired = true;
			LOGGER.warn("Persist properties have been cleared. User used two or more tabs or session was expired", cause);
		}
		return waitingListBlock;
	}



	private void handleUnexpectedThrowable() {
		IllegalArgumentException exception = new IllegalArgumentException(unexpectedThrowable);
		if (controller != null) {
			controller.getObjectContext().rollbackChanges();
			resetPersistProperties();
		}
		throw exception;
	}

	public void resetPersistProperties()
	{
		expired = false;
		unexpectedThrowable = null;
		controller = null;
	}


	public String getCoursesLink() {
		return HTMLUtils.getUrlBy(request, Courses.class);
	}


}
