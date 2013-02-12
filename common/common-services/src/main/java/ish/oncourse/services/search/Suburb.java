package ish.oncourse.services.search;

import org.apache.solr.common.SolrDocument;

public class Suburb{

    private String postcode;

    private Double latitude;

    private Double longitude;

    private Double distance;

    private String location;


    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getLocation()
    {
        if (location == null)
            location = String.format("%s,%s", latitude,longitude);
        return location;
    }


    public static Suburb valueOf(SolrDocument doc, Double distance)
    {
        Suburb suburb = new Suburb();
        suburb.location = (String) doc.get(SolrQueryBuilder.PARAMETER_loc);
        String[] points = suburb.location.split(",");
        suburb.latitude = Double.parseDouble(points[0]);
        suburb.longitude = Double.parseDouble(points[1]);
        suburb.postcode = (String) doc.get(SolrQueryBuilder.FIELD_postcode);
        suburb.distance = distance != null ? distance: SearchService.DEFAULT_DISTANCE;
        return suburb;
    }
}
