package ish.oncourse.ui.pages.internal;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

import java.util.Collections;
import java.util.List;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class SolrDebugInfo {
    @Parameter
    @Property
    private Object debugInfo;

    @Property
    private String  debugInfoHtml;

    @SetupRender
    public void setupRender() {
        Format format = new Format();
        format.debugInfo = debugInfo;
        debugInfoHtml = format.format();
    }

    public static class Format {
        private Object debugInfo;

        public String format() {
            String template = "<div class='solr-debug-info'>\n" +
                    "\t<div class='description'>%s</div>\n" +
                    "\t<div class='value'>%s</div>\n" +
                    "</div>\n";

            String result = String.format(template,getDescription(), getValue());

            List details = getDetails();
            if (details.size() > 0) {
                result += "<div class=details>\n";
                for (Object detail : details) {
                    Format format = new Format();
                    format.debugInfo = detail;
                    result += format.format();
                }
                result += "</div>\n";
            }
            return result;
        }

        public String getValue() {
            return getValueBy("value");
        }

        public String getDescription() {
            return getValueBy("description");
        }

        public List getDetails() {
            if (debugInfo instanceof SimpleOrderedMap) {
                Object value = ((SimpleOrderedMap) debugInfo).get("details");
                return value instanceof List ? (List) value : Collections.emptyList();
            } else {
                return Collections.emptyList();
            }
        }

        private String getValueBy(String key) {
            if (debugInfo instanceof SimpleOrderedMap) {
                Object value = ((SimpleOrderedMap) debugInfo).get(key);
                return value != null ? value.toString() : StringUtils.EMPTY;
            } else {
                return StringUtils.EMPTY;
            }
        }
    }

}
