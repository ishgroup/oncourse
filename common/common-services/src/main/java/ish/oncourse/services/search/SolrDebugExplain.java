package ish.oncourse.services.search;

import java.util.Map;
import java.util.Set;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class SolrDebugExplain {
    private Map<String, String> explainMap;

    public String get() {
        StringBuilder result = new StringBuilder();
        Set<Map.Entry<String, String>> entries = explainMap.entrySet();
        for (Map.Entry<String, String> next : entries) {
            //result.append(String.format("<div>courseId:%s</div>\n", next.));
        }
        return result.toString();
    }

    public static SolrDebugExplain valueOf(Map<String, String> explainMap) {
        SolrDebugExplain result = new SolrDebugExplain();
        result.explainMap = explainMap;
        return result;
    }

}
