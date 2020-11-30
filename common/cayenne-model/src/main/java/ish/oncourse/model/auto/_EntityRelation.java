package ish.oncourse.model.auto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import org.apache.cayenne.exp.Property;

import ish.oncourse.model.College;
import ish.oncourse.model.EntityRelationType;

/**
 * Class _EntityRelation was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _EntityRelation extends WillowCayenneObject {

    private static final long serialVersionUID = 1L; 

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CREATED_PROPERTY = "created";
    public static final String FROM_ENTITY_IDENTIFIER_PROPERTY = "fromEntityIdentifier";
    public static final String FROM_ENTITY_WILLOW_ID_PROPERTY = "fromEntityWillowId";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String TO_ENTITY_IDENTIFIER_PROPERTY = "toEntityIdentifier";
    public static final String TO_ENTITY_WILLOW_ID_PROPERTY = "toEntityWillowId";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String RELATION_TYPE_PROPERTY = "relationType";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Long> ANGEL_ID = Property.create("angelId", Long.class);
    public static final Property<Date> CREATED = Property.create("created", Date.class);
    public static final Property<String> FROM_ENTITY_IDENTIFIER = Property.create("fromEntityIdentifier", String.class);
    public static final Property<Long> FROM_ENTITY_WILLOW_ID = Property.create("fromEntityWillowId", Long.class);
    public static final Property<Date> MODIFIED = Property.create("modified", Date.class);
    public static final Property<String> TO_ENTITY_IDENTIFIER = Property.create("toEntityIdentifier", String.class);
    public static final Property<Long> TO_ENTITY_WILLOW_ID = Property.create("toEntityWillowId", Long.class);
    public static final Property<College> COLLEGE = Property.create("college", College.class);
    public static final Property<EntityRelationType> RELATION_TYPE = Property.create("relationType", EntityRelationType.class);

    protected Long angelId;
    protected Date created;
    protected String fromEntityIdentifier;
    protected Long fromEntityWillowId;
    protected Date modified;
    protected String toEntityIdentifier;
    protected Long toEntityWillowId;

    protected Object college;
    protected Object relationType;

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

    public void setFromEntityIdentifier(String fromEntityIdentifier) {
        beforePropertyWrite("fromEntityIdentifier", this.fromEntityIdentifier, fromEntityIdentifier);
        this.fromEntityIdentifier = fromEntityIdentifier;
    }

    public String getFromEntityIdentifier() {
        beforePropertyRead("fromEntityIdentifier");
        return this.fromEntityIdentifier;
    }

    public void setFromEntityWillowId(Long fromEntityWillowId) {
        beforePropertyWrite("fromEntityWillowId", this.fromEntityWillowId, fromEntityWillowId);
        this.fromEntityWillowId = fromEntityWillowId;
    }

    public Long getFromEntityWillowId() {
        beforePropertyRead("fromEntityWillowId");
        return this.fromEntityWillowId;
    }

    public void setModified(Date modified) {
        beforePropertyWrite("modified", this.modified, modified);
        this.modified = modified;
    }

    public Date getModified() {
        beforePropertyRead("modified");
        return this.modified;
    }

    public void setToEntityIdentifier(String toEntityIdentifier) {
        beforePropertyWrite("toEntityIdentifier", this.toEntityIdentifier, toEntityIdentifier);
        this.toEntityIdentifier = toEntityIdentifier;
    }

    public String getToEntityIdentifier() {
        beforePropertyRead("toEntityIdentifier");
        return this.toEntityIdentifier;
    }

    public void setToEntityWillowId(Long toEntityWillowId) {
        beforePropertyWrite("toEntityWillowId", this.toEntityWillowId, toEntityWillowId);
        this.toEntityWillowId = toEntityWillowId;
    }

    public Long getToEntityWillowId() {
        beforePropertyRead("toEntityWillowId");
        return this.toEntityWillowId;
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }

    public void setRelationType(EntityRelationType relationType) {
        setToOneTarget("relationType", relationType, true);
    }

    public EntityRelationType getRelationType() {
        return (EntityRelationType)readProperty("relationType");
    }

    protected abstract void onPrePersist();

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
            case "fromEntityIdentifier":
                return this.fromEntityIdentifier;
            case "fromEntityWillowId":
                return this.fromEntityWillowId;
            case "modified":
                return this.modified;
            case "toEntityIdentifier":
                return this.toEntityIdentifier;
            case "toEntityWillowId":
                return this.toEntityWillowId;
            case "college":
                return this.college;
            case "relationType":
                return this.relationType;
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
            case "fromEntityIdentifier":
                this.fromEntityIdentifier = (String)val;
                break;
            case "fromEntityWillowId":
                this.fromEntityWillowId = (Long)val;
                break;
            case "modified":
                this.modified = (Date)val;
                break;
            case "toEntityIdentifier":
                this.toEntityIdentifier = (String)val;
                break;
            case "toEntityWillowId":
                this.toEntityWillowId = (Long)val;
                break;
            case "college":
                this.college = val;
                break;
            case "relationType":
                this.relationType = val;
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
        out.writeObject(this.fromEntityIdentifier);
        out.writeObject(this.fromEntityWillowId);
        out.writeObject(this.modified);
        out.writeObject(this.toEntityIdentifier);
        out.writeObject(this.toEntityWillowId);
        out.writeObject(this.college);
        out.writeObject(this.relationType);
    }

    @Override
    protected void readState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readState(in);
        this.angelId = (Long)in.readObject();
        this.created = (Date)in.readObject();
        this.fromEntityIdentifier = (String)in.readObject();
        this.fromEntityWillowId = (Long)in.readObject();
        this.modified = (Date)in.readObject();
        this.toEntityIdentifier = (String)in.readObject();
        this.toEntityWillowId = (Long)in.readObject();
        this.college = in.readObject();
        this.relationType = in.readObject();
    }

}
