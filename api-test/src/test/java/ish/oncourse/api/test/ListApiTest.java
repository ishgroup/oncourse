package ish.oncourse.api.test;

import com.intuit.karate.junit4.Karate;
import cucumber.api.CucumberOptions;
import org.junit.runner.RunWith;
// was divided on small features by path api/test/list/...
//@RunWith(Karate.class)
@CucumberOptions(tags = "~@ignore", features = "classpath:ish/oncourse/api/test/list")
public class ListApiTest {}
