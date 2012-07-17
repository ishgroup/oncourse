package ish.oncourse.model.auto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;

import ish.oncourse.model.College;
import ish.oncourse.model.Country;
import ish.oncourse.model.Room;
import ish.oncourse.model.WaitingListSite;

/**
 * Class _Site was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Site extends CayenneDataObject {

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
    public static final String WAITING_LIST_SITES_PROPERTY = "waitingListSites";

    public static final String ID_PK_COLUMN = "id";

    public void setAngelId(Long angelId) {
        writeProperty(ANGEL_ID_PROPERTY, angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty(ANGEL_ID_PROPERTY);
    }

    public void setCountryId(Long countryId) {
        writeProperty(COUNTRY_ID_PROPERTY, countryId);
    }
    public Long getCountryId() {
        return (Long)readProperty(COUNTRY_ID_PROPERTY);
    }

    public void setCreated(Date created) {
        writeProperty(CREATED_PROPERTY, created);
    }
    public Date getCreated() {
        return (Date)readProperty(CREATED_PROPERTY);
    }

    public void setDrivingDirections(String drivingDirections) {
        writeProperty(DRIVING_DIRECTIONS_PROPERTY, drivingDirections);
    }
    public String getDrivingDirections() {
        return (String)readProperty(DRIVING_DIRECTIONS_PROPERTY);
    }

    public void setDrivingDirectionsTextile(String drivingDirectionsTextile) {
        writeProperty(DRIVING_DIRECTIONS_TEXTILE_PROPERTY, drivingDirectionsTextile);
    }
    public String getDrivingDirectionsTextile() {
        return (String)readProperty(DRIVING_DIRECTIONS_TEXTILE_PROPERTY);
    }

    public void setIsVirtual(Boolean isVirtual) {
        writeProperty(IS_VIRTUAL_PROPERTY, isVirtual);
    }
    public Boolean getIsVirtual() {
        return (Boolean)readProperty(IS_VIRTUAL_PROPERTY);
    }

    public void setIsWebVisible(Boolean isWebVisible) {
        writeProperty(IS_WEB_VISIBLE_PROPERTY, isWebVisible);
    }
    public Boolean getIsWebVisible() {
        return (Boolean)readProperty(IS_WEB_VISIBLE_PROPERTY);
    }

    public void setLatitude(BigDecimal latitude) {
        writeProperty(LATITUDE_PROPERTY, latitude);
    }
    public BigDecimal getLatitude() {
        return (BigDecimal)readProperty(LATITUDE_PROPERTY);
    }

    public void setLongitude(BigDecimal longitude) {
        writeProperty(LONGITUDE_PROPERTY, longitude);
    }
    public BigDecimal getLongitude() {
        return (BigDecimal)readProperty(LONGITUDE_PROPERTY);
    }

    public void setModified(Date modified) {
        writeProperty(MODIFIED_PROPERTY, modified);
    }
    public Date getModified() {
        return (Date)readProperty(MODIFIED_PROPERTY);
    }

    public void setName(String name) {
        writeProperty(NAME_PROPERTY, name);
    }
    public String getName() {
        return (String)readProperty(NAME_PROPERTY);
    }

    public void setPostcode(String postcode) {
        writeProperty(POSTCODE_PROPERTY, postcode);
    }
    public String getPostcode() {
        return (String)readProperty(POSTCODE_PROPERTY);
    }

    public void setPublicTransportDirections(String publicTransportDirections) {
        writeProperty(PUBLIC_TRANSPORT_DIRECTIONS_PROPERTY, publicTransportDirections);
    }
    public String getPublicTransportDirections() {
        return (String)readProperty(PUBLIC_TRANSPORT_DIRECTIONS_PROPERTY);
    }

    public void setPublicTransportDirectionsTextile(String publicTransportDirectionsTextile) {
        writeProperty(PUBLIC_TRANSPORT_DIRECTIONS_TEXTILE_PROPERTY, publicTransportDirectionsTextile);
    }
    public String getPublicTransportDirectionsTextile() {
        return (String)readProperty(PUBLIC_TRANSPORT_DIRECTIONS_TEXTILE_PROPERTY);
    }

    public void setSpecialInstructions(String specialInstructions) {
        writeProperty(SPECIAL_INSTRUCTIONS_PROPERTY, specialInstructions);
    }
    public String getSpecialInstructions() {
        return (String)readProperty(SPECIAL_INSTRUCTIONS_PROPERTY);
    }

    public void setSpecialInstructionsTextile(String specialInstructionsTextile) {
        writeProperty(SPECIAL_INSTRUCTIONS_TEXTILE_PROPERTY, specialInstructionsTextile);
    }
    public String getSpecialInstructionsTextile() {
        return (String)readProperty(SPECIAL_INSTRUCTIONS_TEXTILE_PROPERTY);
    }

    public void setState(String state) {
        writeProperty(STATE_PROPERTY, state);
    }
    public String getState() {
        return (String)readProperty(STATE_PROPERTY);
    }

    public void setStreet(String street) {
        writeProperty(STREET_PROPERTY, street);
    }
    public String getStreet() {
        return (String)readProperty(STREET_PROPERTY);
    }

    public void setSuburb(String suburb) {
        writeProperty(SUBURB_PROPERTY, suburb);
    }
    public String getSuburb() {
        return (String)readProperty(SUBURB_PROPERTY);
    }

    public void setTimeZone(String timeZone) {
        writeProperty(TIME_ZONE_PROPERTY, timeZone);
    }
    public String getTimeZone() {
        return (String)readProperty(TIME_ZONE_PROPERTY);
    }

    public void setCollege(College college) {
        setToOneTarget(COLLEGE_PROPERTY, college, true);
    }

    public College getCollege() {
        return (College)readProperty(COLLEGE_PROPERTY);
    }


    public void setCountry(Country country) {
        setToOneTarget(COUNTRY_PROPERTY, country, true);
    }

    public Country getCountry() {
        return (Country)readProperty(COUNTRY_PROPERTY);
    }


    public void addToRooms(Room obj) {
        addToManyTarget(ROOMS_PROPERTY, obj, true);
    }
    public void removeFromRooms(Room obj) {
        removeToManyTarget(ROOMS_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Room> getRooms() {
        return (List<Room>)readProperty(ROOMS_PROPERTY);
    }


    public void addToWaitingListSites(WaitingListSite obj) {
        addToManyTarget(WAITING_LIST_SITES_PROPERTY, obj, true);
    }
    public void removeFromWaitingListSites(WaitingListSite obj) {
        removeToManyTarget(WAITING_LIST_SITES_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<WaitingListSite> getWaitingListSites() {
        return (List<WaitingListSite>)readProperty(WAITING_LIST_SITES_PROPERTY);
    }


}
