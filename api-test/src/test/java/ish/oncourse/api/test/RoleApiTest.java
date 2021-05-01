package ish.oncourse.api.test;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;



public class RoleApiTest {
    @Test
    public void test() {
        Results results = Runner.path(  "classpath:ish/oncourse/api/test/role").tags("~@ignore").parallel(1);
        Assertions.assertEquals(results.getFailCount(), 0, results.getErrorMessages());
    }
}
