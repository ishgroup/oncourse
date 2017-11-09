package ish.oncourse.solr.functions.course

import ish.oncourse.model.Site
import ish.oncourse.solr.model.SSite

/**
 * User: akoiro
 * Date: 4/11/17
 */
class SiteFunctions {
    static final SSite getSSite(Site site) {
        SSite sSite = new SSite()
        sSite.id = site.id
        sSite.state = site.state
        sSite.suburb = site.suburb
        sSite.postcode = site.postcode
        sSite.location = site.longitude && site.latitude ? String.format("%f,%f", site.latitude, site.longitude) : null
        sSite
    }
}
