package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import ish.oncourse.model.College;
import ish.oncourse.model.InstructionParameter;

/**
 * Class _Instruction was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Instruction extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    @Deprecated
    public static final String CREATED_PROPERTY = "created";
    @Deprecated
    public static final String EXECUTED_PROPERTY = "executed";
    @Deprecated
    public static final String MESSAGE_PROPERTY = "message";
    @Deprecated
    public static final String MODIFIED_PROPERTY = "modified";
    @Deprecated
    public static final String RESPONSE_PROPERTY = "response";
    @Deprecated
    public static final String COLLEGE_PROPERTY = "college";
    @Deprecated
    public static final String PARAMETERS_PROPERTY = "parameters";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Date> CREATED = new Property<Date>("created");
    public static final Property<Date> EXECUTED = new Property<Date>("executed");
    public static final Property<String> MESSAGE = new Property<String>("message");
    public static final Property<Date> MODIFIED = new Property<Date>("modified");
    public static final Property<String> RESPONSE = new Property<String>("response");
    public static final Property<College> COLLEGE = new Property<College>("college");
    public static final Property<List<InstructionParameter>> PARAMETERS = new Property<List<InstructionParameter>>("parameters");

    public void setCreated(Date created) {
        writeProperty("created", created);
    }
    public Date getCreated() {
        return (Date)readProperty("created");
    }

    public void setExecuted(Date executed) {
        writeProperty("executed", executed);
    }
    public Date getExecuted() {
        return (Date)readProperty("executed");
    }

    public void setMessage(String message) {
        writeProperty("message", message);
    }
    public String getMessage() {
        return (String)readProperty("message");
    }

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setResponse(String response) {
        writeProperty("response", response);
    }
    public String getResponse() {
        return (String)readProperty("response");
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


}
