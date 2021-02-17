package ish.oncourse.solr.query;

import org.apache.solr.common.SolrDocument;

public class Suburb {
	/**
	 * Default value which should be used if no distance specified.
	 */
	public static final double DEFAULT_DISTANCE = 100.0;

	private String identifier;

    private String path;

    private String postcode;

    private Double latitude;

    private Double longitude;

    private Double distance;

    private String suburb;
    
    private String display;


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


    public String getCoordinates() {
        return String.format("%s,%s", latitude, longitude);
    }

    public String getSuburb() {
        return suburb;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getPath() {
        return path;
    }

    public String getDisplay() {
        return display;
    }


    public static Suburb valueOf(String postcode, Double latitude, Double longitude, Double distance) {
        Suburb suburb = new Suburb();
        suburb.postcode = postcode;
        suburb.latitude = latitude;
        suburb.longitude = longitude;
        suburb.distance = distance;
        return suburb;
    }

    public static Suburb valueOf(String path, Double distance, SolrDocument doc) {
        return Suburb.valueOf(path, null, null, distance, doc, "");
    }

    public static Suburb valueOf(String path, String suburb, String postcode, Double distance, SolrDocument doc, String display) {
        String location = (String) doc.get(SolrQueryBuilder.PARAMETER_loc);
        String[] points = location.split(",");
        double latitude = Double.parseDouble(points[0]);
        double longitude = Double.parseDouble(points[1]);

        postcode = postcode != null ? postcode : (String) doc.get(SolrQueryBuilder.FIELD_postcode);
        suburb = suburb != null ? suburb: (String) doc.get(SolrQueryBuilder.FIELD_suburb);

        Suburb result = new Suburb();
        result.path = path;
        result.suburb = suburb;
        result.postcode = postcode;
        result.identifier = path.replaceAll("[^A-Za-z0-9]", "_");
        
        result.display = display;

        result.latitude = latitude;
        result.longitude = longitude;

        result.distance = distance != null ? distance : DEFAULT_DISTANCE;
        return result;
    }

    public static Suburb valueOf(String path, String suburb, String postcode, Double distance) {
        Suburb result = new Suburb();
        result.path = path;
        result.identifier = path.replaceAll("[^A-Za-z0-9]", "_");
        result.suburb = suburb;
        result.postcode = postcode;
        result.distance = distance != null ? distance : DEFAULT_DISTANCE;
        return result;
    }

}
