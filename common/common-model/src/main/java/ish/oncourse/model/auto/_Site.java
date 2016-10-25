package ish.oncourse.model.auto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import ish.oncourse.model.College;
import ish.oncourse.model.Country;
import ish.oncourse.model.Room;
import ish.oncourse.model.SystemUser;
import ish.oncourse.model.WaitingListSite;

/**
 * Class _Site was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Site extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String COUNTRY_ID_PROPERTY = "countryId";
    public static final String CREATED_PROPERTY = "created";
    public static final String DRIVING_DIRECTIONS_PROPERTY = "drivingDirections";
    public static final String DRIVING_DIRECTIONS_TEXTILE_PROPERTY = "drivingDirectionsTextile";
    public static final String IS_VIRTUAL_PROPERTY = "isVirtual";
    public static final String IS_WEB_VISIBLE_PROPERTY = "isWebVisible";
    public static final String LATITUDE_PROPERTY = "latitude";
    public static final String LONGITUDE_PROPERTY = "longitude";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String NAME_PROPERTY = "name";
    public static final String POSTCODE_PROPERTY = "postcode";
    public static final String PUBLIC_TRANSPORT_DIRECTIONS_PROPERTY = "publicTransportDirections";
    public static final String PUBLIC_TRANSPORT_DIRECTIONS_TEXTILE_PROPERTY = "publicTransportDirectionsTextile";
    public static final String SPECIAL_INSTRUCTIONS_PROPERTY = "specialInstructions";
    public static final String SPECIAL_INSTRUCTIONS_TEXTILE_PROPERTY = "specialInstructionsTextile";
    public static final String STATE_PROPERTY = "state";
    public static final String STREET_PROPERTY = "street";
    public static final String SUBURB_PROPERTY = "suburb";
    public static final String TIME_ZONE_PROPERTY = "timeZone";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String COUNTRY_PROPERTY = "country";
    public static final String ROOMS_PROPERTY = "rooms";
    public static final String SYSTEM_USERS_PROPERTY = "systemUsers";
    public static final String WAITING_LIST_SITES_PROPERTY = "waitingListSites";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Long> ANGEL_ID = new Property<Long>("angelId");
    public static final Property<Long> COUNTRY_ID = new Property<Long>("countryId");
    public static final Property<Date> CREATED = new Property<Date>("created");
    public static final Property<String> DRIVING_DIRECTIONS = new Property<String>("drivingDirections");
    public static final Property<String> DRIVING_DIRECTIONS_TEXTILE = new Property<String>("drivingDirectionsTextile");
    public static final Property<Boolean> IS_VIRTUAL = new Property<Boolean>("isVirtual");
    public static final Property<Boolean> IS_WEB_VISIBLE = new Property<Boolean>("isWebVisible");
    public static final Property<BigDecimal> LATITUDE = new Property<BigDecimal>("latitude");
    public static final Property<BigDecimal> LONGITUDE = new Property<BigDecimal>("longitude");
    public static final Property<Date> MODIFIED = new Property<Date>("modified");
    public static final Property<String> NAME = new Property<String>("name");
    public static final Property<String> POSTCODE = new Property<String>("postcode");
    public static final Property<String> PUBLIC_TRANSPORT_DIRECTIONS = new Property<String>("publicTransportDirections");
    public static final Property<String> PUBLIC_TRANSPORT_DIRECTIONS_TEXTILE = new Property<String>("publicTransportDirectionsTextile");
    public static final Property<String> SPECIAL_INSTRUCTIONS = new Property<String>("specialInstructions");
    public static final Property<String> SPECIAL_INSTRUCTIONS_TEXTILE = new Property<String>("specialInstructionsTextile");
    public static final Property<String> STATE = new Property<String>("state");
    public static final Property<String> STREET = new Property<String>("street");
    public static final Property<String> SUBURB = new Property<String>("suburb");
    public static final Property<String> TIME_ZONE = new Property<String>("timeZone");
    public static final Property<College> COLLEGE = new Property<College>("college");
    public static final Property<Country> COUNTRY = new Property<Country>("country");
    public static final Property<List<Room>> ROOMS = new Property<List<Room>>("rooms");
    public static final Property<List<SystemUser>> SYSTEM_USERS = new Property<List<SystemUser>>("systemUsers");
    public static final Property<List<WaitingListSite>> WAITING_LIST_SITES = new Property<List<WaitingListSite>>("waitingListSites");

    public void setAngelId(Long angelId) {
        writeProperty("angelId", angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty("angelId");
    }

    public void setCountryId(Long countryId) {
        writeProperty("countryId", countryId);
    }
    public Long getCountryId() {
        return (Long)readProperty("countryId");
    }

    public void setCreated(Date created) {
        writeProperty("created", created);
    }
    public Date getCreated() {
        return (Date)readProperty("created");
    }

    public void setDrivingDirections(String drivingDirections) {
        writeProperty("drivingDirections", drivingDirections);
    }
    public String getDrivingDirections() {
        return (String)readProperty("drivingDirections");
    }

    public void setDrivingDirectionsTextile(String drivingDirectionsTextile) {
        writeProperty("drivingDirectionsTextile", drivingDirectionsTextile);
    }
    public String getDrivingDirectionsTextile() {
        return (String)readProperty("drivingDirectionsTextile");
    }

    public void setIsVirtual(Boolean isVirtual) {
        writeProperty("isVirtual", isVirtual);
    }
    public Boolean getIsVirtual() {
        return (Boolean)readProperty("isVirtual");
    }

    public void setIsWebVisible(Boolean isWebVisible) {
        writeProperty("isWebVisible", isWebVisible);
    }
    public Boolean getIsWebVisible() {
        return (Boolean)readProperty("isWebVisible");
    }

    public void setLatitude(BigDecimal latitude) {
        writeProperty("latitude", latitude);
    }
    public BigDecimal getLatitude() {
        return (BigDecimal)readProperty("latitude");
    }

    public void setLongitude(BigDecimal longitude) {
        writeProperty("longitude", longitude);
    }
    public BigDecimal getLongitude() {
        return (BigDecimal)readProperty("longitude");
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

    public void setPostcode(String postcode) {
        writeProperty("postcode", postcode);
    }
    public String getPostcode() {
        return (String)readProperty("postcode");
    }

    public void setPublicTransportDirections(String publicTransportDirections) {
        writeProperty("publicTransportDirections", publicTransportDirections);
    }
    public String getPublicTransportDirections() {
        return (String)readProperty("publicTransportDirections");
    }

    public void setPublicTransportDirectionsTextile(String publicTransportDirectionsTextile) {
        writeProperty("publicTransportDirectionsTextile", publicTransportDirectionsTextile);
    }
    public String getPublicTransportDirectionsTextile() {
        return (String)readProperty("publicTransportDirectionsTextile");
    }

    public void setSpecialInstructions(String specialInstructions) {
        writeProperty("specialInstructions", specialInstructions);
    }
    public String getSpecialInstructions() {
        return (String)readProperty("specialInstructions");
    }

    public void setSpecialInstructionsTextile(String specialInstructionsTextile) {
        writeProperty("specialInstructionsTextile", specialInstructionsTextile);
    }
    public String getSpecialInstructionsTextile() {
        return (String)readProperty("specialInstructionsTextile");
    }

    public void setState(String state) {
        writeProperty("state", state);
    }
    public String getState() {
        return (String)readProperty("state");
    }

    public void setStreet(String street) {
        writeProperty("street", street);
    }
    public String getStreet() {
        return (String)readProperty("street");
    }

    public void setSuburb(String suburb) {
        writeProperty("suburb", suburb);
    }
    public String getSuburb() {
        return (String)readProperty("suburb");
    }

    public void setTimeZone(String timeZone) {
        writeProperty("timeZone", timeZone);
    }
    public String getTimeZone() {
        return (String)readProperty("timeZone");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void setCountry(Country country) {
        setToOneTarget("country", country, true);
    }

    public Country getCountry() {
        return (Country)readProperty("country");
    }


    public void addToRooms(Room obj) {
        addToManyTarget("rooms", obj, true);
    }
    public void removeFromRooms(Room obj) {
        removeToManyTarget("rooms", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Room> getRooms() {
        return (List<Room>)readProperty("rooms");
    }


    public void addToSystemUsers(SystemUser obj) {
        addToManyTarget("systemUsers", obj, true);
    }
    public void removeFromSystemUsers(SystemUser obj) {
        removeToManyTarget("systemUsers", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<SystemUser> getSystemUsers() {
        return (List<SystemUser>)readProperty("systemUsers");
    }


    public void addToWaitingListSites(WaitingListSite obj) {
        addToManyTarget("waitingListSites", obj, true);
    }
    public void removeFromWaitingListSites(WaitingListSite obj) {
        removeToManyTarget("waitingListSites", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<WaitingListSite> getWaitingListSites() {
        return (List<WaitingListSite>)readProperty("waitingListSites");
    }


}
