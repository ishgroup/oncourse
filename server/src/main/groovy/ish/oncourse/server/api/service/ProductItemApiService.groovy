/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.service

import com.google.inject.Inject
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import ish.common.types.ExpiryType
import ish.common.types.ProductStatus
import ish.common.types.ProductType
import ish.common.types.TypesUtil
import ish.math.Money
import ish.oncourse.function.GetContactFullName
import ish.oncourse.server.api.dao.AccountDao
import ish.oncourse.server.api.dao.ContactDao
import ish.oncourse.server.api.dao.ProductItemDAO
import ish.oncourse.server.api.dao.TaxDao
import ish.oncourse.server.api.v1.model.ProductItemCancelDTO
import ish.oncourse.server.api.v1.model.ProductItemDTO
import ish.oncourse.server.api.v1.model.ProductItemPaymentDTO
import ish.oncourse.server.api.v1.model.ProductItemStatusDTO
import ish.oncourse.server.api.v1.model.ProductTypeDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.Article
import ish.oncourse.server.cayenne.ArticleCustomField
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.CustomField
import ish.oncourse.server.cayenne.Discount
import ish.oncourse.server.cayenne.DiscountMembership
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.InvoiceLine
import ish.oncourse.server.cayenne.InvoiceLineDiscount
import ish.oncourse.server.cayenne.Membership
import ish.oncourse.server.cayenne.MembershipCustomField
import ish.oncourse.server.cayenne.MembershipProduct
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentInLine
import ish.oncourse.server.cayenne.ProductItem

import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.cayenne.Tax
import ish.oncourse.server.cayenne.Voucher
import ish.oncourse.server.cayenne.VoucherCustomField
import ish.oncourse.server.cayenne.VoucherProduct

import static ish.oncourse.server.api.v1.function.CustomFieldFunctions.updateCustomFields
import static ish.oncourse.server.api.v1.function.CustomFieldFunctions.validateCustomFields
import static ish.util.LocalDateUtils.dateToValue
import static ish.util.LocalDateUtils.valueToDate
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.validation.ValidationResult
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.time.LocalDate
import java.time.ZoneId
import java.util.stream.Collectors

class ProductItemApiService extends EntityApiService<ProductItemDTO, ProductItem, ProductItemDAO> {

    @Inject
    private ProductItemDAO productItemDAO

    @Inject
    private ContactDao contactDao

    @Inject
    private AccountDao accountDao

    @Inject
    private TaxDao taxDao

    @Inject
    private RefundService refundService

    private static final Logger logger = LogManager.logger

    @Override
    Class<ProductItem> getPersistentClass() {
        return ProductItem
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    @Override
    ProductItemDTO toRestModel(ProductItem productItem) {
        new ProductItemDTO().with { productItemDTO ->

            ProductTypeDTO type = ProductTypeDTO.fromValue(TypesUtil.getEnumForDatabaseValue(productItem.type, ProductType).toString())

            productItemDTO.id = productItem.id
            productItemDTO.productId = productItem.product.id
            productItemDTO.productType = type
            productItemDTO.productName = productItem.product.name
            productItemDTO.purchasedById = getPurchasedBy(type, productItem)
            productItemDTO.purchasedByName = getPurchasedByName(type, productItem)
            LocalDate expiryDate = dateToValue(productItem.expiryDate)
            productItemDTO.expiresOn = expiryDate
            productItemDTO.purchasedOn = dateToValue(productItem.createdOn)
            productItemDTO.purchasePrice = productItem.invoiceLine?.priceEachExTax?.toBigDecimal()
            productItemDTO.status = ProductItemStatusDTO.fromValue(productItem.displayStatus.getDisplayName())
            productItemDTO.validFrom = type == ProductTypeDTO.MEMBERSHIP ? getValidFrom(productItem as Membership) : null
            productItemDTO.valueRemaining = type == ProductTypeDTO.VOUCHER ? getValueRemaining(productItem as Voucher) : null
            productItemDTO.voucherCode = type == ProductTypeDTO.VOUCHER ? (productItem as Voucher).code : null
            productItemDTO.redeemableById = type == ProductTypeDTO.VOUCHER ? (productItem as Voucher).redeemableBy?.id : null
            productItemDTO.redeemableByName = (type == ProductTypeDTO.VOUCHER && (productItem as Voucher).redeemableBy != null) ? GetContactFullName.valueOf((productItem as Voucher).redeemableBy, true).get() : null
            productItemDTO.payments = getPayments(type, productItem)
            switch (type) {
                case ProductTypeDTO.PRODUCT:
                    productItemDTO.customFields = (productItem as Article).customFields.collectEntries {[(it.customFieldType.key) : it.value] }
                    break
                case ProductTypeDTO.MEMBERSHIP:
                    productItemDTO.customFields = (productItem as Membership).customFields.collectEntries {[(it.customFieldType.key) : it.value] }
                    break
                case ProductTypeDTO.VOUCHER:
                    productItemDTO.customFields = (productItem as Voucher).customFields.collectEntries {[(it.customFieldType.key) : it.value] }
                    break
            }

            return productItemDTO
        }
    }

    private static ProductItemPaymentDTO toProductItemPayment(PaymentInLine paymentInLine) {
        new ProductItemPaymentDTO().with { pip ->
            pip.id = paymentInLine.invoice.id
            if (paymentInLine.paymentIn.createdOn) {
                pip.createdOn = paymentInLine.paymentIn.createdOn.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
            }
            pip.source = paymentInLine.paymentIn.source
            pip.invoiceNo = paymentInLine.invoice.invoiceNumber
            pip.amount = paymentInLine.amount.toBigDecimal()
            pip
        }
    }

    private static ProductItemPaymentDTO toProductItemPayment(InvoiceLine invoiceLine) {
        new ProductItemPaymentDTO().with { pip ->
            pip.id = invoiceLine.invoice.id
            pip.source = invoiceLine.invoice.source
            if (invoiceLine.createdOn) {
                pip.createdOn = invoiceLine.createdOn.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
            }
            pip.invoiceNo = invoiceLine.invoice.invoiceNumber
            pip
        }
    }

    private static List<ProductItemPaymentDTO> getPayments(ProductTypeDTO productTypeDTO, ProductItem productItem) {
        if (productTypeDTO == ProductTypeDTO.MEMBERSHIP) {
            Membership membership = productItem as Membership
            List<InvoiceLine> invoiceLines = ObjectSelect.query(InvoiceLine)
                    .where(InvoiceLine.INVOICE_LINE_DISCOUNTS.dot(InvoiceLineDiscount.DISCOUNT)
                            .dot(Discount.DISCOUNT_MEMBERSHIPS)
                            .dot(DiscountMembership.MEMBERSHIP_PRODUCT)
                            .eq(membership.product as MembershipProduct))
                    .and(InvoiceLine.ENROLMENT.dot(Enrolment.STUDENT)
                            .dot(Student.CONTACT)
                            .eq(membership.getContact()))
                    .select(membership.context)

            return invoiceLines.stream().map { toProductItemPayment(it) }.collect(Collectors.toList())
        }
        if (productTypeDTO == ProductTypeDTO.VOUCHER) {
            Set<PaymentIn> paymentIns = (productItem as Voucher).getVoucherPaymentsIn().stream().map {
                it.paymentIn
            }.collect(Collectors.toSet())

            return paymentIns.stream().flatMap { it.paymentInLines.findAll().stream() }.map {
                toProductItemPayment(it)
            }.collect(Collectors.toList())
        }

        return new ArrayList<ProductItemPaymentDTO>()
    }

    private static String getValueRemaining(Voucher voucher) {
        def voucherProduct = voucher.product as VoucherProduct
        if (voucherProduct.maxCoursesRedemption) {
            return voucherProduct.maxCoursesRedemption - voucher.redeemedCourseCount + " classes"
        }
        return voucher.redemptionValue.toString()
    }

    private static LocalDate getValidFrom(Membership membership) {
        if (membership.expiryDate != null) {

            Calendar c = new GregorianCalendar()
            c.setTime(membership.expiryDate)

            if (membership.product.expiryType == ExpiryType.LIFETIME) {
                return dateToValue(membership.createdOn)
            } else if (membership.product.expiryType == ExpiryType.DAYS) {
                c.add(Calendar.DATE, 0 - membership.product.expiryDays)
            } else {
                c.add(Calendar.YEAR, -1)
            }
            Date result = c.getTime()
            if (result.before(membership.createdOn)) {
                result = membership.createdOn
            }
            return dateToValue(result)
        }
        return null
    }

    private static Long getPurchasedBy(ProductTypeDTO productTypeDTO, ProductItem productItem) {
        switch (productTypeDTO) {
            case ProductTypeDTO.MEMBERSHIP:
                return productItem.contact?.id
                break
            case ProductTypeDTO.VOUCHER:
                return productItem.invoiceLine?.invoice?.contact?.id
                break
            default:
                return productItem.contact?.id
        }
    }

    private static String getPurchasedByName(ProductTypeDTO productTypeDTO, ProductItem productItem) {
        Contact contact
        switch (productTypeDTO) {
            case ProductTypeDTO.MEMBERSHIP:
                contact = productItem.contact
                break
            case ProductTypeDTO.VOUCHER:
                contact = productItem.invoiceLine?.invoice?.contact
                break
            default:
                contact = productItem.contact
        }
        if (contact != null) {
            return GetContactFullName.valueOf(contact, true).get()
        }
        return null
    }


    @Override
    ProductItem toCayenneModel(ProductItemDTO productItemDTO, ProductItem productItem) {
        productItem.expiryDate = valueToDate(productItemDTO.expiresOn)

        if (ProductItemStatusDTO.DELIVERED == productItemDTO.status && productItem instanceof Article && ProductStatus.DELIVERED != productItem.status) {
            productItem.status = ProductStatus.DELIVERED
        }

        if (productItemDTO.productType == ProductTypeDTO.VOUCHER) {
            Voucher voucher = productItem as Voucher
            if (productItemDTO.redeemableById != null) {
                voucher.redeemableBy = contactDao.getById(productItem.context, productItemDTO.redeemableById)
            } else {
                voucher.redeemableBy = null
            }
        }
        switch (productItemDTO.productType) {
            case ProductTypeDTO.PRODUCT:
                updateCustomFields(productItem.context, productItem as Article, productItemDTO.customFields, ArticleCustomField)
                break
            case ProductTypeDTO.MEMBERSHIP:
                updateCustomFields(productItem.context, productItem as Membership, productItemDTO.customFields, MembershipCustomField)
                break
            case ProductTypeDTO.VOUCHER:
                updateCustomFields(productItem.context, productItem as Voucher, productItemDTO.customFields, VoucherCustomField)
                break
        }
        productItem
    }

    @Override
    void validateModelBeforeSave(ProductItemDTO productItemDTO, ObjectContext context, Long id) {
        if (id != null) {
            ProductItem productItem = productItemDAO.getById(context, id)
            if (productItem == null) {
                validator.throwClientErrorException(id, "id", "ProductItem with id=$id doesn't exist.")
            }
            if (productItem.status != ProductStatus.ACTIVE) {
                validator.throwClientErrorException(id, "id", "Only ProductItem with active status can be modified.")
            }
            def expiryDate = dateToValue(productItem.expiryDate)
            if (productItemDTO.expiresOn != expiryDate && LocalDate.now().isAfter(expiryDate)) {
                validator.throwClientErrorException(id, "expiresOn", "Only ProductItem with active status can be modified.")
            }
            if (productItemDTO.productType == ProductTypeDTO.VOUCHER &&
                productItemDTO.redeemableById != (productItem as Voucher).redeemableBy?.id &&
                productItemDTO.redeemableById != null &&
                contactDao.getById(context, productItemDTO.redeemableById) == null) {
                validator.throwClientErrorException(id, "redeemableById", "Contact with id=${productItemDTO.redeemableById} doesn't exist.")
            }
        }

        validateCustomFields(context, ProductItem.class.simpleName, productItemDTO.customFields, id as String, validator)
    }

    @Override
    void validateModelBeforeRemove(ProductItem productItem) {
        throw new Exception("Unsupported operation")
    }


    void cancel(ProductItemCancelDTO cancelRequest) {

        ObjectContext context = cayenneService.newContext
        ProductItem producItem = productItemDAO.getById(context, cancelRequest.id)
        ValidationErrorDTO error = validateForCancel(cancelRequest, producItem)
        if (error) {
            validator.throwClientErrorException(error)
        } else if (cancelRequest.createCrediNote) {
            Money fee = Money.ZERO
            Account account = null
            Tax tax = null
            if (cancelRequest.retainAdministrativeFee) {
                fee = Money.valueOf(cancelRequest.feeAmount)
                account = accountDao.getById(context, cancelRequest.retainAccountId)
                tax = taxDao.getById(context, cancelRequest.feeTaxId)
            }

            ValidationResult validation = refundService.refundInvoiceLine(producItem.invoiceLine, fee, tax,  account, producItem.class.simpleName)

            if (validation && validation.hasFailures()) {
                context.rollbackChanges()
                logger.error('Could not cancel sale.')
                logger.error(cancelRequest.toString())
                validation.failures.each { logger.error(it.description) }
                validator.throwClientErrorException(new ValidationErrorDTO(errorMessage: validation.failures[0].description ))

            }
            producItem.status = ProductStatus.CREDITED

        } else {
            producItem.status = ProductStatus.CANCELLED
        }
        context.commitChanges()
    }


    private ValidationErrorDTO validateForCancel(ProductItemCancelDTO cancelRequest, ProductItem productItem) {

        if(!productItem) {
            return new ValidationErrorDTO(errorMessage: 'There is no sale by provided id')
        }

        if(ProductStatus.ACTIVE != productItem.status) {
            return new ValidationErrorDTO(id:productItem.id.toString(), propertyName: 'status', errorMessage: 'There is no sale by provided id')
        }

        if (productItem instanceof Voucher && cancelRequest.createCrediNote && isPartiallyRedeemed((Voucher) productItem)) {
            return new ValidationErrorDTO(id: productItem.id.toString(), errorMessage: 'Voucher partially redeemed. No automatic credit note can be created. You can create a credit note manually')
        }

        return null
    }


    private boolean isPartiallyRedeemed(Voucher v) {
        if (v.voucherProduct.maxCoursesRedemption == null) {
            return v.redemptionValue.isLessThan(v.valueOnPurchase)
        } else {
            return v.redeemedCourseCount > 0
        }
    }
}
