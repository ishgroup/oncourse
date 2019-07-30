package ish.oncourse.model.auto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.exp.Property;

import ish.oncourse.model.College;
import ish.oncourse.model.MessagePerson;

/**
 * Class _Message was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Message extends WillowCayenneObject {

    private static final long serialVersionUID = 1L; 

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CREATED_PROPERTY = "created";
    public static final String EMAIL_BODY_PROPERTY = "emailBody";
    public static final String EMAIL_SUBJECT_PROPERTY = "emailSubject";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String SMS_TEXT_PROPERTY = "smsText";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String MESSAGE_PEOPLE_PROPERTY = "messagePeople";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Long> ANGEL_ID = Property.create("angelId", Long.class);
    public static final Property<Date> CREATED = Property.create("created", Date.class);
    public static final Property<String> EMAIL_BODY = Property.create("emailBody", String.class);
    public static final Property<String> EMAIL_SUBJECT = Property.create("emailSubject", String.class);
    public static final Property<Date> MODIFIED = Property.create("modified", Date.class);
    public static final Property<String> SMS_TEXT = Property.create("smsText", String.class);
    public static final Property<College> COLLEGE = Property.create("college", College.class);
    public static final Property<List<MessagePerson>> MESSAGE_PEOPLE = Property.create("messagePeople", List.class);

    protected Long angelId;
    protected Date created;
    protected String emailBody;
    protected String emailSubject;
    protected Date modified;
    protected String smsText;

    protected Object college;
    protected Object messagePeople;

    public void setAngelId(Long angelId) {
        beforePropertyWrite("angelId", this.angelId, angelId);
        this.angelId = angelId;
    }

    public Long getAngelId() {
        beforePropertyRead("angelId");
        return this.angelId;
    }

    public void setCreated(Date created) {
        beforePropertyWrite("created", this.created, created);
        this.created = created;
    }

    public Date getCreated() {
        beforePropertyRead("created");
        return this.created;
    }

    public void setEmailBody(String emailBody) {
        beforePropertyWrite("emailBody", this.emailBody, emailBody);
        this.emailBody = emailBody;
    }

    public String getEmailBody() {
        beforePropertyRead("emailBody");
        return this.emailBody;
    }

    public void setEmailSubject(String emailSubject) {
        beforePropertyWrite("emailSubject", this.emailSubject, emailSubject);
        this.emailSubject = emailSubject;
    }

    public String getEmailSubject() {
        beforePropertyRead("emailSubject");
        return this.emailSubject;
    }

    public void setModified(Date modified) {
        beforePropertyWrite("modified", this.modified, modified);
        this.modified = modified;
    }

    public Date getModified() {
        beforePropertyRead("modified");
        return this.modified;
    }

    public void setSmsText(String smsText) {
        beforePropertyWrite("smsText", this.smsText, smsText);
        this.smsText = smsText;
    }

    public String getSmsText() {
        beforePropertyRead("smsText");
        return this.smsText;
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }

    public void addToMessagePeople(MessagePerson obj) {
        addToManyTarget("messagePeople", obj, true);
    }

    public void removeFromMessagePeople(MessagePerson obj) {
        removeToManyTarget("messagePeople", obj, true);
    }

    @SuppressWarnings("unchecked")
    public List<MessagePerson> getMessagePeople() {
        return (List<MessagePerson>)readProperty("messagePeople");
    }

    @Override
    public Object readPropertyDirectly(String propName) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch(propName) {
            case "angelId":
                return this.angelId;
            case "created":
                return this.created;
            case "emailBody":
                return this.emailBody;
            case "emailSubject":
                return this.emailSubject;
            case "modified":
                return this.modified;
            case "smsText":
                return this.smsText;
            case "college":
                return this.college;
            case "messagePeople":
                return this.messagePeople;
            default:
                return super.readPropertyDirectly(propName);
        }
    }

    @Override
    public void writePropertyDirectly(String propName, Object val) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch (propName) {
            case "angelId":
                this.angelId = (Long)val;
                break;
            case "created":
                this.created = (Date)val;
                break;
            case "emailBody":
                this.emailBody = (String)val;
                break;
            case "emailSubject":
                this.emailSubject = (String)val;
                break;
            case "modified":
                this.modified = (Date)val;
                break;
            case "smsText":
                this.smsText = (String)val;
                break;
            case "college":
                this.college = val;
                break;
            case "messagePeople":
                this.messagePeople = val;
                break;
            default:
                super.writePropertyDirectly(propName, val);
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        writeSerialized(out);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        readSerialized(in);
    }

    @Override
    protected void writeState(ObjectOutputStream out) throws IOException {
        super.writeState(out);
        out.writeObject(this.angelId);
        out.writeObject(this.created);
        out.writeObject(this.emailBody);
        out.writeObject(this.emailSubject);
        out.writeObject(this.modified);
        out.writeObject(this.smsText);
        out.writeObject(this.college);
        out.writeObject(this.messagePeople);
    }

    @Override
    protected void readState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readState(in);
        this.angelId = (Long)in.readObject();
        this.created = (Date)in.readObject();
        this.emailBody = (String)in.readObject();
        this.emailSubject = (String)in.readObject();
        this.modified = (Date)in.readObject();
        this.smsText = (String)in.readObject();
        this.college = in.readObject();
        this.messagePeople = in.readObject();
    }

}
