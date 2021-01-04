/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.api.test.list.entity;


import com.intuit.karate.KarateOptions;
import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import com.intuit.karate.junit4.Karate;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

public class ContactMergeApiTest {
    @Test
    public void test() {
        Results results = Runner.path(  "classpath:ish/oncourse/api/test/list/entity/contact/merge/get.feature", "classpath:ish/oncourse/api/test/list/entity/contact/merge/post.feature").tags("~@ignore").parallel(1);
        assertEquals(results.getErrorMessages(), results.getFailCount(), 0);
    }
}
