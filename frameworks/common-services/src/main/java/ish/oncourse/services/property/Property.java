package ish.oncourse.services.property;

/**
 * Defines common property keys.
 */
public enum Property {

	CustomComponentsPath("CustomComponentsPath"), SolrServer("solr.server");

    private String value;

    private Property(String value) {
       this.value = value;
    }

    public String value() {
        return value;
    }
}
