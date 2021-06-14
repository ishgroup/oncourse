package ish.oncourse.api.test;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;



public class PreferenceApiTest {
    @Test
    public void test() {
        Results results = Runner.builder().clientFactory(ish.oncourse.api.test.client.KarateClient::new).path(  "classpath:ish/oncourse/api/test/preference").tags("~@ignore").parallel(1);
        Assertions.assertEquals(results.getFailCount(), 0, results.getErrorMessages());
    }
}
