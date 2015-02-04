package ish.oncourse.webservices.replication.updaters.v9;


import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.model.Queueable;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.replication.updaters.UpdaterException;
import ish.oncourse.webservices.replication.v9.updaters.PaymentInLineUpdater;
import ish.oncourse.webservices.soap.v4.ReplicationTestModule;
import ish.oncourse.webservices.v9.stubs.replication.PaymentInLineStub;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PaymentInLineUpdaterTest extends ServiceTest {
    
    private static Logger logger = Logger.getLogger(PaymentInLineUpdaterTest.class.getName());


    @Before
    public void setupDataSet() throws Exception {
        initTest("ish.oncourse.webservices.services", StringUtils.EMPTY, ReplicationTestModule.class);

    }

    @Test
    /**
     * Test when we can find  invoice and paymentIn by angelIds .
     */
    public void testValidInvoiceAndPayment() {
        PaymentInLineUpdater paymentInLineUpdater = new PaymentInLineUpdater();
        PaymentInLineStub paymentInLineStub = new PaymentInLineStub();
        paymentInLineStub.setAngelId(1l);
        paymentInLineStub.setInvoiceId(1l);
        paymentInLineStub.setPaymentInId(1l);


        final ObjectContext objectContext = getService(ICayenneService.class).newContext();
        try
        {
            PaymentInLine pil = objectContext.newObject(PaymentInLine.class);
            RelationShipCallback relationShipCallback = new RelationShipCallback() {
                @Override
                public <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
                    if (clazz == Invoice.class) return objectContext.newObject(clazz);
                    if (clazz == PaymentIn.class) return objectContext.newObject(clazz);
                    return null;
                }
            };
            paymentInLineUpdater.updateEntity(paymentInLineStub, pil, relationShipCallback);

            assertNotNull("invoice property is not null", pil.getInvoice());
            assertNotNull("paymentIn property is not null", pil.getPaymentIn());
        } catch(UpdaterException e)
        {
            logger.error( e.getMessage(), e);
            assertTrue(e.getMessage(), false);
        }
        finally
        {
            objectContext.rollbackChanges();
        }

    }

    @Test
    public void testInvalidInvoice() {
        PaymentInLineUpdater paymentInLineUpdater = new PaymentInLineUpdater();
        PaymentInLineStub paymentInLineStub = new PaymentInLineStub();
        paymentInLineStub.setAngelId(1l);
        paymentInLineStub.setInvoiceId(1l);
        paymentInLineStub.setPaymentInId(1l);


        final ObjectContext objectContext = getService(ICayenneService.class).newContext();
        try
        {
            PaymentInLine pil = objectContext.newObject(PaymentInLine.class);
            RelationShipCallback relationShipCallback = new RelationShipCallback() {
                @Override
                public <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
                    if (clazz == PaymentIn.class) return objectContext.newObject(clazz);
                    return null;
                }
            };
            paymentInLineUpdater.updateEntity(paymentInLineStub, pil, relationShipCallback);

            assertTrue("The updater should throw exception", false);
        } catch(UpdaterException e)
        {
            logger.info(e.getMessage(), e);
            assertTrue("test error message", e.getMessage().contains("Can not find invoice"));
        }
        finally
        {
            objectContext.rollbackChanges();
        }

    }

    @Test
    public void testInvalidPaymentIn() {
        PaymentInLineUpdater paymentInLineUpdater = new PaymentInLineUpdater();
        PaymentInLineStub paymentInLineStub = new PaymentInLineStub();
        paymentInLineStub.setAngelId(1l);
        paymentInLineStub.setInvoiceId(1l);
        paymentInLineStub.setPaymentInId(1l);


        final ObjectContext objectContext = getService(ICayenneService.class).newContext();
        try
        {
            PaymentInLine pil = objectContext.newObject(PaymentInLine.class);
            RelationShipCallback relationShipCallback = new RelationShipCallback() {
                @Override
                public <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
                    if (clazz == Invoice.class) return objectContext.newObject(clazz);
                    return null;
                }
            };
            paymentInLineUpdater.updateEntity(paymentInLineStub, pil, relationShipCallback);

            assertTrue("The updater should throw exception", false);
        } catch(UpdaterException e)
        {
            logger.info( e.getMessage(), e);
            assertTrue("test error message" , e.getMessage().contains("Can not find paymentIn"));
        }
        finally
        {
            objectContext.rollbackChanges();
        }

    }
}
