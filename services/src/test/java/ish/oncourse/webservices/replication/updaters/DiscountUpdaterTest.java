package ish.oncourse.webservices.replication.updaters;

import ish.math.MoneyRounding;
import ish.oncourse.model.College;
import ish.oncourse.model.Discount;
import ish.oncourse.model.Queueable;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.replication.v8.updaters.DiscountUpdater;
import ish.oncourse.webservices.soap.v4.ReplicationTestModule;
import ish.oncourse.webservices.v8.stubs.replication.DiscountStub;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang.NotImplementedException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;

public class DiscountUpdaterTest extends ServiceTest {

    @Before
    public void setupDataSet() throws Exception {
        initTest("ish.oncourse.webservices.services", "", ReplicationTestModule.class);
        InputStream st = DiscountUpdaterTest.class.getClassLoader().getResourceAsStream("ish/oncourse/webservices/replication/v4/updaters/oncourseDataSet.xml");
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
        DataSource onDataSource = getDataSource("jdbc/oncourse");
        DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(onDataSource.getConnection(), null), dataSet);
    }

    @Test
    public void testIsAvailableOnWeb() {
        ObjectContext objectContext = getService(ICayenneService.class).newContext();

        College college = Cayenne.objectForPK(objectContext, College.class, 1);

        DiscountStub discountStub = new DiscountStub();
        discountStub.setCode("Code");
        discountStub.setCombinationType(Boolean.FALSE);
        discountStub.setCreated(Calendar.getInstance().getTime());
        discountStub.setDetail("Detail");
        discountStub.setDiscountAmount(BigDecimal.ZERO);
        discountStub.setDiscountRate(BigDecimal.ZERO);
        discountStub.setMaximumDiscount(BigDecimal.ZERO);
        discountStub.setMinimumDiscount(BigDecimal.ZERO);
        discountStub.setModified(Calendar.getInstance().getTime());
        discountStub.setName("Name");
        discountStub.setRoundingMode(MoneyRounding.ROUNDING_NONE.getDatabaseValue());
        discountStub.setStudentAge(0);
        discountStub.setStudentAgeOperator("SA");
        discountStub.setStudentEnrolledWithinDays(0);
        discountStub.setStudentPostcodes("Postcodes");
        discountStub.setValidFrom(Calendar.getInstance().getTime());
        discountStub.setValidTo(Calendar.getInstance().getTime());
        discountStub.setAvailableOnWeb(true);
        discountStub.setHideOnWeb(false);


        TestDiscountUpdater discountUpdater = new TestDiscountUpdater();

        Discount discount = objectContext.newObject(Discount.class);
        discount.setCollege(college);

        discountUpdater.updateEntity(discountStub, discount, new RelationShipCallback() {
            @Override
            public <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
                throw new NotImplementedException();
            }
        });

        assertEquals("IsAvailableOnWeb value", Boolean.TRUE, discount.getIsAvailableOnWeb());
        assertEquals("HideOnWeb value", Boolean.FALSE, discount.getHideOnWeb());

        objectContext.commitChanges();
    }



    private class  TestDiscountUpdater extends  DiscountUpdater
    {
        @Override
        public void updateEntity(DiscountStub stub, Discount entity, RelationShipCallback callback) {
            super.updateEntity(stub, entity, callback);
        }
    }
}
