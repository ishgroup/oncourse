package ish.oncourse.solr

import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope
import org.apache.solr.SolrTestCaseJ4

/**
 * User: akoiro
 * Date: 4/1/18
 */
@ThreadLeakScope(ThreadLeakScope.Scope.NONE)
abstract class ASolrTest extends SolrTestCaseJ4 {
    static {
        System.setProperty("test.solr.allowed.securerandom", "NativePRNG")
        System.setProperty("tests.timezone", "Australia/Sydney")
        System.setProperty("tests.locale", "en-AU")
    }
}
