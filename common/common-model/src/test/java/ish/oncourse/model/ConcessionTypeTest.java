package ish.oncourse.model;

import ish.oncourse.test.TestContext;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.validation.ValidationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class ConcessionTypeTest {

    /**
     * Entity to test.
     */
    private ConcessionType concessionType;

    private TestContext testContext = new TestContext();
    private ObjectContext context;

    @Before
    public void setup() throws Exception {
        testContext.open();
        context = testContext.getServerRuntime().newContext();
    }


    @Test
    public void testRequiredCollegeId() throws Exception {
        concessionType = context.newObject(ConcessionType.class);
        /**
         *  Name is required too
         */
        concessionType.setName("TestName");
        assertNull("College status should be null before test", concessionType.getCollege());

        boolean invalid = false;
        try {
            context.commitChanges();
        } catch (ValidationException ve) {
            invalid = true;
        }
        assertTrue("commit should be in failure status", invalid);
    }
    
    @After
    public void tearDown(){
        testContext.close();
    }
}
