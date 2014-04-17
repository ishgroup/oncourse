package ish.oncourse.model;

import ish.oncourse.test.ContextUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.validation.ValidationException;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DiscountConcessionTypeTest {

    /**
     * Entity to test.
     */
    private DiscountConcessionType discountConcessionType;

    private ObjectContext context;
    private College college;
    private ConcessionType concessionType;
    private Discount discount;

    @Before
    public void setup() throws Exception {
        ContextUtils.setupDataSources();
        context = ContextUtils.createObjectContext();

        /**
         * Instance of any College entity.
         */
        college = context.newObject(College.class);
        college.setTimeZone("Australia/Sydney");
        college.setFirstRemoteAuthentication(new Date());
        college.setRequiresAvetmiss(false);

        context.commitChanges();
        /**
         * Instance of any ConcessionType entity.
         */
        concessionType = context.newObject(ConcessionType.class);
        concessionType.setName("TestName");
        concessionType.setCollege(college);

        context.commitChanges();
        /**
         * Instance of any Discount entity.
         */
        discount = context.newObject(Discount.class);
        discount.setCollege(college);
        discount.setHideOnWeb(false);
        context.commitChanges();
    }

    @Test
    public void testRequiredCollegeId() throws Exception {
        discountConcessionType = context.newObject(DiscountConcessionType.class);
        assertNull("College status should be null before test", discountConcessionType.getCollege());

        /**
         *  ConcessionType and Discount is required too
         */
        discountConcessionType.setConcessionType(concessionType);
        discountConcessionType.setDiscount(discount);

        boolean invalid = false;
        try {
            context.commitChanges();
        } catch (ValidationException ve) {
            invalid = true;
        }
        assertTrue("commit should be in failure status", invalid);
    }
}
