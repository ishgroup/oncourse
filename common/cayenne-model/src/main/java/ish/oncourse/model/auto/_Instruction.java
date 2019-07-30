package ish.oncourse.model.auto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.exp.Property;

import ish.oncourse.model.College;
import ish.oncourse.model.InstructionParameter;

/**
 * Class _Instruction was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Instruction extends WillowCayenneObject {

    private static final long serialVersionUID = 1L; 

    public static final String CREATED_PROPERTY = "created";
    public static final String EXECUTED_PROPERTY = "executed";
    public static final String MESSAGE_PROPERTY = "message";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String RESPONSE_PROPERTY = "response";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String PARAMETERS_PROPERTY = "parameters";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Date> CREATED = Property.create("created", Date.class);
    public static final Property<Date> EXECUTED = Property.create("executed", Date.class);
    public static final Property<String> MESSAGE = Property.create("message", String.class);
    public static final Property<Date> MODIFIED = Property.create("modified", Date.class);
    public static final Property<String> RESPONSE = Property.create("response", String.class);
    public static final Property<College> COLLEGE = Property.create("college", College.class);
    public static final Property<List<InstructionParameter>> PARAMETERS = Property.create("parameters", List.class);

    protected Date created;
    protected Date executed;
    protected String message;
    protected Date modified;
    protected String response;

    protected Object college;
    protected Object parameters;

    public void setCreated(Date created) {
        beforePropertyWrite("created", this.created, created);
        this.created = created;
    }

    public Date getCreated() {
        beforePropertyRead("created");
        return this.created;
    }

    public void setExecuted(Date executed) {
        beforePropertyWrite("executed", this.executed, executed);
        this.executed = executed;
    }

    public Date getExecuted() {
        beforePropertyRead("executed");
        return this.executed;
    }

    public void setMessage(String message) {
        beforePropertyWrite("message", this.message, message);
        this.message = message;
    }

    public String getMessage() {
        beforePropertyRead("message");
        return this.message;
    }

    public void setModified(Date modified) {
        beforePropertyWrite("modified", this.modified, modified);
        this.modified = modified;
    }

    public Date getModified() {
        beforePropertyRead("modified");
        return this.modified;
    }

    public void setResponse(String response) {
        beforePropertyWrite("response", this.response, response);
        this.response = response;
    }

    public String getResponse() {
        beforePropertyRead("response");
        return this.response;
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }

    public void addToParameters(InstructionParameter obj) {
        addToManyTarget("parameters", obj, true);
    }

    public void removeFromParameters(InstructionParameter obj) {
        removeToManyTarget("parameters", obj, true);
    }

    @SuppressWarnings("unchecked")
    public List<InstructionParameter> getParameters() {
        return (List<InstructionParameter>)readProperty("parameters");
    }

    @Override
    public Object readPropertyDirectly(String propName) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch(propName) {
            case "created":
                return this.created;
            case "executed":
                return this.executed;
            case "message":
                return this.message;
            case "modified":
                return this.modified;
            case "response":
                return this.response;
            case "college":
                return this.college;
            case "parameters":
                return this.parameters;
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
            case "created":
                this.created = (Date)val;
                break;
            case "executed":
                this.executed = (Date)val;
                break;
            case "message":
                this.message = (String)val;
                break;
            case "modified":
                this.modified = (Date)val;
                break;
            case "response":
                this.response = (String)val;
                break;
            case "college":
                this.college = val;
                break;
            case "parameters":
                this.parameters = val;
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
        out.writeObject(this.created);
        out.writeObject(this.executed);
        out.writeObject(this.message);
        out.writeObject(this.modified);
        out.writeObject(this.response);
        out.writeObject(this.college);
        out.writeObject(this.parameters);
    }

    @Override
    protected void readState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readState(in);
        this.created = (Date)in.readObject();
        this.executed = (Date)in.readObject();
        this.message = (String)in.readObject();
        this.modified = (Date)in.readObject();
        this.response = (String)in.readObject();
        this.college = in.readObject();
        this.parameters = in.readObject();
    }

}
