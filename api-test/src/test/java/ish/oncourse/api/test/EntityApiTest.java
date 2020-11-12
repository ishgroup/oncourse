package ish.oncourse.api.test;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import com.intuit.karate.junit4.Karate;
import cucumber.api.CucumberOptions;
import org.junit.Test;
import org.junit.runner.RunWith;

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
        assertEquals(results.getErrorMessages(), results.getFailCount(), 0);
    }
}
