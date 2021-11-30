/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.quartz.job.solr;

import ish.oncourse.model.College;
import ish.oncourse.model.Course;
import ish.oncourse.model.KeyStatus;
import ish.oncourse.solr.SolrCollection;
import ish.oncourse.solr.reindex.ReindexCourses;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.common.SolrDocumentList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReindexCoursesJob extends AReindexCollectionJob {

    private static Logger logger = LogManager.getLogger();

    @Override
    protected void execute0() {
        ObjectContext context = serverRuntime.newContext();
        new ReindexCourses(serverRuntime.newContext(), solrClient, true).run();

        List<Long> collegeIds = ObjectSelect.dataRowQuery(College.class)
                .where(College.COMMUNICATION_KEY_STATUS.in(KeyStatus.RESTRICTED, KeyStatus.VALID))
                .select(context).stream().map(r ->  (Long) r.get(College.ID_PK_COLUMN)).collect(Collectors.toList());

        collegeIds.forEach(collegeId -> {
            List<String> courseIds = ObjectSelect.dataRowQuery(Course.class)
                    .where(ExpressionFactory.matchDbExp(Course.COLLEGE.dot(College.ID_PK_COLUMN).getName(), collegeId))
                    .and(Course.IS_WEB_VISIBLE.isFalse())
                    .select(context).stream()
                    .map(r ->  r.get(Course.ID_PK_COLUMN).toString())
                    .collect(Collectors.toList());
            
            if (!courseIds.isEmpty()) {
                SolrQuery query = new SolrQuery();
                query.setQuery("collegeId:" + collegeId);
                query.setFilterQueries("id:(" + String.join(" ", courseIds) + ")");
                query.setFields("id");
                query.setRows(Integer.MAX_VALUE);

                try {
                    QueryRequest queryRequest = new QueryRequest(query, SolrRequest.METHOD.POST);
                    Object response = solrClient.request(queryRequest, SolrCollection.courses.name()).get("response");

                    if (response instanceof SolrDocumentList && ((SolrDocumentList) response).size() > 0) {
                        SolrDocumentList solrDocuments = (SolrDocumentList) response;
                        List<String> toDelete = new ArrayList<>();
                        solrDocuments.iterator().forEachRemaining(it -> toDelete.add(it.get("id").toString()));
                        solrClient.deleteById(SolrCollection.courses.name(), toDelete);
                        solrClient.commit(SolrCollection.courses.name());
                        solrClient.deleteByQuery(SolrCollection.classes.name(), "courseId:(" + String.join(" ", toDelete) + ")");
                        solrClient.commit(SolrCollection.classes.name());
                    }
                } catch (Exception e) {
                    logger.error(String.format("Can not delete courses from solr, college id: %d, courses: %s", collegeId, String.join(" ", courseIds)));
                    logger.catching(e);
                }
            }
        });
        
        
    }
}