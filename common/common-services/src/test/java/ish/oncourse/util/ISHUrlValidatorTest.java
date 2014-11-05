package ish.oncourse.util;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

public class ISHUrlValidatorTest {
    @Test
    public void test() {
       ISHUrlValidator validator = new ISHUrlValidator();

        String[] paths = new String[]{"/courses/centre+for+continuing+education+(cce)/arts+and+humanities/creative+arts/music",
                "/courses/centre+for+continuing+education+(cce)/arts+and+humanities/creative+arts/visual+arts",
                "/courses/centre+for+continuing+education+(cce)/arts+and+humanities/languages+and+culture/german",
                "/courses/centre+for+continuing+education+(cce)/complementary+studies/study+skills",
                "/courses/centre+for+continuing+education+(cce)/continuing+professional+education/accounting",
                "/courses/Centre+for+Continuing+Education+(CCE)/University+Preparation+Courses+(UPC)",
                "/courses/centre+for+continuing+education/information+technology/web+development/adobe+dreamweaver,+fireworks+and+flash"};

        for (String path : paths) {
            assertTrue(validator.isValidOnlyPath(path));
        }
    }
}
