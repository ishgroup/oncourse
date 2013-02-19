/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.services.binary;

import ish.common.types.AttachmentInfoVisibility;
import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.BinaryInfoRelation;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.DataSetInitializer;
import ish.oncourse.test.ServiceTest;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.internal.test.TestableRequest;
import org.apache.tapestry5.services.Request;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BinaryDataServiceTest extends ServiceTest{
	private ICayenneService cayenneService;
	private IBinaryDataService binaryDataService;
	private College college;
	private Request request;

	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "service", ServiceModule.class);

		DataSetInitializer.initDataSets("ish/oncourse/services/binary/BinaryDataServiceTest.xml");
		request = getService(TestableRequest.class);
		cayenneService = getService(ICayenneService.class);
		binaryDataService = getService(IBinaryDataService.class);
	}


	@Test
	public void profilePictureTest()
	{
		ObjectContext context = cayenneService.newContext();
		college = Cayenne.objectForPK(context, College.class, 1l);
		request.setAttribute("currentCollege", college);
		Contact contact1 = Cayenne.objectForPK(context, Contact.class, 1l);

		BinaryInfo binaryInfo = createBinaryInfo(context, "binaryInfo1");
		createBinaryInfoRelation(context, contact1, binaryInfo);

		binaryInfo = createBinaryInfo(context, "binaryInfo2");
		createBinaryInfoRelation(context, contact1, binaryInfo);

		binaryInfo = createBinaryInfo(context, "binaryInfo3");
		createBinaryInfoRelation(context, contact1, binaryInfo);

		binaryInfo = createBinaryInfo(context, BinaryDataService.NAME_PROFILE_PICTURE);
		createBinaryInfoRelation(context, contact1, binaryInfo);
		context.commitChanges();

		List<BinaryInfo> list = binaryDataService.getAttachedFiles(1l, Contact.class.getSimpleName(), false);
		assertEquals(3, list.size());

		binaryInfo = binaryDataService.getProfilePicture(contact1);
		assertNotNull(binaryInfo);
	}


	private BinaryInfo createBinaryInfo(ObjectContext context, String fileName) {
		BinaryInfo binaryInfo = context.newObject(BinaryInfo.class);
		binaryInfo.setCollege(college);
		binaryInfo.setMimeType("");
		binaryInfo.setName(fileName);
		binaryInfo.setPixelHeight(0);
		binaryInfo.setPixelWidth(0);
		binaryInfo.setWebVisible(AttachmentInfoVisibility.PUBLIC);
		return binaryInfo;
	}

	private BinaryInfoRelation createBinaryInfoRelation(ObjectContext context, Contact contact, BinaryInfo binaryInfo) {
		BinaryInfoRelation relation = context.newObject(BinaryInfoRelation.class);
		relation.setBinaryInfo(binaryInfo);
		relation.setCollege(binaryInfo.getCollege());
		relation.setEntityAngelId(contact.getAngelId());
		relation.setEntityWillowId(contact.getId());
		relation.setEntityIdentifier(Contact.class.getSimpleName());
		return relation;
	}


}
