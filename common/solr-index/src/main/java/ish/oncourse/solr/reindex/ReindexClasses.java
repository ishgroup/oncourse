/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.solr.reindex;

import ish.oncourse.model.WebSite;
import ish.oncourse.solr.SolrCollection;
import ish.oncourse.solr.functions.course.CourseFunctions;
import ish.oncourse.solr.functions.course.SCourseFunctions;
import ish.oncourse.solr.model.SCourseClass;
import org.apache.cayenne.ObjectContext;
import org.apache.solr.client.solrj.SolrClient;

public class ReindexClasses extends ReindexCollection<SCourseClass> {

    public ReindexClasses(ObjectContext objectContext, SolrClient solrClient, WebSite webSite) {
        super(solrClient, SolrCollection.classes, SCourseFunctions.SClasses(() -> CourseFunctions.CoursesByWebSite.call(objectContext, webSite.getCollege()), webSite));
    }
    
}
