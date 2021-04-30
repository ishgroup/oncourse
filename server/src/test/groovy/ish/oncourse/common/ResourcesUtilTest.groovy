/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.common

import org.junit.jupiter.api.Test

import static org.junit.Assert.*

/**
 */
class ResourcesUtilTest {

	@Test
    void testGetMimeType() {
		File f = new File(this.getClass().getResource("/resources/picture.jpg").getPath())
        assertTrue("File "+f.getAbsolutePath()+" does not exist", f.exists())
        assertEquals("Assuming the file type", "image/jpeg", ResourcesUtil.getMimeType(f.getAbsolutePath()))

        f = new File(this.getClass().getResource("/resources/html_doc.html").getPath())
        assertTrue("File "+f.getAbsolutePath()+" does not exist", f.exists())
        assertEquals("Assuming the file type", "text/html", ResourcesUtil.getMimeType(f.getAbsolutePath()))

        f = new File(this.getClass().getResource("/resources/text_doc.txt").getPath())
        assertTrue("File "+f.getAbsolutePath()+" does not exist", f.exists())
        assertEquals("Assuming the file type", "text/plain", ResourcesUtil.getMimeType(f.getAbsolutePath()))

        f = new File(this.getClass().getResource("/resources/excel.xls").getPath())
        assertTrue("File "+f.getAbsolutePath()+" does not exist", f.exists())
        assertEquals("Assuming the file type", "application/octet-stream", ResourcesUtil.getMimeType(f.getAbsolutePath()))

        f = new File(this.getClass().getResource("/resources/powerpoint.ppt").getPath())
        assertTrue("File "+f.getAbsolutePath()+" does not exist", f.exists())
        assertEquals("Assuming the file type", "application/octet-stream", ResourcesUtil.getMimeType(f.getAbsolutePath()))

        f = new File(this.getClass().getResource("/resources/sample.doc").getPath())
        assertTrue("File "+f.getAbsolutePath()+" does not exist", f.exists())
        assertEquals("Assuming the file type", "application/octet-stream", ResourcesUtil.getMimeType(f.getAbsolutePath()))

        f = new File(this.getClass().getResource("/resources/sample.docx").getPath())
        assertTrue("File "+f.getAbsolutePath()+" does not exist", f.exists())
        assertEquals("Assuming the file type", "application/octet-stream",
				ResourcesUtil.getMimeType(f.getAbsolutePath()))
    }

	@Test
    void testFileToByteArray() {
		try {
			File f = new File(this.getClass().getResource("/resources/text_doc.txt").getFile())
            assertTrue("File "+f.getAbsolutePath()+" does not exist", f.exists())
            byte[] result = ResourcesUtil.fileToByteArray(f)
            assertEquals(9, result.length)

        } catch (IOException e) {
            fail(e.getMessage())
        }
	}

	@Test
    void testReadFile() {
		File f = new File(this.getClass().getResource("/resources/text_doc.txt").getFile())
        assertTrue("File "+f.getAbsolutePath()+" does not exist", f.exists())
        assertTrue(ResourcesUtil.readFile(f.getPath()) != null)
        assertTrue(ResourcesUtil.readFile(f.getPath()).startsWith("some text"))
    }

	@Test
    void testHashFile() {
		try {
			File f = new File(this.getClass().getResource("/resources/text_doc.txt").getFile())
            assertTrue("File "+f.getAbsolutePath()+" does not exist", f.exists())
            assertEquals("", "37aa63c77398d954473262e1a057c1e632eda77", ResourcesUtil.hashFile(f))
        } catch (IOException e) {
            fail(e.getMessage())
        }
	}

	@Test
    void testGetAppVersion() {
		assertTrue(ResourcesUtil.getReleaseVersionString() != null)
        assertTrue("Testing release version string: " + ResourcesUtil.getReleaseVersionString(), ResourcesUtil.getReleaseVersionString().length() > 1)
    }

	@Test
    void testJarPathAndAttributes() throws Exception {
		Class<?> classwithnojar = ResourcesUtilTest.class

        URL jarUrl = ResourcesUtil.getRunningJarUrl(classwithnojar)
        assertNull(jarUrl)

        Class<?> classwithjar = javax.mail.Address.class
        jarUrl = ResourcesUtil.getRunningJarUrl(classwithjar)

        assertTrue(jarUrl != null)

        JarURLConnection conn = (JarURLConnection) jarUrl.openConnection()

        assertTrue(conn != null)
    }

	@Test
    void testGetResourceAsStream() {
		// test resources
		assertNotNull(ResourcesUtil.getResourceAsInputStream("resources/picture.jpg"))
        assertNotNull(ResourcesUtil.getResourceAsInputStream("resources/text_doc.txt"))
        // resources from the main
		assertNotNull(ResourcesUtil.getResourceAsInputStream("LICENSE"))
        assertNotNull(ResourcesUtil.getResourceAsInputStream("cayenne/AngelMap.map.xml"))
    }
}
