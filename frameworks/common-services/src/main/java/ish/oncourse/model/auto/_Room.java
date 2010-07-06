package ish.oncourse.model.auto;

import ish.oncourse.model.Taggable;

/**
 * Class _Room was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Room extends Taggable {

    public static final String CAPACITY_PROPERTY = "capacity";
    public static final String DIRECTIONS_PROPERTY = "directions";
    public static final String DIRECTIONS_TEXTILE_PROPERTY = "directionsTextile";
    public static final String FACILITIES_PROPERTY = "facilities";
    public static final String FACILITIES_TEXTILE_PROPERTY = "facilitiesTextile";
    public static final String NAME_PROPERTY = "name";
    public static final String SITE_ID_PROPERTY = "siteId";

    public static final String ID_PK_COLUMN = "id";

    public void setCapacity(Integer capacity) {
        writeProperty("capacity", capacity);
    }
    public Integer getCapacity() {
        return (Integer)readProperty("capacity");
    }

    public void setDirections(String directions) {
        writeProperty("directions", directions);
    }
    public String getDirections() {
        return (String)readProperty("directions");
    }

    public void setDirectionsTextile(String directionsTextile) {
        writeProperty("directionsTextile", directionsTextile);
    }
    public String getDirectionsTextile() {
        return (String)readProperty("directionsTextile");
    }

    public void setFacilities(String facilities) {
        writeProperty("facilities", facilities);
    }
    public String getFacilities() {
        return (String)readProperty("facilities");
    }

    public void setFacilitiesTextile(String facilitiesTextile) {
        writeProperty("facilitiesTextile", facilitiesTextile);
    }
    public String getFacilitiesTextile() {
        return (String)readProperty("facilitiesTextile");
    }

    public void setName(String name) {
        writeProperty("name", name);
    }
    public String getName() {
        return (String)readProperty("name");
    }

    public void setSiteId(Long siteId) {
        writeProperty("siteId", siteId);
    }
    public Long getSiteId() {
        return (Long)readProperty("siteId");
    }

}
