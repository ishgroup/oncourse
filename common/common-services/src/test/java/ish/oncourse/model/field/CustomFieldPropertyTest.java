/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.model.field;

import ish.oncourse.cayenne.FieldInterface;
import ish.oncourse.common.field.PropertyGetSet;
import ish.oncourse.common.field.PropertyGetSetFactory;
import ish.oncourse.model.Contact;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectById;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

public class CustomFieldPropertyTest extends ServiceTest {

	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "service", ServiceModule.class);

		InputStream st = CustomFieldPropertyTest.class.getClassLoader().getResourceAsStream("ish/oncourse/model/field/customFieldPropertyDataSet.xml");
		FlatXmlDataSetBuilder builder =  new FlatXmlDataSetBuilder();
		builder.setColumnSensing(true);
		FlatXmlDataSet dataSet = builder.build(st);
		DataSource onDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(onDataSource.getConnection(), null), dataSet);
	}
	
	@Test
		 public void testSet() {
		ObjectContext context = getService(ICayenneService.class).newContext();
		Contact contact = SelectById.query(Contact.class, 1l).selectOne(context);

		FieldInterface customField = mock(FieldInterface.class);
		when(customField.getProperty()).thenReturn("customField.customField");

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
