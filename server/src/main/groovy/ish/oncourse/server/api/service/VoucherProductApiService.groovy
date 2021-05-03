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
import ish.common.types.AccountType
import ish.common.types.ExpiryType
import ish.math.Money
import ish.oncourse.server.api.dao.AccountDao
import ish.oncourse.server.api.dao.CorporatePassDao
import ish.oncourse.server.api.dao.CorporatePassProductDao
import ish.oncourse.server.api.dao.CourseDao
import ish.oncourse.server.api.dao.EntityRelationDao
import ish.oncourse.server.api.dao.ProductDao
import ish.oncourse.server.api.dao.TaxDao
import ish.oncourse.server.api.dao.VoucherProductCourseDao
import ish.oncourse.server.api.dao.VoucherProductDao
import ish.oncourse.server.cayenne.Product

import static ish.oncourse.server.api.function.MoneyFunctions.toMoneyValue
import static ish.oncourse.server.api.v1.function.EntityRelationFunctions.toRestFromEntityRelation
import static ish.oncourse.server.api.v1.function.EntityRelationFunctions.toRestToEntityRelation
import static ish.oncourse.server.api.v1.function.ProductFunctions.updateCorporatePassesByIds
import static ish.oncourse.server.api.v1.model.ProductStatusDTO.CAN_BE_PURCHASED_IN_OFFICE
import static ish.oncourse.server.api.v1.model.ProductStatusDTO.CAN_BE_PURCHASED_IN_OFFICE_ONLINE
import static ish.oncourse.server.api.v1.model.ProductStatusDTO.DISABLED
import ish.oncourse.server.api.v1.model.VoucherCorporatePassDTO
import ish.oncourse.server.api.v1.model.VoucherProductCourseDTO
import ish.oncourse.server.api.v1.model.VoucherProductDTO
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.VoucherProduct
import org.apache.cayenne.ObjectContext
import static org.apache.commons.lang3.StringUtils.isBlank
import static org.apache.commons.lang3.StringUtils.isNotBlank
import static org.apache.commons.lang3.StringUtils.trimToNull

import java.time.ZoneId

@CompileStatic
class VoucherProductApiService extends EntityApiService<VoucherProductDTO, VoucherProduct, VoucherProductDao> {

    @Inject
    private AccountDao accountDao

    @Inject
    private CorporatePassDao corporatePassDao

    @Inject
    private CorporatePassProductDao corporatePassProductDao

    @Inject
    private CourseDao courseDao

    @Inject
    private VoucherProductCourseDao voucherProductCourseDao

    @Inject
    private ProductDao productDao

    @Inject
    private TaxDao taxDao

    @Override
    Class<VoucherProduct> getPersistentClass() {
        return VoucherProduct
    }

    @Override
    VoucherProductDTO toRestModel(VoucherProduct voucherProduct) {
        new VoucherProductDTO().with { voucherProductDTO ->
            voucherProductDTO.id = voucherProduct.id
            voucherProductDTO.name = voucherProduct.name
            voucherProductDTO.code = voucherProduct.sku
            voucherProductDTO.feeExTax = voucherProduct.priceExTax?.toBigDecimal()
            voucherProductDTO.liabilityAccountId = voucherProduct.liabilityAccount?.id
            voucherProductDTO.expiryDays = voucherProduct.expiryDays
            voucherProductDTO.value = voucherProduct.value?.toBigDecimal()
            voucherProductDTO.maxCoursesRedemption = voucherProduct.maxCoursesRedemption
            voucherProductDTO.courses = voucherProduct.voucherProductCourses.collect { vpc ->
                new VoucherProductCourseDTO().with { it ->
                    it.id = vpc.course.id
                    it.code = vpc.course.code
                    it.name = vpc.course.name
                    it
                }
            }
            voucherProductDTO.description = voucherProduct.description
            voucherProductDTO.status = voucherProduct.isOnSale ? voucherProduct.isWebVisible ? CAN_BE_PURCHASED_IN_OFFICE_ONLINE : CAN_BE_PURCHASED_IN_OFFICE : DISABLED
            voucherProductDTO.corporatePasses = voucherProduct.corporatePassProducts.collect { cp ->
                new VoucherCorporatePassDTO().with { vcp ->
                    vcp.id = cp.corporatePass.id
                    vcp.contactFullName = cp.corporatePass.contact.fullName
                    vcp
                }
            }
            voucherProductDTO.soldVouchersCount = voucherProduct.getProductItems().size()
            voucherProductDTO.relatedSellables = (EntityRelationDao.getRelatedFrom(voucherProduct.context, Product.simpleName, voucherProduct.id).collect { toRestFromEntityRelation(it) } +
                    EntityRelationDao.getRelatedTo(voucherProduct.context, Product.simpleName, voucherProduct.id).collect { toRestToEntityRelation(it) })
            voucherProductDTO.createdOn = voucherProduct.createdOn?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
            voucherProductDTO.modifiedOn = voucherProduct.modifiedOn?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
            voucherProductDTO.customFields = voucherProduct.customFields.collectEntries {[(it.customFieldType.key) : it.value] }
            voucherProductDTO
        }
    }

    @Override
    VoucherProduct toCayenneModel(VoucherProductDTO voucherProductDTO, VoucherProduct voucherProduct) {
        voucherProduct.name = trimToNull(voucherProductDTO.name)
        voucherProduct.sku = trimToNull(voucherProductDTO.code)
        voucherProduct.priceExTax = toMoneyValue(voucherProductDTO.feeExTax)
        voucherProduct.liabilityAccount = accountDao.getById(voucherProduct.context, voucherProductDTO.liabilityAccountId.toLong())
        voucherProduct.expiryDays = voucherProductDTO.expiryDays
        voucherProduct.value = toMoneyValue(voucherProductDTO.value)
        voucherProduct.maxCoursesRedemption = voucherProductDTO.maxCoursesRedemption
        voucherProduct.description = trimToNull(voucherProductDTO.description)
        voucherProduct.isOnSale = voucherProductDTO.status == CAN_BE_PURCHASED_IN_OFFICE_ONLINE || voucherProductDTO.status == CAN_BE_PURCHASED_IN_OFFICE
        voucherProduct.isWebVisible = voucherProductDTO.status == CAN_BE_PURCHASED_IN_OFFICE_ONLINE
        updateCorporatePassesByIds(voucherProduct, voucherProductDTO.corporatePasses*.id.findAll(), corporatePassProductDao, corporatePassDao)
        updateCourses(voucherProduct, voucherProductDTO.courses)
        if (voucherProduct.newRecord) {
            voucherProduct.tax = taxDao.getNonSupplyTax(voucherProduct.context)
            voucherProduct.taxAdjustment = Money.ZERO
            voucherProduct.setExpiryType(ExpiryType.DAYS)
        }
        voucherProduct
    }

    @Override
    void validateModelBeforeSave(VoucherProductDTO voucherProductDTO, ObjectContext context, Long id) {
        if (isBlank(voucherProductDTO.name)) {
            validator.throwClientErrorException(id, 'name', 'Name is required.')
        } else if (trimToNull(voucherProductDTO.name).length() > 500) {
            validator.throwClientErrorException(id, 'name', 'Name cannot be more than 500 chars.')
        }

        if (isBlank(voucherProductDTO.code)) {
            validator.throwClientErrorException(id, 'code', 'Code is required.')
        } else if (trimToNull(voucherProductDTO.code).length() > 10) {
            validator.throwClientErrorException(id, 'code', 'Code cannot be more than 10 chars.')
        } else if (!voucherProductDTO.code.matches('^\\w+(\\.\\w+)*$')) {
            validator.throwClientErrorException(id, 'code', 'Code must start and end with letters or numbers and can contain full stops.')
        } else {
            Long articleId = productDao.getByCode(context, trimToNull(voucherProductDTO.code))?.id
            if (articleId && articleId != id) {
                validator.throwClientErrorException(id, 'code', 'Code must be unique.')
            }
        }

        if(id != null) {
            VoucherProduct voucherProduct = (VoucherProduct) productDao.getById(context, id)
            if (voucherProduct.getProductItems().size() > 0) {
                if (!isRedemptionTypeEqual(voucherProductDTO, voucherProduct)) {
                    validator.throwClientErrorException(id, 'maxCoursesRedemption | feeExTax', 'Wrong value')
                }
                if (!isCoursesEqual(voucherProductDTO, voucherProduct)) {
                    validator.throwClientErrorException(id, 'courses', 'Wrong value')
                }
            }
        }

        if (voucherProductDTO.feeExTax && toMoneyValue(voucherProductDTO.feeExTax).isNegative()) {
            validator.throwClientErrorException(id, 'value', 'Voucher price cannot be negative.')
        }

        if (!voucherProductDTO.expiryDays || voucherProductDTO.expiryDays < 1) {
            validator.throwClientErrorException(id, 'expiryDays', 'Expiry days value must be greater then zero.')
        }

        if (voucherProductDTO.status == null) {
            validator.throwClientErrorException(id, 'status', 'Status is required.')
        }

        if (isNotBlank(voucherProductDTO.description) && trimToNull(voucherProductDTO.description).length() > 8000) {
            validator.throwClientErrorException(id, 'description', 'Description cannot be more than 8000 chars.')
        }

        if (!voucherProductDTO.liabilityAccountId) {
            validator.throwClientErrorException(id, 'liabilityAccountId', 'liabilityAccountId is required for account entity.')
        } else {
            Account account = accountDao.getById(context, voucherProductDTO.liabilityAccountId)
            if (!account) {
                validator.throwClientErrorException(id, 'liabilityAccount', "Account with id=$voucherProductDTO.liabilityAccountId doesn't exist.")
            } else if (account.type != AccountType.LIABILITY) {
                validator.throwClientErrorException(id, 'liabilityAccount', "Only accounts of liability type can be assigned to voucher.")
            }
        }

        voucherProductDTO.corporatePasses.each {
            if (!it.id) {
                validator.throwClientErrorException(id, 'corporatePasses', 'Id is required for corporate pass entity.')
            } else if (!corporatePassDao.getById(context, it.id)) {
                validator.throwClientErrorException(id, 'corporatePasses', "CorporatePass with id=$it.id doesn't exist.")
            }
        }

        if (voucherProductDTO.value && voucherProductDTO.maxCoursesRedemption && voucherProductDTO.maxCoursesRedemption > 0 ) {
            validator.throwClientErrorException(id, 'value', 'Redeem value and redeemed classes are not available in the same time')
        }

        if (voucherProductDTO.courses.empty) {
            if (voucherProductDTO.maxCoursesRedemption) {
                validator.throwClientErrorException(id, 'maxCoursesRedemption', 'If voucher product is not linked to any course then maxCoursesRedemption should be null.')
            }
        } else {
            voucherProductDTO.courses.each {
                if (!it.id) {
                    validator.throwClientErrorException(id, 'courses', 'Id is required for course entity.')
                } else if (!courseDao.getById(context, it.id)) {
                    validator.throwClientErrorException(id, 'courses', "Course with id=$it.id doesn't exist.")
                }
            }

            if (voucherProductDTO.feeExTax == null || toMoneyValue(voucherProductDTO.feeExTax).isNegative()) {
                validator.throwClientErrorException(id, 'feeExTax', 'If voucher product is linked to courses then price should not be null.')
            }

            if (!voucherProductDTO.maxCoursesRedemption || voucherProductDTO.maxCoursesRedemption < 1) {
                validator.throwClientErrorException(id, 'maxCoursesRedemption', 'If voucher product is linked to courses then maxCoursesRedemption should not be null and be greater than zero.')
            }

            if (voucherProductDTO.value) {
                validator.throwClientErrorException(id, 'value', 'If voucher product is linked to courses then redemption value should not be null.')
            }
        }

        if (voucherProductDTO.value) {
            if (toMoneyValue(voucherProductDTO.value).isNegative()) {
                validator.throwClientErrorException(id, 'value', 'Redemption value cannot be negative.')
            }

            if (voucherProductDTO.feeExTax == null) {
                validator.throwClientErrorException(id, 'feeExTax', 'Fee ex tax is required for money value voucher.')
            }
        }
    }

    @Override
    void validateModelBeforeRemove(VoucherProduct cayenneModel) {
        validator.throwClientErrorException(cayenneModel.id, 'id', 'Product cannot be deleted. Instead of it you can disable it.')
    }

    private void updateCourses(VoucherProduct cayenneModel, List<VoucherProductCourseDTO> courses){
        List<Long> relationsToSave = courses*.id ?: [] as List<Long>
        cayenneModel.context.deleteObjects(cayenneModel.voucherProductCourses.findAll { !relationsToSave.contains(it.course.id) })
        courses.findAll { !cayenneModel.voucherProductCourses*.course*.id.contains(it.id) }.each { VoucherProductCourseDTO dto ->
            voucherProductCourseDao.newObject(cayenneModel.context).with { cpr ->
                cpr.course = courseDao.getById(cayenneModel.context, dto.id)
                cpr.voucherProduct = cayenneModel
                cpr
            }
        }
    }

    static Boolean isRedemptionTypeEqual(VoucherProductDTO voucherProductDTO, VoucherProduct voucherProduct) {
        if ((voucherProductDTO.maxCoursesRedemption == null && voucherProduct.maxCoursesRedemption != null)
                || (voucherProductDTO.maxCoursesRedemption != null && voucherProduct.maxCoursesRedemption == null)) {
            return false
        }
        if ((voucherProductDTO.value == null && voucherProduct.value != null) || (voucherProductDTO.value != null && voucherProduct.value == null)) {
            return false
        }
        return true
    }

    static Boolean isCoursesEqual(VoucherProductDTO voucherProductDTO, VoucherProduct voucherProduct) {
        return voucherProduct.voucherProductCourses.collect { it.course.id }.sort() == voucherProductDTO.courses.collect {
            it.id
        }.sort()
    }
}
