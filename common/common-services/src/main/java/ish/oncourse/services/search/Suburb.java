package ish.oncourse.services.search;

import org.apache.solr.common.SolrDocument;

public class Suburb{

    private String postcode;

    private Double latitude;

    private Double longitude;

    private Double distance;

    private String suburb;


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

    public String getSuburb()
    {
        if (suburb == null)
            suburb = String.format("%s,%s", latitude,longitude);
        return suburb;
    }


    public static Suburb valueOf(SolrDocument doc, Double distance)
    {
        Suburb suburb = new Suburb();
        suburb.suburb = (String) doc.get(SolrQueryBuilder.PARAMETER_loc);
        String[] points = suburb.suburb.split(",");
        suburb.latitude = Double.parseDouble(points[0]);
        suburb.longitude = Double.parseDouble(points[1]);
        suburb.postcode = (String) doc.get(SolrQueryBuilder.FIELD_postcode);
        suburb.distance = distance != null ? distance: SearchService.DEFAULT_DISTANCE;
        return suburb;
    }

    public static Suburb valueOf(String suburb, String postcode , Double distance)
    {
        Suburb result = new Suburb();
        result.suburb = suburb;
        result.postcode = postcode;
        result.distance = distance != null ? distance: SearchService.DEFAULT_DISTANCE;
        return result;
    }

}
