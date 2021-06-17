/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.api.test;

//import com.intuit.karate.KarateOptions;
import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;



public class CheckoutApiTest {

    @Test
    public void test() {
        Results results = Runner.builder().clientFactory(ish.oncourse.api.test.client.KarateClient::new).path( "classpath:ish/oncourse/api/test/checkout/post.feature",
                "classpath:ish/oncourse/api/test/checkout/discount/get.feature",
                "classpath:ish/oncourse/api/test/checkout/postPaymentWithCreditsAndOwings.feature",
                "classpath:ish/oncourse/api/test/checkout/postValidateAmountsOfProducts.feature").tags("~@ignore").parallel(1);
        Assertions.assertEquals(results.getFailCount(), 0, results.getErrorMessages());
    }
}
