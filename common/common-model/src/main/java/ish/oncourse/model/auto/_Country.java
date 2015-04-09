package ish.oncourse.model.auto;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import java.util.Date;

/**
 * Class _Country was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Country extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    @Deprecated
    public static final String ASCCSS_CODE_PROPERTY = "asccssCode";
    @Deprecated
    public static final String CREATED_PROPERTY = "created";
    @Deprecated
    public static final String ISH_VERSION_PROPERTY = "ishVersion";
    @Deprecated
    public static final String ISO_CODE_ALPHA2_PROPERTY = "isoCodeAlpha2";
    @Deprecated
    public static final String ISO_CODE_ALPHA3_PROPERTY = "isoCodeAlpha3";
    @Deprecated
    public static final String ISO_CODE_NUMERIC_PROPERTY = "isoCodeNumeric";
    @Deprecated
    public static final String MODIFIED_PROPERTY = "modified";
    @Deprecated
    public static final String NAME_PROPERTY = "name";
    @Deprecated
    public static final String SACC_CODE_PROPERTY = "saccCode";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<String> ASCCSS_CODE = new Property<String>("asccssCode");
    public static final Property<Date> CREATED = new Property<Date>("created");
    public static final Property<Long> ISH_VERSION = new Property<Long>("ishVersion");
    public static final Property<String> ISO_CODE_ALPHA2 = new Property<String>("isoCodeAlpha2");
    public static final Property<String> ISO_CODE_ALPHA3 = new Property<String>("isoCodeAlpha3");
    public static final Property<Integer> ISO_CODE_NUMERIC = new Property<Integer>("isoCodeNumeric");
    public static final Property<Date> MODIFIED = new Property<Date>("modified");
    public static final Property<String> NAME = new Property<String>("name");
    public static final Property<Integer> SACC_CODE = new Property<Integer>("saccCode");

    public void setAsccssCode(String asccssCode) {
        writeProperty("asccssCode", asccssCode);
    }
    public String getAsccssCode() {
        return (String)readProperty("asccssCode");
    }

    public void setCreated(Date created) {
        writeProperty("created", created);
    }
    public Date getCreated() {
        return (Date)readProperty("created");
    }

    public void setIshVersion(Long ishVersion) {
        writeProperty("ishVersion", ishVersion);
    }
    public Long getIshVersion() {
        return (Long)readProperty("ishVersion");
    }

    public void setIsoCodeAlpha2(String isoCodeAlpha2) {
        writeProperty("isoCodeAlpha2", isoCodeAlpha2);
    }
    public String getIsoCodeAlpha2() {
        return (String)readProperty("isoCodeAlpha2");
    }

    public void setIsoCodeAlpha3(String isoCodeAlpha3) {
        writeProperty("isoCodeAlpha3", isoCodeAlpha3);
    }
    public String getIsoCodeAlpha3() {
        return (String)readProperty("isoCodeAlpha3");
    }

    public void setIsoCodeNumeric(Integer isoCodeNumeric) {
        writeProperty("isoCodeNumeric", isoCodeNumeric);
    }
    public Integer getIsoCodeNumeric() {
        return (Integer)readProperty("isoCodeNumeric");
    }

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setName(String name) {
        writeProperty("name", name);
    }
    public String getName() {
        return (String)readProperty("name");
    }

    public void setSaccCode(Integer saccCode) {
        writeProperty("saccCode", saccCode);
    }
    public Integer getSaccCode() {
        return (Integer)readProperty("saccCode");
    }

}
