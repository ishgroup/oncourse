/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.discount;

import ish.math.Money;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.DiscountCourseClass;
import ish.oncourse.services.ServiceTestModule;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class GetDiscountTest extends ServiceTest {

	private ICayenneService cayenneService;


	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "service", ServiceTestModule.class);
		InputStream st = GetDiscountTest.class.getClassLoader().getResourceAsStream("ish/oncourse/services/discount/GetDiscountTest.xml");

		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		builder.setColumnSensing(true);
		FlatXmlDataSet dataSet = builder.build(st);

		DataSource refDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);

		cayenneService = getService(ICayenneService.class);
	}
	
	@Test
	public void test() {
		ObjectContext context = cayenneService.newContext();
		CourseClass courseClass = SelectById.query(CourseClass.class, 1l).selectOne(context);
		List<Discount> promotions = SelectById.query(Discount.class, 5l).select(context);

		Comparator idComparator = new Comparator<DiscountCourseClass>() {
			@Override
			public int compare(DiscountCourseClass o1, DiscountCourseClass o2) {
				return o1.getId().compareTo(o2.getId());
			}
		};

		List<DiscountCourseClass> applicableDiscounts = GetAppliedDiscounts.valueOf(courseClass,promotions, null).get();
		assertEquals(3, applicableDiscounts.size());
		Collections.sort(applicableDiscounts,idComparator);

		assertEquals(Long.valueOf(1l), applicableDiscounts.get(0).getId());
		assertEquals(Long.valueOf(2l), applicableDiscounts.get(1).getId());


		List<DiscountCourseClass> potencialDiscounts = GetPossibleDiscounts.valueOf(courseClass).get();
		assertEquals(5, potencialDiscounts.size());
		Collections.sort(potencialDiscounts, idComparator);

		assertEquals(Long.valueOf(8l), potencialDiscounts.get(0).getId());
		assertEquals(Long.valueOf(9l), potencialDiscounts.get(1).getId());
		assertEquals(Long.valueOf(10l), potencialDiscounts.get(2).getId());
		assertEquals(Long.valueOf(12l), potencialDiscounts.get(3).getId());
		assertEquals(Long.valueOf(13l), potencialDiscounts.get(4).getId());
		
		List<DiscountItem> discountItems = WebDiscountUtils.sortByDiscountValue(potencialDiscounts,courseClass.getFeeExGst(), courseClass.getTaxRate());

		assertEquals(4, discountItems.size());
		assertEquals(new Money("55.00"), discountItems.get(0).getFeeIncTax());
		assertEquals("discountPercent12 / discountPercent13", discountItems.get(0).getTitle());
		assertEquals(new Money("66.00"), discountItems.get(1).getFeeIncTax());
		assertEquals("discountPercent10", discountItems.get(1).getTitle());
		assertEquals(new Money("77.00"), discountItems.get(2).getFeeIncTax());
		assertEquals("discountPercent9", discountItems.get(2).getTitle());
		assertEquals(new Money("88.00"), discountItems.get(3).getFeeIncTax());
		assertEquals("discountPercent8", discountItems.get(3).getTitle());
	}	
}
