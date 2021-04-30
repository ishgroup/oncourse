package ish.oncourse.api.test;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import static org.junit.Assert.assertEquals;


public class EntityApiTest {

    @Test
    public void test() {
        Results results = Runner.path("classpath:ish/oncourse/api/test/createEntity.feature",
                "classpath:ish/oncourse/api/test/deleteEntityWithoutAccessRights.feature",
                "classpath:ish/oncourse/api/test/getAccessWithoutAccessRights.feature",
                "classpath:ish/oncourse/api/test/getEntityWithoutAccessRights.feature",
                "classpath:ish/oncourse/api/test/postEntityWithoutAccessRights.feature",
                "classpath:ish/oncourse/api/test/removeEntity.feature",
                "classpath:ish/oncourse/api/test/removeEntityById.feature",
                "classpath:ish/oncourse/api/test/signIn.feature").tags("~@ignore").parallel(1);
        Assertions.assertEquals(results.getFailCount(), 0, results.getErrorMessages());
    }
}
