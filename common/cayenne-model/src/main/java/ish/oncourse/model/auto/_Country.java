package ish.oncourse.model.auto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import org.apache.cayenne.exp.Property;

/**
 * Class _Country was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Country extends WillowCayenneObject {

    private static final long serialVersionUID = 1L; 

    public static final String ASCCSS_CODE_PROPERTY = "asccssCode";
    public static final String CREATED_PROPERTY = "created";
    public static final String ISH_VERSION_PROPERTY = "ishVersion";
    public static final String ISO_CODE_ALPHA2_PROPERTY = "isoCodeAlpha2";
    public static final String ISO_CODE_ALPHA3_PROPERTY = "isoCodeAlpha3";
    public static final String ISO_CODE_NUMERIC_PROPERTY = "isoCodeNumeric";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String NAME_PROPERTY = "name";
    public static final String SACC_CODE_PROPERTY = "saccCode";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<String> ASCCSS_CODE = Property.create("asccssCode", String.class);
    public static final Property<Date> CREATED = Property.create("created", Date.class);
    public static final Property<Long> ISH_VERSION = Property.create("ishVersion", Long.class);
    public static final Property<String> ISO_CODE_ALPHA2 = Property.create("isoCodeAlpha2", String.class);
    public static final Property<String> ISO_CODE_ALPHA3 = Property.create("isoCodeAlpha3", String.class);
    public static final Property<Integer> ISO_CODE_NUMERIC = Property.create("isoCodeNumeric", Integer.class);
    public static final Property<Date> MODIFIED = Property.create("modified", Date.class);
    public static final Property<String> NAME = Property.create("name", String.class);
    public static final Property<Integer> SACC_CODE = Property.create("saccCode", Integer.class);

    protected String asccssCode;
    protected Date created;
    protected Long ishVersion;
    protected String isoCodeAlpha2;
    protected String isoCodeAlpha3;
    protected Integer isoCodeNumeric;
    protected Date modified;
    protected String name;
    protected Integer saccCode;


    public void setAsccssCode(String asccssCode) {
        beforePropertyWrite("asccssCode", this.asccssCode, asccssCode);
        this.asccssCode = asccssCode;
    }

    public String getAsccssCode() {
        beforePropertyRead("asccssCode");
        return this.asccssCode;
    }

    public void setCreated(Date created) {
        beforePropertyWrite("created", this.created, created);
        this.created = created;
    }

    public Date getCreated() {
        beforePropertyRead("created");
        return this.created;
    }

    public void setIshVersion(Long ishVersion) {
        beforePropertyWrite("ishVersion", this.ishVersion, ishVersion);
        this.ishVersion = ishVersion;
    }

    public Long getIshVersion() {
        beforePropertyRead("ishVersion");
        return this.ishVersion;
    }

    public void setIsoCodeAlpha2(String isoCodeAlpha2) {
        beforePropertyWrite("isoCodeAlpha2", this.isoCodeAlpha2, isoCodeAlpha2);
        this.isoCodeAlpha2 = isoCodeAlpha2;
    }

    public String getIsoCodeAlpha2() {
        beforePropertyRead("isoCodeAlpha2");
        return this.isoCodeAlpha2;
    }

    public void setIsoCodeAlpha3(String isoCodeAlpha3) {
        beforePropertyWrite("isoCodeAlpha3", this.isoCodeAlpha3, isoCodeAlpha3);
        this.isoCodeAlpha3 = isoCodeAlpha3;
    }

    public String getIsoCodeAlpha3() {
        beforePropertyRead("isoCodeAlpha3");
        return this.isoCodeAlpha3;
    }

    public void setIsoCodeNumeric(Integer isoCodeNumeric) {
        beforePropertyWrite("isoCodeNumeric", this.isoCodeNumeric, isoCodeNumeric);
        this.isoCodeNumeric = isoCodeNumeric;
    }

    public Integer getIsoCodeNumeric() {
        beforePropertyRead("isoCodeNumeric");
        return this.isoCodeNumeric;
    }

    public void setModified(Date modified) {
        beforePropertyWrite("modified", this.modified, modified);
        this.modified = modified;
    }

    public Date getModified() {
        beforePropertyRead("modified");
        return this.modified;
    }

    public void setName(String name) {
        beforePropertyWrite("name", this.name, name);
        this.name = name;
    }

    public String getName() {
        beforePropertyRead("name");
        return this.name;
    }

    public void setSaccCode(Integer saccCode) {
        beforePropertyWrite("saccCode", this.saccCode, saccCode);
        this.saccCode = saccCode;
    }

    public Integer getSaccCode() {
        beforePropertyRead("saccCode");
        return this.saccCode;
    }

    @Override
    public Object readPropertyDirectly(String propName) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch(propName) {
            case "asccssCode":
                return this.asccssCode;
            case "created":
                return this.created;
            case "ishVersion":
                return this.ishVersion;
            case "isoCodeAlpha2":
                return this.isoCodeAlpha2;
            case "isoCodeAlpha3":
                return this.isoCodeAlpha3;
            case "isoCodeNumeric":
                return this.isoCodeNumeric;
            case "modified":
                return this.modified;
            case "name":
                return this.name;
            case "saccCode":
                return this.saccCode;
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
            case "asccssCode":
                this.asccssCode = (String)val;
                break;
            case "created":
                this.created = (Date)val;
                break;
            case "ishVersion":
                this.ishVersion = (Long)val;
                break;
            case "isoCodeAlpha2":
                this.isoCodeAlpha2 = (String)val;
                break;
            case "isoCodeAlpha3":
                this.isoCodeAlpha3 = (String)val;
                break;
            case "isoCodeNumeric":
                this.isoCodeNumeric = (Integer)val;
                break;
            case "modified":
                this.modified = (Date)val;
                break;
            case "name":
                this.name = (String)val;
                break;
            case "saccCode":
                this.saccCode = (Integer)val;
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
        out.writeObject(this.asccssCode);
        out.writeObject(this.created);
        out.writeObject(this.ishVersion);
        out.writeObject(this.isoCodeAlpha2);
        out.writeObject(this.isoCodeAlpha3);
        out.writeObject(this.isoCodeNumeric);
        out.writeObject(this.modified);
        out.writeObject(this.name);
        out.writeObject(this.saccCode);
    }

    @Override
    protected void readState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readState(in);
        this.asccssCode = (String)in.readObject();
        this.created = (Date)in.readObject();
        this.ishVersion = (Long)in.readObject();
        this.isoCodeAlpha2 = (String)in.readObject();
        this.isoCodeAlpha3 = (String)in.readObject();
        this.isoCodeNumeric = (Integer)in.readObject();
        this.modified = (Date)in.readObject();
        this.name = (String)in.readObject();
        this.saccCode = (Integer)in.readObject();
    }

}
