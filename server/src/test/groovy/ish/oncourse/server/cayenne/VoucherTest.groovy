/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.cayenne

import groovy.transform.CompileStatic
import ish.TestWithDatabase
import ish.common.types.PaymentSource
import ish.common.types.ProductStatus
import ish.math.Money
import org.apache.cayenne.validation.ValidationResult
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
class VoucherTest extends TestWithDatabase {

    @Test
    void testValidationRules() {
        ValidationResult result = new ValidationResult()

        VoucherProduct vProduct = cayenneContext.newObject(VoucherProduct.class)
        vProduct.setPriceExTax(new Money("100.0"))
        vProduct.setValue(new Money("100.0"))

        InvoiceLine il = cayenneContext.newObject(InvoiceLine.class)

        Voucher voucher = cayenneContext.newObject(Voucher.class)

        voucher.setCode("code")
        voucher.setExpiryDate(new Date())
        voucher.setStatus(ProductStatus.ACTIVE)
        voucher.setSource(PaymentSource.SOURCE_ONCOURSE)
        voucher.setProduct(vProduct)
        voucher.setInvoiceLine(il)
        voucher.setValueOnPurchase(vProduct.getValue())

        voucher.validateForSave(result)

        Assertions.assertFalse(result.hasFailures())
        result = new ValidationResult()

        voucher = cayenneContext.newObject(Voucher.class)

        voucher.setExpiryDate(new Date())
        voucher.setStatus(ProductStatus.ACTIVE)
        voucher.setSource(PaymentSource.SOURCE_ONCOURSE)
        voucher.setProduct(vProduct)
        voucher.setInvoiceLine(il)

        voucher.validateForSave(result)

        Assertions.assertTrue(result.hasFailures())
        result = new ValidationResult()

        voucher = cayenneContext.newObject(Voucher.class)

        voucher.setCode("code")
        voucher.setStatus(ProductStatus.ACTIVE)
        voucher.setSource(PaymentSource.SOURCE_ONCOURSE)
        voucher.setProduct(vProduct)
        voucher.setInvoiceLine(il)

        voucher.validateForSave(result)

        Assertions.assertTrue(result.hasFailures())
        result = new ValidationResult()

        voucher = cayenneContext.newObject(Voucher.class)

        voucher.setCode("code")
        voucher.setExpiryDate(new Date())
        voucher.setSource(PaymentSource.SOURCE_ONCOURSE)
        voucher.setProduct(vProduct)
        voucher.setInvoiceLine(il)

        voucher.validateForSave(result)

        Assertions.assertTrue(result.hasFailures())
        result = new ValidationResult()

        voucher = cayenneContext.newObject(Voucher.class)

        voucher.setCode("code")
        voucher.setExpiryDate(new Date())
        voucher.setStatus(ProductStatus.ACTIVE)
        voucher.setProduct(vProduct)
        voucher.setInvoiceLine(il)

        voucher.validateForSave(result)

        Assertions.assertTrue(result.hasFailures())
        result = new ValidationResult()

        voucher = cayenneContext.newObject(Voucher.class)

        voucher.setCode("code")
        voucher.setExpiryDate(new Date())
        voucher.setStatus(ProductStatus.ACTIVE)
        voucher.setSource(PaymentSource.SOURCE_ONCOURSE)
        voucher.setInvoiceLine(il)

        voucher.validateForSave(result)

        Assertions.assertTrue(result.hasFailures())
        result = new ValidationResult()

        voucher = cayenneContext.newObject(Voucher.class)

        voucher.setCode("code")
        voucher.setExpiryDate(new Date())
        voucher.setStatus(ProductStatus.ACTIVE)
        voucher.setSource(PaymentSource.SOURCE_ONCOURSE)
        voucher.setProduct(vProduct)

        voucher.validateForSave(result)

        Assertions.assertTrue(result.hasFailures())
        result = new ValidationResult()
    }

}
