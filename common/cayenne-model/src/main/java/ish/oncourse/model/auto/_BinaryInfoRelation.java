package ish.oncourse.model.auto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import org.apache.cayenne.exp.Property;

import ish.common.types.AttachmentSpecialType;
import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.College;
import ish.oncourse.model.Document;
import ish.oncourse.model.DocumentVersion;

/**
 * Class _BinaryInfoRelation was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _BinaryInfoRelation extends WillowCayenneObject {

    private static final long serialVersionUID = 1L; 

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CREATED_PROPERTY = "created";
    public static final String ENTITY_ANGEL_ID_PROPERTY = "entityAngelId";
    public static final String ENTITY_IDENTIFIER_PROPERTY = "entityIdentifier";
    public static final String ENTITY_WILLOW_ID_PROPERTY = "entityWillowId";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String SPECIAL_TYPE_PROPERTY = "specialType";
    public static final String BINARY_INFO_PROPERTY = "binaryInfo";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String DOCUMENT_PROPERTY = "document";
    public static final String DOCUMENT_VERSION_PROPERTY = "documentVersion";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Long> ANGEL_ID = Property.create("angelId", Long.class);
    public static final Property<Date> CREATED = Property.create("created", Date.class);
    public static final Property<Long> ENTITY_ANGEL_ID = Property.create("entityAngelId", Long.class);
    public static final Property<String> ENTITY_IDENTIFIER = Property.create("entityIdentifier", String.class);
    public static final Property<Long> ENTITY_WILLOW_ID = Property.create("entityWillowId", Long.class);
    public static final Property<Date> MODIFIED = Property.create("modified", Date.class);
    public static final Property<AttachmentSpecialType> SPECIAL_TYPE = Property.create("specialType", AttachmentSpecialType.class);
    public static final Property<BinaryInfo> BINARY_INFO = Property.create("binaryInfo", BinaryInfo.class);
    public static final Property<College> COLLEGE = Property.create("college", College.class);
    public static final Property<Document> DOCUMENT = Property.create("document", Document.class);
    public static final Property<DocumentVersion> DOCUMENT_VERSION = Property.create("documentVersion", DocumentVersion.class);

    protected Long angelId;
    protected Date created;
    protected Long entityAngelId;
    protected String entityIdentifier;
    protected Long entityWillowId;
    protected Date modified;
    protected AttachmentSpecialType specialType;

    protected Object binaryInfo;
    protected Object college;
    protected Object document;
    protected Object documentVersion;

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

    public void setEntityAngelId(Long entityAngelId) {
        beforePropertyWrite("entityAngelId", this.entityAngelId, entityAngelId);
        this.entityAngelId = entityAngelId;
    }

    public Long getEntityAngelId() {
        beforePropertyRead("entityAngelId");
        return this.entityAngelId;
    }

    public void setEntityIdentifier(String entityIdentifier) {
        beforePropertyWrite("entityIdentifier", this.entityIdentifier, entityIdentifier);
        this.entityIdentifier = entityIdentifier;
    }

    public String getEntityIdentifier() {
        beforePropertyRead("entityIdentifier");
        return this.entityIdentifier;
    }

    public void setEntityWillowId(Long entityWillowId) {
        beforePropertyWrite("entityWillowId", this.entityWillowId, entityWillowId);
        this.entityWillowId = entityWillowId;
    }

    public Long getEntityWillowId() {
        beforePropertyRead("entityWillowId");
        return this.entityWillowId;
    }

    public void setModified(Date modified) {
        beforePropertyWrite("modified", this.modified, modified);
        this.modified = modified;
    }

    public Date getModified() {
        beforePropertyRead("modified");
        return this.modified;
    }

    public void setSpecialType(AttachmentSpecialType specialType) {
        beforePropertyWrite("specialType", this.specialType, specialType);
        this.specialType = specialType;
    }

    public AttachmentSpecialType getSpecialType() {
        beforePropertyRead("specialType");
        return this.specialType;
    }

    public void setBinaryInfo(BinaryInfo binaryInfo) {
        setToOneTarget("binaryInfo", binaryInfo, true);
    }

    public BinaryInfo getBinaryInfo() {
        return (BinaryInfo)readProperty("binaryInfo");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }

    public void setDocument(Document document) {
        setToOneTarget("document", document, true);
    }

    public Document getDocument() {
        return (Document)readProperty("document");
    }

    public void setDocumentVersion(DocumentVersion documentVersion) {
        setToOneTarget("documentVersion", documentVersion, true);
    }

    public DocumentVersion getDocumentVersion() {
        return (DocumentVersion)readProperty("documentVersion");
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
            case "entityAngelId":
                return this.entityAngelId;
            case "entityIdentifier":
                return this.entityIdentifier;
            case "entityWillowId":
                return this.entityWillowId;
            case "modified":
                return this.modified;
            case "specialType":
                return this.specialType;
            case "binaryInfo":
                return this.binaryInfo;
            case "college":
                return this.college;
            case "document":
                return this.document;
            case "documentVersion":
                return this.documentVersion;
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
            case "entityAngelId":
                this.entityAngelId = (Long)val;
                break;
            case "entityIdentifier":
                this.entityIdentifier = (String)val;
                break;
            case "entityWillowId":
                this.entityWillowId = (Long)val;
                break;
            case "modified":
                this.modified = (Date)val;
                break;
            case "specialType":
                this.specialType = (AttachmentSpecialType)val;
                break;
            case "binaryInfo":
                this.binaryInfo = val;
                break;
            case "college":
                this.college = val;
                break;
            case "document":
                this.document = val;
                break;
            case "documentVersion":
                this.documentVersion = val;
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
        out.writeObject(this.entityAngelId);
        out.writeObject(this.entityIdentifier);
        out.writeObject(this.entityWillowId);
        out.writeObject(this.modified);
        out.writeObject(this.specialType);
        out.writeObject(this.binaryInfo);
        out.writeObject(this.college);
        out.writeObject(this.document);
        out.writeObject(this.documentVersion);
    }

    @Override
    protected void readState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readState(in);
        this.angelId = (Long)in.readObject();
        this.created = (Date)in.readObject();
        this.entityAngelId = (Long)in.readObject();
        this.entityIdentifier = (String)in.readObject();
        this.entityWillowId = (Long)in.readObject();
        this.modified = (Date)in.readObject();
        this.specialType = (AttachmentSpecialType)in.readObject();
        this.binaryInfo = in.readObject();
        this.college = in.readObject();
        this.document = in.readObject();
        this.documentVersion = in.readObject();
    }

}
