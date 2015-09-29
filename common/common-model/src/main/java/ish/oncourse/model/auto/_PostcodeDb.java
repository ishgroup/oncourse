package ish.oncourse.model.auto;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import ish.common.types.PostcodeType;

/**
 * Class _PostcodeDb was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _PostcodeDb extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    @Deprecated
    public static final String DC_PROPERTY = "dc";
    @Deprecated
    public static final String ISH_VERSION_PROPERTY = "ishVersion";
    @Deprecated
    public static final String LAT_PROPERTY = "lat";
    @Deprecated
    public static final String LON_PROPERTY = "lon";
    @Deprecated
    public static final String POSTCODE_PROPERTY = "postcode";
    @Deprecated
    public static final String STATE_PROPERTY = "state";
    @Deprecated
    public static final String SUBURB_PROPERTY = "suburb";
    @Deprecated
    public static final String TYPE_PROPERTY = "type";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<String> DC = new Property<String>("dc");
    public static final Property<Long> ISH_VERSION = new Property<Long>("ishVersion");
    public static final Property<Double> LAT = new Property<Double>("lat");
    public static final Property<Double> LON = new Property<Double>("lon");
    public static final Property<Long> POSTCODE = new Property<Long>("postcode");
    public static final Property<String> STATE = new Property<String>("state");
    public static final Property<String> SUBURB = new Property<String>("suburb");
    public static final Property<PostcodeType> TYPE = new Property<PostcodeType>("type");

    public void setDc(String dc) {
        writeProperty("dc", dc);
    }
    public String getDc() {
        return (String)readProperty("dc");
    }

    public void setIshVersion(Long ishVersion) {
        writeProperty("ishVersion", ishVersion);
    }
    public Long getIshVersion() {
        return (Long)readProperty("ishVersion");
    }

    public void setLat(Double lat) {
        writeProperty("lat", lat);
    }
    public Double getLat() {
        return (Double)readProperty("lat");
    }

    public void setLon(Double lon) {
        writeProperty("lon", lon);
    }
    public Double getLon() {
        return (Double)readProperty("lon");
    }

    public void setPostcode(Long postcode) {
        writeProperty("postcode", postcode);
    }
    public Long getPostcode() {
        return (Long)readProperty("postcode");
    }

    public void setState(String state) {
        writeProperty("state", state);
    }
    public String getState() {
        return (String)readProperty("state");
    }

    public void setSuburb(String suburb) {
        writeProperty("suburb", suburb);
    }
    public String getSuburb() {
        return (String)readProperty("suburb");
    }

    public void setType(PostcodeType type) {
        writeProperty("type", type);
    }
    public PostcodeType getType() {
        return (PostcodeType)readProperty("type");
    }

}
