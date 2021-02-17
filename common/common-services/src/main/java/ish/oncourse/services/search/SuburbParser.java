package ish.oncourse.services.search;

import ish.oncourse.solr.query.Suburb;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class SuburbParser {
    private String near;
    private String km;

    private String identifier;
    private String suburb;
    private String postcode;
    private Double distance;
    private String display;

    private Suburb result;
    private ISearchService searchService;

    public Suburb parse() {
        if (near.contains("/")) {
            parseLocation();
        } else {
            parseNear();
        }
        return result;
    }


    private void parseLocation() {
        String[] values = near.split("/");
        if (values.length >= 1) {
            identifier = near;
            suburb = values[0];
            if (values.length >= 2) {
                postcode = values[1];
            }
            if (values.length >= 3) {
                distance = parseKm(values[2]);
            }
            if (values.length >= 4) {
                display = values[3];
            }

            SolrDocumentList solrSuburbs;
            if (postcode != null) {
                solrSuburbs = searchService.searchSuburb(String.format("%s %s", suburb, postcode));
            } else {
                solrSuburbs = searchService.searchSuburb(suburb);
            }

            if (solrSuburbs.size() > 0) {
                SolrDocument doc = solrSuburbs.get(0);
                result = Suburb.valueOf(identifier, suburb, postcode, distance, doc, display);
            }
        }
    }

    private void parseNear() {
        identifier = near.replaceAll("[^A-Za-z0-9]", "_");
        SolrDocumentList solrSuburbs = searchService.searchSuburb(convertPostcodeParameterToLong(near));

        if (solrSuburbs.size() > 0)
            result = Suburb.valueOf(identifier, parseKm(km), solrSuburbs.get(0));
    }


    public static SuburbParser valueOf(String near, String km, ISearchService searchService) {
        SuburbParser suburbParser = new SuburbParser();
        suburbParser.near = near;
        suburbParser.km = km;
        suburbParser.searchService = searchService;
        return suburbParser;
    }


    public static Double parseKm(String parameter) {
        if (StringUtils.isNumeric(parameter)) {
            Double km = Double.valueOf(parameter);
            if (km != null) {
                if (SearchService.MAX_DISTANCE < km) {
                    //check for higher distance
                    km = SearchService.MAX_DISTANCE;
                } else if (km < SearchService.MIN_DISTANCE) {
                    //check for lower distance
                    km = SearchService.MIN_DISTANCE;
                }
            }
            return km;
        }
        return null;
    }


    public static String convertPostcodeParameterToLong(String parameter) {
        //the workaround is for #17051. Till postcode stored as the long in db and indexed as is we need to call String-to-Long and back conversion
        //to be able found the postcodes which starts from 0
        if (StringUtils.isNumeric(parameter)) {
            parameter = Long.valueOf(parameter).toString();
        }
        return parameter;
    }
}
