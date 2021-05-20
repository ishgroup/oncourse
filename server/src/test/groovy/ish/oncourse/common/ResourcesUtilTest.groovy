/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.common

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
class ResourcesUtilTest {

    @Test
    void testGetMimeType() {
        File f = new File(this.getClass().getResource("/resources/picture.jpg").getPath())
        Assertions.assertTrue(f.exists(), "File " + f.getAbsolutePath() + " does not exist")
        Assertions.assertEquals("image/jpeg", ResourcesUtil.getMimeType(f.getAbsolutePath()))

        f = new File(this.getClass().getResource("/resources/html_doc.html").getPath())
        Assertions.assertTrue(f.exists(), "File " + f.getAbsolutePath() + " does not exist")
        Assertions.assertEquals("text/html", ResourcesUtil.getMimeType(f.getAbsolutePath()))

        f = new File(this.getClass().getResource("/resources/text_doc.txt").getPath())
        Assertions.assertTrue(f.exists(), "File " + f.getAbsolutePath() + " does not exist")
        Assertions.assertEquals("text/plain", ResourcesUtil.getMimeType(f.getAbsolutePath()))

        f = new File(this.getClass().getResource("/resources/excel.xls").getPath())
        Assertions.assertTrue(f.exists(), "File " + f.getAbsolutePath() + " does not exist")
        Assertions.assertEquals("application/octet-stream", ResourcesUtil.getMimeType(f.getAbsolutePath()))

        f = new File(this.getClass().getResource("/resources/powerpoint.ppt").getPath())
        Assertions.assertTrue(f.exists(), "File " + f.getAbsolutePath() + " does not exist")
        Assertions.assertEquals("application/octet-stream", ResourcesUtil.getMimeType(f.getAbsolutePath()))

        f = new File(this.getClass().getResource("/resources/sample.doc").getPath())
        Assertions.assertTrue(f.exists(), "File " + f.getAbsolutePath() + " does not exist")
        Assertions.assertEquals("application/octet-stream", ResourcesUtil.getMimeType(f.getAbsolutePath()))

        f = new File(this.getClass().getResource("/resources/sample.docx").getPath())
        Assertions.assertTrue(f.exists(), "File " + f.getAbsolutePath() + " does not exist")
        Assertions.assertEquals("application/octet-stream", ResourcesUtil.getMimeType(f.getAbsolutePath()))
    }

    @Test
    void testFileToByteArray() {
        try {
            File f = new File(this.getClass().getResource("/resources/text_doc.txt").getFile())
            Assertions.assertTrue(f.exists(), "File " + f.getAbsolutePath() + " does not exist")
            byte[] result = ResourcesUtil.fileToByteArray(f)
            Assertions.assertEquals(9, result.length)

        } catch (IOException e) {
            Assertions.fail(e.getMessage())
        }
    }

    @Test
    void testReadFile() {
        File f = new File(this.getClass().getResource("/resources/text_doc.txt").getFile())
        Assertions.assertTrue(f.exists(), "File " + f.getAbsolutePath() + " does not exist")
        Assertions.assertTrue(ResourcesUtil.readFile(f.getPath()) != null)
        Assertions.assertTrue(ResourcesUtil.readFile(f.getPath()).startsWith("some text"))
    }

    @Test
    void testHashFile() {
        try {
            File f = new File(this.getClass().getResource("/resources/text_doc.txt").getFile())
            Assertions.assertTrue(f.exists(), "File " + f.getAbsolutePath() + " does not exist")
            Assertions.assertEquals( "37aa63c77398d954473262e1a057c1e632eda77", ResourcesUtil.hashFile(f))
        } catch (IOException e) {
            Assertions.fail(e.getMessage())
        }
    }

    @Test
    void testGetAppVersion() {
        Assertions.assertTrue(ResourcesUtil.getReleaseVersionString() != null)
        Assertions.assertTrue(ResourcesUtil.getReleaseVersionString().length() > 1, "Testing release version string: " + ResourcesUtil.getReleaseVersionString())
    }

    @Test
    void testJarPathAndAttributes() throws Exception {
        Class<?> classwithnojar = ResourcesUtilTest.class

        URL jarUrl = ResourcesUtil.getRunningJarUrl(classwithnojar)
        Assertions.assertNull(jarUrl)

        Class<?> classwithjar = javax.mail.Address.class
        jarUrl = ResourcesUtil.getRunningJarUrl(classwithjar)

        Assertions.assertTrue(jarUrl != null)

        JarURLConnection conn = (JarURLConnection) jarUrl.openConnection()

        Assertions.assertTrue(conn != null)
    }

    @Test
    void testGetResourceAsStream() {
        // test resources
        Assertions.assertNotNull(ResourcesUtil.getResourceAsInputStream("resources/picture.jpg"))
        Assertions.assertNotNull(ResourcesUtil.getResourceAsInputStream("resources/text_doc.txt"))
        // resources from the main
        Assertions.assertNotNull(ResourcesUtil.getResourceAsInputStream("LICENSE"))
        Assertions.assertNotNull(ResourcesUtil.getResourceAsInputStream("cayenne/AngelMap.map.xml"))
    }
}
