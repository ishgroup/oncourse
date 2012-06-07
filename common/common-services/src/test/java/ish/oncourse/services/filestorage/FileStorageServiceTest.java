package ish.oncourse.services.filestorage;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;

import static org.junit.Assert.*;

public class FileStorageServiceTest {

    private static File rootDir;
    private static FileStorageService fileStorageService;

    @BeforeClass
    public static void setup() throws Exception {
        rootDir = new File(System.getProperty("user.dir"), "FileStorageServiceTest");
        boolean result = rootDir.mkdirs();
        if (!result) {
            throw new IllegalArgumentException();
        }
        fileStorageService = new FileStorageService();
        fileStorageService.setReplicationService(new FileStorageReplicationService());
        fileStorageService.setRootDir(rootDir);

    }

    @Test
    public void testPut() throws Exception {
        InputStream inputStream = null;
        try {
            inputStream = FileStorageService.class.getResourceAsStream("FileStorageServiceTest.class");
            byte[] data = IOUtils.toByteArray(inputStream);
            fileStorageService.put(data,"test1/FileStorageServiceTest.class");

            File file = new File(fileStorageService.getRootDir(),"test1/FileStorageServiceTest.class");
            assertTrue("File exists", file.exists());
            assertTrue("File is file", file.isFile());

        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Test
    public void testGet() throws Exception {
        try {
            byte[] data = fileStorageService.get("test1/FileStorageServiceTest.class");
            File file = new File(fileStorageService.getRootDir(),"test1/FileStorageServiceTest.class");
            assertNotNull("Data is not empty", data);
            assertEquals("Size equals", file.length(), data.length);
        } finally {
        }
    }

    @Test
    public void testDelete() throws Exception {
            fileStorageService.delete("test1/FileStorageServiceTest.class");
            File file = new File(fileStorageService.getRootDir(),"test1/FileStorageServiceTest.class");
            assertTrue("File does not exist", !file.exists());
    }


    @AfterClass
    public static void destroy() throws Exception {
        FileUtils.deleteQuietly(rootDir);
    }

}
