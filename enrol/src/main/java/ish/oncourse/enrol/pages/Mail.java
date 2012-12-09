package ish.oncourse.enrol.pages;

import ish.oncourse.enrol.checkout.ValidateHandler;
import ish.oncourse.enrol.checkout.contact.AddContactParser;
import ish.oncourse.enrol.components.MailingListBox;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.enrol.waitinglist.MailingListController;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Tag;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import org.apache.cayenne.ObjectContext;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.List;

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
    private Request  request;

    @InjectComponent
    private MailingListBox mailingListBox;

    @Property
    @Persist
    private MailingListController controller;

    @Property
    private ValidateHandler validateHandler;

    @Persist
    private boolean expiered;

    @SetupRender
    Object setupRender() {

        if (expiered)
            return null;

        synchronized (this) {
            if (controller == null) {
                ObjectContext context = cayenneService.newContext();
                controller = new MailingListController();
                controller.setPreferenceController(preferenceController);
                controller.setStudentService(studentService);
                controller.setObjectContext(context);
                controller.setMessages(messages);
                controller.setTagService(tagService);
                controller.setCollege((College) context.localObject(webSiteService.getCurrentCollege().getObjectId(), null));
                controller.init();
            }
        }
        validateHandler = new ValidateHandler();
        validateHandler.setErrors(controller.getErrors());
        return null;
    }

    @AfterRender
    void afterRender() {
        if (controller != null && controller.isFinished()) {
           controller = null;
        }
        controller = null;
        expiered = false;
    }

    @OnEvent(component = "addMailingList", value = "selected")
    Object addMailingList() {

        if (controller.isAddContact())
        {
            AddContactParser addContactParser = new AddContactParser();
            addContactParser.setContactCredentials(controller.getContactCredentials());
            addContactParser.setRequest(request);
            addContactParser.parse();

            controller.setErrors(addContactParser.getErrors());
            controller.setSelectedMailingLists(mailingListBox.getSelectedMailingLists());
            controller.addMailingList();
        }
        return this;
    }

    public boolean hasMailingLists()
    {
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

    public Object onException(Throwable cause) {
        if (controller == null) {
            LOGGER.warn("", cause);
            expiered = true;
        } else {
            controller = null;
            throw new IllegalArgumentException(cause);
        }
        return this;
    }

    public boolean isExpiered() {
        return expiered;
    }
}
