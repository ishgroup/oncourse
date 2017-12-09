/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.services.binary;

import ish.common.types.AttachmentInfoVisibility;
import ish.common.types.AttachmentSpecialType;
import ish.oncourse.model.*;
import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.tag.TagService;
import ish.oncourse.test.LoadDataSet;
import ish.oncourse.test.ServiceTest;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.internal.test.TestableRequest;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.Request;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BinaryDataServiceTest extends ServiceTest {
	private ICayenneService cayenneService;
	private IBinaryDataService binaryDataService;
	private ApplicationStateManager applicationStateManager;
	
	private College college;
	private Request request;

	@Mock
	private IWebSiteService webSiteService;

	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "service", ServiceTestModule.class);

		new LoadDataSet().dataSetFile("ish/oncourse/services/binary/BinaryDataServiceTest.xml").load(testContext.getDS());
		request = getService(TestableRequest.class);
		cayenneService = getService(ICayenneService.class);
		binaryDataService = getService(IBinaryDataService.class);
		applicationStateManager = getService(ApplicationStateManager.class);
	}


	@Test
	public void profilePictureTest() {
		ObjectContext context = cayenneService.newContext();
		college = Cayenne.objectForPK(context, College.class, 1l);
		request.setAttribute("currentCollege", college);
		Contact contact1 = Cayenne.objectForPK(context, Contact.class, 1l);

		Document binaryInfo = createBinaryInfo(context, "binaryInfo1");
		createBinaryInfoRelation(context, contact1, binaryInfo);

		binaryInfo = createBinaryInfo(context, "binaryInfo2");
		createBinaryInfoRelation(context, contact1, binaryInfo);

		binaryInfo = createBinaryInfo(context, "binaryInfo3");
		createBinaryInfoRelation(context, contact1, binaryInfo);

		binaryInfo = createBinaryInfo(context, "binaryInfo4");
		BinaryInfoRelation binaryInfoRelation4 = createBinaryInfoRelation(context, contact1, binaryInfo);
		binaryInfoRelation4.setSpecialType(AttachmentSpecialType.PROFILE_PICTURE);

		binaryInfo = createBinaryInfo(context, "binaryInfo5");
		BinaryInfoRelation binaryInfoRelation5 = createBinaryInfoRelation(context, contact1, binaryInfo);
		binaryInfoRelation5.setSpecialType(AttachmentSpecialType.PROFILE_PICTURE);
		context.commitChanges();

		List<Document> list = binaryDataService.getAttachedFiles(1l, Contact.class.getSimpleName(), false);
		assertEquals(3, list.size());

		binaryInfo = binaryDataService.getProfilePicture(contact1);
		assertNotNull(binaryInfo);
	}

	@Test
	public void testGetAttachments() {
		ObjectContext context = cayenneService.newContext();
		college = Cayenne.objectForPK(context, College.class, 1l);
		Contact contact = Cayenne.objectForPK(context, Contact.class, 1l);
		
		
		when(webSiteService.getCurrentCollege()).thenReturn(college);
		IBinaryDataService binaryDataService = new BinaryDataService(cayenneService, webSiteService, applicationStateManager, null, null, null);
		
		
		Document binaryInfo1 = createBinaryInfo(context, "binaryInfo1");
		createBinaryInfoRelation(context, contact, binaryInfo1);

		Document binaryInfo2 = createBinaryInfo(context, "binaryInfo2");
		createBinaryInfoRelation(context, contact, binaryInfo2);

		Document binaryInfo3 = createBinaryInfo(context, "binaryInfo3");
		createBinaryInfoRelation(context, contact, binaryInfo3);
		
		context.commitChanges();
		
		assertEquals(3, binaryDataService.getAttachments(contact).size());

		Contact contact2 = Cayenne.objectForPK(context, Contact.class, 2l);

		assertEquals(0, binaryDataService.getAttachments(contact2).size());
	}

	@Test
	public void testGetAttachmentByTag() {
		ObjectContext context = cayenneService.newContext();
		college = Cayenne.objectForPK(context, College.class, 1l);
		Contact contact = Cayenne.objectForPK(context, Contact.class, 1l);


		when(webSiteService.getCurrentCollege()).thenReturn(college);

		ITagService tagService = new TagService(cayenneService, webSiteService);
		IBinaryDataService binaryDataService = new BinaryDataService(cayenneService, webSiteService, applicationStateManager, null, null, tagService);


		Document binaryInfo1 = createBinaryInfo(context, "binaryInfo1");
		createBinaryInfoRelation(context, contact, binaryInfo1);

		Document binaryInfo2 = createBinaryInfo(context, "binaryInfo2");
		createBinaryInfoRelation(context, contact, binaryInfo2);

		Document binaryInfo3 = createBinaryInfo(context, "binaryInfo3");
		createBinaryInfoRelation(context, contact, binaryInfo3);

		context.commitChanges();

		Tag documentTag = context.newObject(Tag.class);
		documentTag.setCollege(college);
		documentTag.setName("document Tag");
		documentTag.setIsTagGroup(true);
		documentTag.setIsWebVisible(true);

		Tag childTag = context.newObject(Tag.class);
		childTag.setCollege(college);
		childTag.setName("pic");
		childTag.setParent(documentTag);
		childTag.setIsWebVisible(true);

		Taggable taggable = context.newObject(Taggable.class);
		taggable.setCollege(college);
		taggable.setEntityIdentifier("Document");
		taggable.setEntityWillowId(binaryInfo1.getId());

		TaggableTag taggableTag = context.newObject(TaggableTag.class);
		taggableTag.setCollege(college);
		taggableTag.setTag(childTag);
		taggableTag.setTaggable(taggable);

		context.commitChanges();

		assertEquals(3, binaryDataService.getAttachments(contact).size());
		assertEquals(binaryInfo1.getObjectId(), binaryDataService.getAttachmentByTag(contact, childTag.getDefaultPath()).getObjectId());
	}

	@Test
	public void GetImagesTest() {
		ObjectContext context = cayenneService.newContext();
		college = Cayenne.objectForPK(context, College.class, 1l);
		request.setAttribute("currentCollege", college);
		Course course = Cayenne.objectForPK(context, Course.class, 1l);


		when(webSiteService.getCurrentCollege()).thenReturn(college);
		IBinaryDataService binaryDataService = new BinaryDataService(cayenneService, webSiteService, applicationStateManager, null, null, null);

		Document binaryInfo = createBinaryInfo(context, "binaryInfo1course", AttachmentType.JPEG.getMimeType());
		createBinaryInfoRelation(context, course, binaryInfo);

		binaryInfo = createBinaryInfo(context, "binaryInfo2course", AttachmentType.JPEG.getMimeType());
		createBinaryInfoRelation(context, course, binaryInfo);

		binaryInfo = createBinaryInfo(context, "binaryInfo3course", AttachmentType.JPEG.getMimeType());
		createBinaryInfoRelation(context, course, binaryInfo);

		binaryInfo = createBinaryInfo(context, "binaryInfo4course", AttachmentType.JPEG.getMimeType());
		createBinaryInfoRelation(context, course, binaryInfo);

		binaryInfo = createBinaryInfo(context, "binaryInfo5course", AttachmentType.XLS.getMimeType());
		createBinaryInfoRelation(context, course, binaryInfo);

		binaryInfo = createBinaryInfo(context, "binaryInfo6course", AttachmentType.TXT.getMimeType());
		createBinaryInfoRelation(context, course, binaryInfo);

		binaryInfo = createBinaryInfo(context, "binaryInfo7course", AttachmentType.BMP.getMimeType());
		createBinaryInfoRelation(context, course, binaryInfo);
		
		context.commitChanges();

		List<Document> list = binaryDataService.getImages(course);
		assertEquals(4, list.size());
	}
	
	private Document createBinaryInfo(ObjectContext context, String fileName) {
		return createBinaryInfo(context, fileName, "");
	}

	private Document createBinaryInfo(ObjectContext context, String fileName, String mimeType) {
		Document document = context.newObject(Document.class);
		DocumentVersion version = context.newObject(DocumentVersion.class);
		
		document.setCollege(college);
		document.setName(fileName);
		document.setWebVisibility(AttachmentInfoVisibility.PUBLIC);
		document.setIsRemoved(false);
		document.setIsShared(true);

		version.setCollege(college);
		version.setPixelHeight(0);
		version.setPixelWidth(0);
		version.setMimeType(mimeType);
		
		version.setDocument(document);
		
		return document;
	}

	private BinaryInfoRelation createBinaryInfoRelation(ObjectContext context, Queueable entity, Document binaryInfo) {
		BinaryInfoRelation relation = context.newObject(BinaryInfoRelation.class);
		relation.setDocument(binaryInfo);
		relation.setCollege(binaryInfo.getCollege());
		relation.setEntityAngelId(entity.getAngelId());
		relation.setEntityWillowId(entity.getId());
		relation.setEntityIdentifier(entity.getClass().getSimpleName());
		return relation;
	}


}
