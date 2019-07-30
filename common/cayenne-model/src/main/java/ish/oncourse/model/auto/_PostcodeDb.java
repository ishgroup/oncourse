package ish.oncourse.model.auto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.cayenne.exp.Property;

import ish.common.types.PostcodeType;

/**
 * Class _PostcodeDb was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _PostcodeDb extends WillowCayenneObject {

    private static final long serialVersionUID = 1L; 

    public static final String DC_PROPERTY = "dc";
    public static final String ISH_VERSION_PROPERTY = "ishVersion";
    public static final String LAT_PROPERTY = "lat";
    public static final String LON_PROPERTY = "lon";
    public static final String POSTCODE_PROPERTY = "postcode";
    public static final String STATE_PROPERTY = "state";
    public static final String SUBURB_PROPERTY = "suburb";
    public static final String TYPE_PROPERTY = "type";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<String> DC = Property.create("dc", String.class);
    public static final Property<Long> ISH_VERSION = Property.create("ishVersion", Long.class);
    public static final Property<Double> LAT = Property.create("lat", Double.class);
    public static final Property<Double> LON = Property.create("lon", Double.class);
    public static final Property<Long> POSTCODE = Property.create("postcode", Long.class);
    public static final Property<String> STATE = Property.create("state", String.class);
    public static final Property<String> SUBURB = Property.create("suburb", String.class);
    public static final Property<PostcodeType> TYPE = Property.create("type", PostcodeType.class);

    protected String dc;
    protected Long ishVersion;
    protected Double lat;
    protected Double lon;
    protected Long postcode;
    protected String state;
    protected String suburb;
    protected PostcodeType type;


    public void setDc(String dc) {
        beforePropertyWrite("dc", this.dc, dc);
        this.dc = dc;
    }

    public String getDc() {
        beforePropertyRead("dc");
        return this.dc;
    }

    public void setIshVersion(Long ishVersion) {
        beforePropertyWrite("ishVersion", this.ishVersion, ishVersion);
        this.ishVersion = ishVersion;
    }

    public Long getIshVersion() {
        beforePropertyRead("ishVersion");
        return this.ishVersion;
    }

    public void setLat(Double lat) {
        beforePropertyWrite("lat", this.lat, lat);
        this.lat = lat;
    }

    public Double getLat() {
        beforePropertyRead("lat");
        return this.lat;
    }

    public void setLon(Double lon) {
        beforePropertyWrite("lon", this.lon, lon);
        this.lon = lon;
    }

    public Double getLon() {
        beforePropertyRead("lon");
        return this.lon;
    }

    public void setPostcode(Long postcode) {
        beforePropertyWrite("postcode", this.postcode, postcode);
        this.postcode = postcode;
    }

    public Long getPostcode() {
        beforePropertyRead("postcode");
        return this.postcode;
    }

    public void setState(String state) {
        beforePropertyWrite("state", this.state, state);
        this.state = state;
    }

    public String getState() {
        beforePropertyRead("state");
        return this.state;
    }

    public void setSuburb(String suburb) {
        beforePropertyWrite("suburb", this.suburb, suburb);
        this.suburb = suburb;
    }

    public String getSuburb() {
        beforePropertyRead("suburb");
        return this.suburb;
    }

    public void setType(PostcodeType type) {
        beforePropertyWrite("type", this.type, type);
        this.type = type;
    }

    public PostcodeType getType() {
        beforePropertyRead("type");
        return this.type;
    }

    @Override
    public Object readPropertyDirectly(String propName) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch(propName) {
            case "dc":
                return this.dc;
            case "ishVersion":
                return this.ishVersion;
            case "lat":
                return this.lat;
            case "lon":
                return this.lon;
            case "postcode":
                return this.postcode;
            case "state":
                return this.state;
            case "suburb":
                return this.suburb;
            case "type":
                return this.type;
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
            case "dc":
                this.dc = (String)val;
                break;
            case "ishVersion":
                this.ishVersion = (Long)val;
                break;
            case "lat":
                this.lat = (Double)val;
                break;
            case "lon":
                this.lon = (Double)val;
                break;
            case "postcode":
                this.postcode = (Long)val;
                break;
            case "state":
                this.state = (String)val;
                break;
            case "suburb":
                this.suburb = (String)val;
                break;
            case "type":
                this.type = (PostcodeType)val;
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
        out.writeObject(this.dc);
        out.writeObject(this.ishVersion);
        out.writeObject(this.lat);
        out.writeObject(this.lon);
        out.writeObject(this.postcode);
        out.writeObject(this.state);
        out.writeObject(this.suburb);
        out.writeObject(this.type);
    }

    @Override
    protected void readState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readState(in);
        this.dc = (String)in.readObject();
        this.ishVersion = (Long)in.readObject();
        this.lat = (Double)in.readObject();
        this.lon = (Double)in.readObject();
        this.postcode = (Long)in.readObject();
        this.state = (String)in.readObject();
        this.suburb = (String)in.readObject();
        this.type = (PostcodeType)in.readObject();
    }

}
