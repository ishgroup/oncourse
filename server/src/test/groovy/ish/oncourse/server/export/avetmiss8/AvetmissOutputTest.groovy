/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.export.avetmiss8

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test

import static ish.oncourse.server.export.avetmiss8.AvetmissOutput.avetmissPostCodeID

@CompileStatic
class AvetmissOutputTest {
    @Test
    void testAppend() throws Exception {

        AvetmissOutput line = new AvetmissOutput()
        line.append(2, null as String)
        assert line.toString() == "  "

        line = new AvetmissOutput()
        line.append(2, null as String, ' ')
        assert line.toString() == "  "

        line = new AvetmissOutput()
        line.append(2, null as Integer)
        assert line.toString() == "@@"

        line = new AvetmissOutput()
        line.append(2, 0)
        assert line.toString() == "00"
    }

    @Test
    void testAvetmissPostCodeID() {
        assert avetmissPostCodeID('12345') == "99"

        assert avetmissPostCodeID('') == "99"

        assert avetmissPostCodeID('2618') == "08"

        assert avetmissPostCodeID('2000') == "01"
        assert avetmissPostCodeID('3560') == "02"
        assert avetmissPostCodeID('3000') == "02"

        assert avetmissPostCodeID("0810") == "07"
        assert avetmissPostCodeID("810") == "07"
    }
}
