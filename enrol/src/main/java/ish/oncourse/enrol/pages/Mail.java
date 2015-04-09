package ish.oncourse.enrol.pages;

import ish.oncourse.enrol.checkout.ValidationResult;
import ish.oncourse.enrol.checkout.contact.AddContactParser;
import ish.oncourse.enrol.checkout.contact.ContactEditorParser;
import ish.oncourse.enrol.components.MailingListBox;
import ish.oncourse.enrol.components.checkout.contact.ContactEditorFieldSet;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.enrol.waitinglist.MailingListController;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Tag;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.reference.ICountryService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.util.HTMLUtils;
import ish.oncourse.util.ValidateHandler;
import org.apache.cayenne.ObjectContext;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.List;

import static ish.oncourse.services.preference.PreferenceController.ContactFiledsSet.mailinglist;

@Secure // this anatation is important. The page should use secure handling allways
public class Mail {

    private static final Logger LOGGER = Logger.getLogger(Mail.class);

    @Property
    private Tag currentMailingList;

    @Inject
    private ITagService tagService;

    @Inject
    private ICayenneService cayenneService;

    @Inject
    private PreferenceController preferenceController;

    @Inject
    private IStudentService studentService;

    @Inject
    private Messages messages;

    @Inject
    private IWebSiteService webSiteService;

	@Inject
	private ICountryService countryService;

    @Inject
    @Property
    private Request request;

    @InjectComponent
    private MailingListBox mailingListBox;

    @InjectComponent
    private ContactEditorFieldSet contactEditorFieldSet;


    @Property
    @Persist
    private MailingListController controller;

    @Property
    private ValidateHandler validateHandler;

    @Persist
    @Property
    private boolean expired;

    @Property
    @Persist
    private String refererUrl;

    private ValidationResult validationResult;

    @SetupRender
    Object setupRender() {

        if (expired)
            return null;

        synchronized (this) {
            if (controller == null) {
                ObjectContext context = cayenneService.newContext();
                controller = new MailingListController();
				controller.setContactFiledsSet(mailinglist);
                controller.setPreferenceController(preferenceController);
                controller.setStudentService(studentService);
                controller.setObjectContext(context);
                controller.setMessages(messages);
                controller.setTagService(tagService);
                controller.setCollege(context.localObject(webSiteService.getCurrentCollege()));
                controller.init();
                refererUrl = request.getHeader("referer");
            }
        }
        validateHandler = new ValidateHandler();
        validateHandler.setErrors(controller.getErrors());
        return null;
    }

    @OnEvent(component = "addMailingList", value = "selected")
    Object addMailingList() {

        if (controller.isAddContact()) {
            AddContactParser addContactParser = new AddContactParser();
            addContactParser.setContactCredentials(controller.getContactCredentials());
            addContactParser.setRequest(request);
            addContactParser.parse();

            controller.setErrors(addContactParser.getErrors());
        } else if (controller.isEditContact()) {
            ContactEditorParser parser = new ContactEditorParser();
			parser.setCountryService(countryService);
			parser.setMessages(contactEditorFieldSet.getMessages());
            parser.setContact(controller.getContact());
            parser.setVisibleFields(controller.getVisibleFields());
            parser.setContactFieldHelper(controller.getContactFieldHelper());
            parser.setRequest(request);
            parser.parse();
            controller.setErrors(parser.getErrors());
        }
        controller.addMailingList();
        return this;
    }

    public boolean hasMailingLists() {
        return !tagService.getMailingLists().isEmpty();
    }

    public List<Tag> getSelectedMailingLists() {
        return controller.getSelectedMailingLists();
    }

    @Deprecated //todo remove after the version was deployed
    public void setSelectedMailingLists(List<Tag> selectedMailingLists) {
    }

    @Deprecated //todo remove after the version was deployed
    public void setContact(Contact contact) {
    }

    public void resetPersistProperties()
    {
        expired = false;
        controller = null;
        refererUrl = null;
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
            resetPersistProperties();
            throw new IllegalArgumentException(cause);
        }
        return this;
    }

    public String getRedirectLink() {
        return (refererUrl != null) ? refererUrl : String.format("%s%s", HTMLUtils.HTTP_PROTOCOL, request.getServerName());
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

}
