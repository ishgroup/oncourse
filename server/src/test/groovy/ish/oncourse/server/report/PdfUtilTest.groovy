/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.server.report

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import ish.IshTestCase
import org.apache.commons.io.FileUtils
import org.junit.jupiter.api.Test


@CompileStatic
class PdfUtilTest extends IshTestCase {

    private static File original = getResourceAsFile("resources/schema/referenceData/reports/LoremIpsumPdf.pdf")
    private static File overlay2page = getResourceAsFile("resources/schema/referenceData/reports/overlay2page.pdf")
    private static File overlay1page = getResourceAsFile("resources/schema/referenceData/reports/overlay1page.pdf")

    
    @Test
    void testOverlayFail2() {
        try {
            PdfUtil.overlayPDFs(null, overlay1page)
        } catch (Exception e) {
            return
        }
        fail("an exception should be thrown!")
    }

    
    @Test
    void testOverlayMultipage() throws Exception {

        File testingCopy = new File(original.getParentFile().getAbsoluteFile(), "testingCopy1.pdf")
        FileUtils.copyFile(original, testingCopy)

        PdfUtil.overlayPDFs(testingCopy, overlay2page)

        //so far there is no better way of testing the overlay
        Assertions.assertTrue("new file should be larger than original", testingCopy.length() > original.length())

        testingCopy.delete()
    }

    
    @Test
    void testOverlayOnepage() throws Exception {
        File testingCopy = new File(original.getParentFile().getAbsoluteFile(), "testingCopy2.pdf")
        FileUtils.copyFile(original, testingCopy)

        PdfUtil.overlayPDFs(testingCopy, overlay1page)

        //so far there is no better way of testing the overlay
        Assertions.assertTrue("new file should be larger than original", testingCopy.length() > original.length())

        testingCopy.delete()
    }

    
    @Test
    void testOverlayEmpty() throws Exception {
        File testingCopy = new File(original.getParentFile().getAbsoluteFile(), "testingCopy3.pdf")
        FileUtils.copyFile(original, testingCopy)

        PdfUtil.overlayPDFs(testingCopy, null)

        //so far there is no better way of testing the overlay
        Assertions.assertEquals("new file should be same as original", testingCopy.length(), original.length())

        testingCopy.delete()
    }
}
