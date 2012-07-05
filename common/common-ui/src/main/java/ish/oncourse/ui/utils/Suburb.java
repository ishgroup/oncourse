package ish.oncourse.ui.utils;

import ish.oncourse.model.SearchParam;
import ish.oncourse.services.search.SearchService;
import ish.oncourse.services.search.SolrQueryBuilder;
import org.apache.solr.common.SolrDocument;

import java.util.Map;

public class Suburb{

    private String postcode;

    private Double latitude;

    private Double longitude;

    private Double distance;


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


    public static Suburb valueOf(SolrDocument doc, Map<SearchParam, Object> searchParams) throws NumberFormatException
    {
        Suburb suburb = new Suburb();
        String[] points = ((String) doc.get(SolrQueryBuilder.PARAMETER_loc)).split(",");
        suburb.latitude = Double.parseDouble(points[0]);
        suburb.longitude = Double.parseDouble(points[1]);
        suburb.postcode = (String) doc.get(SolrQueryBuilder.FIELD_postcode);
        suburb.distance = searchParams.containsKey(SearchParam.km) ? (Double)searchParams.get(SearchParam.km): SearchService.MAX_DISTANCE;
        return suburb;
    }
}
