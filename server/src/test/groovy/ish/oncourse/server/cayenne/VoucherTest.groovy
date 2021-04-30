/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.cayenne

import ish.CayenneIshTestCase
import ish.common.types.PaymentSource
import ish.common.types.ProductStatus
import ish.math.Money
import ish.oncourse.server.ICayenneService
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.PersistenceState
import org.apache.cayenne.validation.ValidationResult
import org.junit.Assert
import org.junit.Ignore
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

class VoucherTest extends CayenneIshTestCase {

	private ICayenneService cayenneService

    @BeforeEach
    void setup() throws Exception {
		this.cayenneService = injector.getInstance(ICayenneService.class)
        super.setup()
    }

	@Test
    void testValidationRules() {
		ObjectContext context = cayenneService.getNewNonReplicatingContext()
        ValidationResult result = new ValidationResult()

        VoucherProduct vProduct = context.newObject(VoucherProduct.class)
        vProduct.setPriceExTax(new Money("100.0"))
        vProduct.setValue(new Money("100.0"))

        InvoiceLine il = context.newObject(InvoiceLine.class)

        Voucher voucher = context.newObject(Voucher.class)

        voucher.setCode("code")
        voucher.setExpiryDate(new Date())
        voucher.setStatus(ProductStatus.ACTIVE)
        voucher.setSource(PaymentSource.SOURCE_ONCOURSE)
        voucher.setProduct(vProduct)
        voucher.setInvoiceLine(il)
        voucher.setValueOnPurchase(vProduct.getValue())

        voucher.validateForSave(result)

        assertFalse(result.hasFailures())
        result = new ValidationResult()

        voucher = context.newObject(Voucher.class)

        voucher.setExpiryDate(new Date())
        voucher.setStatus(ProductStatus.ACTIVE)
        voucher.setSource(PaymentSource.SOURCE_ONCOURSE)
        voucher.setProduct(vProduct)
        voucher.setInvoiceLine(il)

        voucher.validateForSave(result)

        assertTrue(result.hasFailures())
        result = new ValidationResult()

        voucher = context.newObject(Voucher.class)

        voucher.setCode("code")
        voucher.setStatus(ProductStatus.ACTIVE)
        voucher.setSource(PaymentSource.SOURCE_ONCOURSE)
        voucher.setProduct(vProduct)
        voucher.setInvoiceLine(il)

        voucher.validateForSave(result)

        assertTrue(result.hasFailures())
        result = new ValidationResult()

        voucher = context.newObject(Voucher.class)

        voucher.setCode("code")
        voucher.setExpiryDate(new Date())
        voucher.setSource(PaymentSource.SOURCE_ONCOURSE)
        voucher.setProduct(vProduct)
        voucher.setInvoiceLine(il)

        voucher.validateForSave(result)

        assertTrue(result.hasFailures())
        result = new ValidationResult()

        voucher = context.newObject(Voucher.class)

        voucher.setCode("code")
        voucher.setExpiryDate(new Date())
        voucher.setStatus(ProductStatus.ACTIVE)
        voucher.setProduct(vProduct)
        voucher.setInvoiceLine(il)

        voucher.validateForSave(result)

        assertTrue(result.hasFailures())
        result = new ValidationResult()

        voucher = context.newObject(Voucher.class)

        voucher.setCode("code")
        voucher.setExpiryDate(new Date())
        voucher.setStatus(ProductStatus.ACTIVE)
        voucher.setSource(PaymentSource.SOURCE_ONCOURSE)
        voucher.setInvoiceLine(il)

        voucher.validateForSave(result)

        assertTrue(result.hasFailures())
        result = new ValidationResult()

        voucher = context.newObject(Voucher.class)

        voucher.setCode("code")
        voucher.setExpiryDate(new Date())
        voucher.setStatus(ProductStatus.ACTIVE)
        voucher.setSource(PaymentSource.SOURCE_ONCOURSE)
        voucher.setProduct(vProduct)

        voucher.validateForSave(result)

        assertTrue(result.hasFailures())
        result = new ValidationResult()
    }

	// TODO: disable status change rules for now (perhaps need to remove it at all?)
	// it is required to change voucher status from REDEEMED to ACTIVE when changes to it are reverted
	// due to change in enrolment selection or not successful money payment
	@Ignore
	@Test
    void testStatusChangeRules() {
		ObjectContext context = cayenneService.getNewNonReplicatingContext()

        Voucher voucher = context.newObject(Voucher.class)

        voucher.setStatus(ProductStatus.ACTIVE)
        voucher.setPersistenceState(PersistenceState.COMMITTED)

        voucher.setStatus(ProductStatus.REDEEMED)

        try {
			voucher.setStatus(ProductStatus.ACTIVE)
            Assert.fail()
        } catch (IllegalStateException e) {
			// that is expected
		}
	}

}
