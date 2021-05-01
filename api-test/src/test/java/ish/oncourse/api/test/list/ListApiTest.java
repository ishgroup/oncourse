/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.api.test.list;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;



public class ListApiTest {

    @Test
    public void test() {
        Results results = Runner.path(  "classpath:ish/oncourse/api/test/list/column",
                "classpath:ish/oncourse/api/test/list/plain",
                "classpath:ish/oncourse/api/test/list/get.feature",
                "classpath:ish/oncourse/api/test/list/getListOfEntity.feature",
                "classpath:ish/oncourse/api/test/list/getListOfEntityWithoutRights.feature",
                "classpath:ish/oncourse/api/test/list/post.feature",
                "classpath:ish/oncourse/api/test/list/postListOfEntity.feature",
                "classpath:ish/oncourse/api/test/list/postListOfEntityWithoutRights.feature").tags("~@ignore").parallel(1);
        Assertions.assertEquals(results.getFailCount(), 0, results.getErrorMessages());
    }
}
