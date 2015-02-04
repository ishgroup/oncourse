package ish.oncourse.webservices.replication.updaters.v9;

import ish.common.types.ExpiryType;
import ish.common.types.ProductType;
import ish.math.Money;
import ish.oncourse.model.College;
import ish.oncourse.model.Queueable;
import ish.oncourse.model.VoucherProduct;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.replication.updaters.UpdaterException;
import ish.oncourse.webservices.replication.v9.updaters.AbstractProductUpdater;
import ish.oncourse.webservices.replication.v9.updaters.VoucherProductUpdater;
import ish.oncourse.webservices.soap.v4.ReplicationTestModule;
import ish.oncourse.webservices.v9.stubs.replication.VoucherProductStub;
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
 * Time: 17:11
 */
public class VoucherProductUpdaterTest extends ServiceTest {
	private static Logger logger = Logger.getLogger(VoucherProductUpdaterTest.class.getName());

	@Before
	public void setupDataSet() throws Exception {
		initTest("ish.oncourse.webservices.services", StringUtils.EMPTY, ReplicationTestModule.class);
	}

	@Test
	public void testValidSKU() {
		VoucherProductUpdater updater = new VoucherProductUpdater();
		VoucherProductStub stub = new VoucherProductStub();
		stub.setAngelId(1l);
		stub.setWillowId(1l);
		stub.setType(ProductType.VOUCHER.getDatabaseValue());
		stub.setTaxAdjustment(Money.ZERO.toBigDecimal());
		stub.setSku("validSKU");
		stub.setNotes(StringUtils.EMPTY);
		stub.setName("Valid voucher product name");
		stub.setIsWebVisible(false);
		stub.setIsOnSale(false);
		stub.setDescription(StringUtils.EMPTY);
		stub.setMaxCoursesRedemption(0);
		stub.setPriceExTax(Money.ZERO.toBigDecimal());
		stub.setExpiryType(ExpiryType.LIFETIME.getDatabaseValue());
		stub.setExpiryDays(0);

		final ObjectContext objectContext = getService(ICayenneService.class).newContext();

		try {
			VoucherProduct entity = objectContext.newObject(VoucherProduct.class);
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
			assertEquals(entity.getMaxCoursesRedemption(), stub.getMaxCoursesRedemption());
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
		VoucherProductUpdater updater = new VoucherProductUpdater();
		VoucherProductStub stub = new VoucherProductStub();
		stub.setAngelId(1l);
		stub.setWillowId(1l);
		stub.setType(ProductType.VOUCHER.getDatabaseValue());
		stub.setTaxAdjustment(Money.ZERO.toBigDecimal());
		stub.setSku("        ");
		stub.setNotes(StringUtils.EMPTY);
		stub.setName("Valid voucher product name");
		stub.setIsWebVisible(false);
		stub.setIsOnSale(false);
		stub.setDescription(StringUtils.EMPTY);
		stub.setMaxCoursesRedemption(0);
		stub.setPriceExTax(Money.ZERO.toBigDecimal());
		stub.setExpiryType(ExpiryType.LIFETIME.getDatabaseValue());
		stub.setExpiryDays(0);

		final ObjectContext objectContext = getService(ICayenneService.class).newContext();

		try {
			VoucherProduct entity = objectContext.newObject(VoucherProduct.class);
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
			assertEquals("Updater should throw an exception because SKU is empty!",
					e.getMessage(),
					String.format(AbstractProductUpdater.ERROR_SKU_MESSAGE_TEMPLATE,
							VoucherProduct.class.getSimpleName(),
							1,
							1)
			);
		} finally {
			objectContext.rollbackChanges();
		}
	}
}
