package ish.oncourse.api.test;


import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AuditApiTest {
    @Test
    public void test() {
        Results results = Runner.builder().clientFactory(ish.oncourse.api.test.client.KarateClient::new).path("classpath:ish/oncourse/api/test/list/entity/audit/get.feature").tags("~@ignore").parallel(1);
        Assertions.assertEquals(results.getFailCount(), 0, results.getErrorMessages());
    }
}
