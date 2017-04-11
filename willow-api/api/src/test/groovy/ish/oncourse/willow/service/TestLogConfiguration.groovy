package ish.oncourse.willow.service

import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TestLogConfiguration {
    final static  Logger logger = LoggerFactory.getLogger(TestLogConfiguration.class)

    @Test
    void test() {
        logger.warn("TEST WARN")
    }
}
