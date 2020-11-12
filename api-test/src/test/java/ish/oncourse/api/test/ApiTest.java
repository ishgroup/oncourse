/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.api.test;

// uncomment class and run for test all features at one time

import com.intuit.karate.KarateOptions;
import com.intuit.karate.junit4.Karate;
import org.junit.runner.RunWith;

//@RunWith(Karate.class)
@KarateOptions(tags = "~@ignore", features = {"classpath:ish/oncourse/api/test/checkout/discount"
})
public class ApiTest {
}