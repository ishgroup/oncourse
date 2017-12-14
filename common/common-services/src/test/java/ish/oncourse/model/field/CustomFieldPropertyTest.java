/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.model.field;

import ish.oncourse.cayenne.FieldInterface;
import ish.oncourse.common.field.PropertyGetSet;
import ish.oncourse.common.field.PropertyGetSetFactory;
import ish.oncourse.model.Contact;
import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.LoadDataSet;
import ish.oncourse.test.tapestry.ServiceTest;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectById;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

public class CustomFieldPropertyTest extends ServiceTest {

	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "service", ServiceTestModule.class);
		new LoadDataSet().dataSetFile("ish/oncourse/model/field/customFieldPropertyDataSet.xml").load(testContext.getDS());
	}
	
	@Test
		 public void testSet() {
		ObjectContext context = getService(ICayenneService.class).newContext();
		Contact contact = SelectById.query(Contact.class, 1l).selectOne(context);

		FieldInterface customField = mock(FieldInterface.class);
		when(customField.getProperty()).thenReturn("customField.contact.customField");

		PropertyGetSetFactory factory = new PropertyGetSetFactory("ish.oncourse.model");
		PropertyGetSet customFieldGetSet = factory.get(customField, contact);

		assertNull(customFieldGetSet.get());
		String value = "Some value";
		customFieldGetSet.set(value);
		assertEquals(1, contact.getCustomFields().size());
		assertEquals(value, contact.getCustomFields().get(0).getValue());
		assertEquals("customField", contact.getCustomFields().get(0).getCustomFieldType().getKey());
		assertEquals(1,context.newObjects().size());

		context.commitChanges();

		contact = SelectById.query(Contact.class, 1l).selectOne(context);
		assertEquals(value, contact.getCustomFields().get(0).getValue());
		assertEquals(value, customFieldGetSet.get());
	}

}
