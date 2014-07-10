package ish.oncourse.services.filestorage;

import ish.common.types.AttachmentInfoVisibility;
import ish.oncourse.model.College;
import ish.oncourse.model.Document;
import ish.oncourse.model.DocumentVersion;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.DataSetInitializer;
import ish.oncourse.test.InitialContextFactoryMock;
import ish.oncourse.test.ServiceTest;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.File;
import java.io.InputStream;

import static org.junit.Assert.*;

public class FileStorageAssetServiceTest extends ServiceTest {

    private File rootDir;
    private FileStorageAssetService fileStorageAssetService;
    private ICayenneService cayenneService;

    @Before
    public void setup() throws Exception {
        initTest("ish.oncourse.services", "service", ServiceModule.class);

        DataSetInitializer.initDataSets("ish/oncourse/services/filestorage/dataSet.xml");
        cayenneService = getService(ICayenneService.class);
        initFileStorageAssetService();
    }

    private void initFileStorageAssetService() throws NamingException {
        rootDir = new File(System.getProperty("user.dir"), "FileStorageServiceTest");
        boolean result = rootDir.mkdirs();
        if (!result) {
            throw new IllegalArgumentException();
        }
        Context context = new InitialContext();
        InitialContextFactoryMock.bind("java:comp/env",context);
        context.bind(FileStorageService.ENV_NAME_fileStorageRoot, rootDir.getAbsolutePath());
        context.bind(FileStorageReplicationService.ENV_NAME_syncScript, rootDir.getAbsolutePath());

        fileStorageAssetService = new FileStorageAssetService();
    }



    @Test
    public void test() throws Exception
    {
        InputStream inputStream = FileStorageAssetService.class.getResourceAsStream("FileStorageAssetService.class");
        byte[] data = IOUtils.toByteArray(inputStream);
        String md5 = DigestUtils.md5Hex(data);

        ObjectContext context = cayenneService.newNonReplicatingContext();

        College college = Cayenne.objectForPK(context,College.class, 1);

        DocumentVersion binaryInfo = createBinaryInfo(context, "file1", college);

        binaryInfo.setByteSize((long) data.length);
        binaryInfo.setCollege(college);
        String path = fileStorageAssetService.getPathBy(data, binaryInfo);
        //test path building
        assertNotNull("path is not null", path);
        assertEquals("check path value", String.format("%d/%s", college.getId(), md5), path);

        //test the  binaryInfo put
        fileStorageAssetService.put(data,binaryInfo);
        assertEquals("check BinaryInfo filePath", path, binaryInfo.getFilePath());
        context.commitChanges();

        //test the  binaryInfo get
        byte[] data1 = fileStorageAssetService.get(binaryInfo);
        assertEquals("get data size",data.length, data1.length);


        //test put the same file but  binaryInfo2
        DocumentVersion binaryInfo2 = createBinaryInfo(context, "file2", college);
        binaryInfo2.setByteSize((long) data.length);
        binaryInfo2.setCollege(college);
        fileStorageAssetService.put(data,binaryInfo2);
        context.commitChanges();


        //test delete binaryInfo2
        fileStorageAssetService.delete(binaryInfo2);
        context.deleteObject(binaryInfo2);
        context.commitChanges();
        assertTrue("file exists for the binaryInfo2", fileStorageAssetService.contains(binaryInfo2));

        //test delete binaryInfo
        fileStorageAssetService.delete(binaryInfo);
        context.deleteObject(binaryInfo);
        context.commitChanges();
        assertFalse("file does not exist for the binaryInfo", fileStorageAssetService.contains(binaryInfo));

        //test put again the same final but new binaryInfo
        binaryInfo = createBinaryInfo(context, "file2", college);
        binaryInfo.setByteSize((long) data.length);
        binaryInfo.setCollege(college);
        fileStorageAssetService.put(data, binaryInfo);
        context.commitChanges();
        assertTrue("file does exist for the binaryInfo", fileStorageAssetService.contains(binaryInfo));

    }

    private DocumentVersion createBinaryInfo(ObjectContext context, String fileName, College college) {
        Document document = context.newObject(Document.class);
		DocumentVersion documentVersion = context.newObject(DocumentVersion.class);

		document.setCollege(college);
		document.setName(fileName);
		document.setWebVisibility(AttachmentInfoVisibility.PUBLIC);
		document.setIsRemoved(false);
		document.setIsShared(true);
		
		documentVersion.setCollege(college);
        documentVersion.setMimeType("");
        documentVersion.setPixelHeight(0);
        documentVersion.setPixelWidth(0);
		documentVersion.setDocument(document);
        
        return documentVersion;
    }

    @After
    public void destroy() throws Exception {
        FileUtils.deleteQuietly(rootDir);
    }


}
