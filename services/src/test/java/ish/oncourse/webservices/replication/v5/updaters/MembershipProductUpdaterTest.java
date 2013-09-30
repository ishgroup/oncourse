package ish.oncourse.webservices.replication.v5.updaters;

import ish.common.types.ExpiryType;
import ish.common.types.ProductType;
import ish.math.Money;
import ish.oncourse.model.College;
import ish.oncourse.model.MembershipProduct;
import ish.oncourse.model.Queueable;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.replication.v4.updaters.UpdaterException;
import ish.oncourse.webservices.soap.v4.ReplicationTestModule;
import ish.oncourse.webservices.v5.stubs.replication.MembershipProductStub;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: vdavidovich
 * Date: 30.09.13
 * Time: 17:44
 */
public class MembershipProductUpdaterTest extends ServiceTest {
	private static Logger logger = Logger.getLogger(MembershipProductUpdaterTest.class.getName());

	@Before
	public void setupDataSet() throws Exception {
		initTest("ish.oncourse.webservices.services", StringUtils.EMPTY, ReplicationTestModule.class);
	}

	@Test
	public void testValidSKU() {
		MembershipProductUpdater updater = new MembershipProductUpdater();
		MembershipProductStub stub = new MembershipProductStub();
		stub.setAngelId(1l);
		stub.setWillowId(1l);
		stub.setType(ProductType.VOUCHER.getDatabaseValue());
		stub.setTaxAdjustment(Money.ZERO.toBigDecimal());
		stub.setSku("validSKU");
		stub.setNotes(StringUtils.EMPTY);
		stub.setName("Valid membership product name");
		stub.setIsWebVisible(false);
		stub.setIsOnSale(false);
		stub.setDescription(StringUtils.EMPTY);
		stub.setPriceExTax(Money.ZERO.toBigDecimal());
		stub.setExpiryType(ExpiryType.LIFETIME.getDatabaseValue());
		stub.setExpiryDays(0);

		final ObjectContext objectContext = getService(ICayenneService.class).newContext();

		try {
			MembershipProduct entity = objectContext.newObject(MembershipProduct.class);
			College college = objectContext.newObject(College.class);
			entity.setCollege(college);
			RelationShipCallback relationShipCallback = new RelationShipCallback() {
				@Override
				public <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
					return null;
				}
			};
			updater.updateEntity(stub, entity, relationShipCallback);
			assertEquals(entity.getType(), stub.getType());
			assertEquals(entity.getTaxAdjustment().toBigDecimal(), stub.getTaxAdjustment());
			assertEquals(entity.getSku(), stub.getSku());
			assertEquals(entity.getNotes(), stub.getNotes());
			assertEquals(entity.getName(), stub.getName());
			assertEquals(entity.getIsWebVisible(), stub.isIsWebVisible());
			assertEquals(entity.getIsOnSale(), stub.isIsOnSale());
			assertEquals(entity.getDescription(), stub.getDescription());
			assertEquals(entity.getPriceExTax().toBigDecimal(), stub.getPriceExTax());
			assertEquals(entity.getExpiryType().getDatabaseValue(), stub.getExpiryType());
			assertEquals(entity.getExpiryDays(), stub.getExpiryDays());
		} catch (UpdaterException e) {
			logger.error( e.getMessage(), e);
			assertTrue(e.getMessage(), false);
		} finally {
			objectContext.rollbackChanges();
		}
	}

	@Test
	public void testInValidSKU() {
		MembershipProductUpdater updater = new MembershipProductUpdater();
		MembershipProductStub stub = new MembershipProductStub();
		stub.setAngelId(1l);
		stub.setWillowId(1l);
		stub.setType(ProductType.VOUCHER.getDatabaseValue());
		stub.setTaxAdjustment(Money.ZERO.toBigDecimal());
		stub.setSku("        ");
		stub.setNotes(StringUtils.EMPTY);
		stub.setName("Valid membership product name");
		stub.setIsWebVisible(false);
		stub.setIsOnSale(false);
		stub.setDescription(StringUtils.EMPTY);
		stub.setPriceExTax(Money.ZERO.toBigDecimal());
		stub.setExpiryType(ExpiryType.LIFETIME.getDatabaseValue());
		stub.setExpiryDays(0);

		final ObjectContext objectContext = getService(ICayenneService.class).newContext();

		try {
			MembershipProduct entity = objectContext.newObject(MembershipProduct.class);
			College college = objectContext.newObject(College.class);
			entity.setCollege(college);
			RelationShipCallback relationShipCallback = new RelationShipCallback() {
				@Override
				public <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
					return null;
				}
			};
			updater.updateEntity(stub, entity, relationShipCallback);
			assertTrue("Empty SKU should lead to updater exception", false);
		} catch (UpdaterException e) {
			logger.error( e.getMessage(), e);
			assertTrue("Updater should throw an exception because SKU is empty!",
					e.getMessage().startsWith("Membership product with angelId = 1 and willowid = 1 and empty SKU detected!"));
		} finally {
			objectContext.rollbackChanges();
		}
	}
}
