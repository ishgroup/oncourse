/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.api.test.list.entity;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import ish.oncourse.api.test.client.KarateClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FacultyApiTest {
    @Test
    public void test() {
        Results results = Runner.builder().clientFactory(KarateClient::new).path("classpath:ish/oncourse/api/test/list/entity/faculty").tags("~@ignore").parallel(1);
        Assertions.assertEquals(results.getFailCount(), 0, results.getErrorMessages());
    }
}
