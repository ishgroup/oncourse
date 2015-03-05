package ish.oncourse.enrol.pages;

import ish.oncourse.enrol.checkout.ValidationResult;
import ish.oncourse.enrol.checkout.contact.AddContactParser;
import ish.oncourse.enrol.checkout.contact.ContactEditorParser;
import ish.oncourse.enrol.components.checkout.contact.ContactEditorFieldSet;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.enrol.waitinglist.WaitingListController;
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
import ish.oncourse.utils.StringUtilities;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.Request;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ish.oncourse.services.preference.PreferenceController.ContactFiledsSet.waitinglist;

/**
 * Java class for WaitingListForm.tml.
 *
 * @author ksenia
 */

@Secure // this anatation is important. The page should use secure handling allways
public class WaitingListForm {
    private static final int MAX_PARAMETERS_AMOUNT = 10;

	private static final Logger LOGGER = Logger.getLogger(WaitingListForm.class);

	public static final String KEY_ERROR_potentialStudent = "message-potentialStudent";

	@Property
	@Persist
	private WaitingListController controller;

	@Property
	@Persist
	private ValidateHandler validateHandler;

	@Inject
    @Property
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
    private PageRenderLinkSource pageRenderLinkSource;

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

    @Persist
    private Map<String, String> requestParametes;


	@Property
	private boolean unknownCourse;

    private ValidationResult validationResult;

	private Long  courseId;

	void onActivate(Long id)
	{
		resetPersistProperties();
		courseId = id;

        storeParameters();
	}

    private void storeParameters() {
        List<String> names = request.getParameterNames();
        if (names.size() > 0 &&
                names.size() < MAX_PARAMETERS_AMOUNT)
        {
            requestParametes = new HashMap<>();
            for (int i = 0; i < names.size(); i++) {
                String name = request.getParameterNames().get(i);
                requestParametes.put(name, request.getParameter(name));
            }
        }
    }

    @SetupRender
	void beforeRender() {


		if (expired)
			return;

		synchronized (this) {
			if (controller == null) {

				if (courseId == null)
				{
					unknownCourse = true;
					return;
				}

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
				controller.setCollege(context.localObject(webSiteService.getCurrentCollege()));
				controller.setCourse(context.localObject(result.get(0)));
				controller.init();

				refererUrl = request.getHeader("referer");
			}
		}

		validateHandler = new ValidateHandler();
		validateHandler.setErrors(controller.getErrors());
	}

    public ValidationResult getValidationResult()
    {
        if (validationResult == null)
        {
            validationResult = new ValidationResult();
            validationResult.setWarnings(controller.getWarnings());
            validationResult.setErrors(controller.getErrors());
        }
        return validationResult;
    }


    public Object onSuccess()
    {
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
			parser.setRequest(request);
			parser.setVisibleFields(controller.getVisibleFields());
			parser.parse();
			controller.setErrors(parser.getErrors());
		}


		String value = StringUtilities.cutToNull(request.getParameter(WaitingList.POTENTIAL_STUDENTS_PROPERTY));
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

        Link  link = pageRenderLinkSource.createPageRenderLink(this.getClass());
        restoreParameters(link);
        return link;
	}

    private void restoreParameters(Link link) {
        if (requestParametes != null)
        {
            for (Map.Entry<String, String> entry : requestParametes.entrySet()) {
                link.addParameter(entry.getKey(), entry.getValue());
            }
        }
    }

    public void resetPersistProperties() {
		expired = false;
		controller = null;
		refererUrl = null;
		courseId = null;
        requestParametes = null;
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
            controller.getObjectContext().rollbackChanges();
            resetPersistProperties();
			throw new IllegalArgumentException(cause);
		}
		return this;
	}

}