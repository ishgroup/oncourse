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
import ish.common.types.AccountType
import ish.math.Money
import ish.oncourse.server.api.dao.AccountDao
import ish.oncourse.server.api.dao.ContactRelationTypeDao
import ish.oncourse.server.api.dao.CorporatePassDao
import ish.oncourse.server.api.dao.CorporatePassProductDao
import ish.oncourse.server.api.dao.DiscountDao
import ish.oncourse.server.api.dao.DiscountMembershipDao
import ish.oncourse.server.api.dao.DiscountMembershipRelationTypeDao
import ish.oncourse.server.api.dao.EntityRelationDao
import ish.oncourse.server.api.dao.MembershipProductDao
import ish.oncourse.server.api.dao.ProductDao
import ish.oncourse.server.api.dao.TaxDao
import static ish.oncourse.server.api.v1.function.CustomFieldFunctions.updateCustomFields
import ish.oncourse.server.cayenne.Product

import static ish.oncourse.server.api.function.MoneyFunctions.toMoneyValue
import ish.oncourse.server.api.v1.function.MembershipProductFunctions

import static ish.oncourse.server.api.v1.function.EntityRelationFunctions.toRestFromEntityRelation
import static ish.oncourse.server.api.v1.function.EntityRelationFunctions.toRestToEntityRelation
import static ish.oncourse.server.api.v1.function.ProductFunctions.expiryTypeMap
import static ish.oncourse.server.api.v1.function.ProductFunctions.updateCorporatePasses
import ish.oncourse.server.api.v1.model.ExpiryTypeDTO
import ish.oncourse.server.api.v1.model.MembershipCorporatePassDTO
import ish.oncourse.server.api.v1.model.MembershipDiscountDTO
import ish.oncourse.server.api.v1.model.MembershipProductDTO
import static ish.oncourse.server.api.v1.model.ProductStatusDTO.CAN_BE_PURCHASED_IN_OFFICE
import static ish.oncourse.server.api.v1.model.ProductStatusDTO.CAN_BE_PURCHASED_IN_OFFICE_ONLINE
import static ish.oncourse.server.api.v1.model.ProductStatusDTO.DISABLED
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.CorporatePass
import ish.oncourse.server.cayenne.DiscountMembership
import ish.oncourse.server.cayenne.DiscountMembershipRelationType
import ish.oncourse.server.cayenne.MembershipProduct
import ish.oncourse.server.cayenne.Tax
import static ish.util.MoneyUtil.calculateTaxAdjustment
import static ish.util.MoneyUtil.getPriceIncTax
import org.apache.cayenne.ObjectContext
import static org.apache.commons.lang3.StringUtils.isBlank
import static org.apache.commons.lang3.StringUtils.isNotBlank
import static org.apache.commons.lang3.StringUtils.trimToNull

import java.time.ZoneId

class MembershipProductApiService extends EntityApiService<MembershipProductDTO, MembershipProduct, MembershipProductDao> {

    @Inject
    private AccountDao accountDao

    @Inject
    private ContactRelationTypeDao contactRelationTypeDao

    @Inject
    private CorporatePassDao corporatePassDao

    @Inject
    private CorporatePassProductDao corporatePassProductDao

    @Inject
    private ProductDao productDao

    @Inject
    private TaxDao taxDao

    @Inject
    private DiscountDao discountDao

    @Inject
    private DiscountMembershipDao discountMembershipDao

    @Inject
    private DiscountMembershipRelationTypeDao discountMembershipRelationTypeDao

    @Override
    Class<MembershipProduct> getPersistentClass() {
        return MembershipProduct
    }

    @Override
    MembershipProductDTO toRestModel(MembershipProduct membershipProduct) {
        new MembershipProductDTO().with { membershipProductDTO ->
            membershipProductDTO.id = membershipProduct.id
            membershipProductDTO.name = membershipProduct.name
            membershipProductDTO.code = membershipProduct.sku
            membershipProductDTO.description = membershipProduct.description
            membershipProductDTO.feeExTax = membershipProduct.priceExTax?.toBigDecimal()
            membershipProductDTO.totalFee = getPriceIncTax(membershipProduct.priceExTax, membershipProduct.tax?.rate, membershipProduct.taxAdjustment)?.toBigDecimal()
            membershipProductDTO.taxId = membershipProduct.tax?.id
            membershipProductDTO.expiryType = expiryTypeMap[membershipProduct.expiryType]
            membershipProductDTO.expiryDays = membershipProduct.expiryDays
            membershipProductDTO.incomeAccountId = membershipProduct.incomeAccount?.id
            membershipProductDTO.status = membershipProduct.isOnSale ? membershipProduct.isWebVisible ? CAN_BE_PURCHASED_IN_OFFICE_ONLINE : CAN_BE_PURCHASED_IN_OFFICE : DISABLED
            membershipProductDTO.corporatePasses = membershipProduct.corporatePassProducts.collect { toRestMembershipCorporatePass(it.corporatePass) }
            membershipProductDTO.membershipDiscounts = membershipProduct.discountMemberships.collect { MembershipProductFunctions.toRestMembershipDiscount(it) }
            membershipProductDTO.relatedSellables = (EntityRelationDao.getRelatedFrom(membershipProduct.context, Product.simpleName, membershipProduct.id).collect { toRestFromEntityRelation(it) } +
                    EntityRelationDao.getRelatedTo(membershipProduct.context, Product.simpleName, membershipProduct.id).collect { toRestToEntityRelation(it) })
            membershipProductDTO.createdOn = membershipProduct.createdOn?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
            membershipProductDTO.modifiedOn = membershipProduct.modifiedOn?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
            membershipProductDTO.customFields = membershipProduct.customFields.collectEntries {[(it.customFieldType.key) : it.value] }
            membershipProductDTO
        }
    }

    static MembershipCorporatePassDTO toRestMembershipCorporatePass(CorporatePass corporatePass) {
        new MembershipCorporatePassDTO().with { mcp ->
            mcp.id = corporatePass.id
            mcp.contactFullName = corporatePass.contact.fullName
            mcp
        }
    }

    @Override
    MembershipProduct toCayenneModel(MembershipProductDTO membershipProductDTO, MembershipProduct membershipProduct) {
        ObjectContext context = membershipProduct.context

        membershipProduct.name = trimToNull(membershipProductDTO.name)
        membershipProduct.sku = trimToNull(membershipProductDTO.code)
        membershipProduct.description = trimToNull(membershipProductDTO.description)
        membershipProduct.priceExTax = toMoneyValue(membershipProductDTO.feeExTax)
        membershipProduct.tax = taxDao.getById(context, membershipProductDTO.taxId.toLong())
        membershipProduct.taxAdjustment = calculateTaxAdjustment(toMoneyValue(membershipProductDTO.totalFee), membershipProduct.priceExTax, membershipProduct.tax.rate)
        membershipProduct.expiryType = expiryTypeMap.getByValue(membershipProductDTO.expiryType)
        membershipProduct.expiryDays = membershipProductDTO.expiryDays
        membershipProduct.incomeAccount = accountDao.getById(context, membershipProductDTO.incomeAccountId.toLong())
        membershipProduct.isOnSale = membershipProductDTO.status == CAN_BE_PURCHASED_IN_OFFICE_ONLINE || membershipProductDTO.status == CAN_BE_PURCHASED_IN_OFFICE
        membershipProduct.isWebVisible = membershipProductDTO.status == CAN_BE_PURCHASED_IN_OFFICE_ONLINE
        updateCorporatePasses(membershipProduct, membershipProductDTO.corporatePasses, corporatePassProductDao, corporatePassDao)
        updateDiscountMemberships(membershipProduct, membershipProductDTO.membershipDiscounts)
        updateCustomFields(context, membershipProduct, membershipProductDTO.customFields, membershipProduct.customFieldClass)
        membershipProduct
    }

    @Override
    void validateModelBeforeSave(MembershipProductDTO membershipProductDTO, ObjectContext context, Long id) {
        if (isBlank(membershipProductDTO.name)) {
            validator.throwClientErrorException(id, 'name', 'Name is required.')
        } else if (trimToNull(membershipProductDTO.name).length() > 500) {
            validator.throwClientErrorException(id, 'name', 'Name cannot be more than 500 chars.')
        }

        if (isBlank(membershipProductDTO.code)) {
            validator.throwClientErrorException(id, 'code', 'Code is required.')
        } else if (trimToNull(membershipProductDTO.code).length() > 10) {
            validator.throwClientErrorException(id, 'code', 'Code cannot be more than 10 chars.')
        } else if (!membershipProductDTO.code.matches('^\\w+(\\.\\w+)*$')) {
            validator.throwClientErrorException(id, 'code', 'Code must start and end with letters or numbers and can contain full stops.')
        } else {
            Long articleId = productDao.getByCode(context, trimToNull(membershipProductDTO.code))?.id
            if (articleId && articleId != id) {
                validator.throwClientErrorException(id, 'code', 'Code must be unique.')
            }
        }

        if (membershipProductDTO.feeExTax == null) {
            validator.throwClientErrorException(id, 'feeExTax', 'Fee ex tax is required.')
        }
        if (membershipProductDTO.feeExTax < 0) {
            validator.throwClientErrorException(id, 'feeExTax', 'Fee ex tax must be non negative.')
        }
        if (membershipProductDTO.totalFee == null) {
            validator.throwClientErrorException(id, 'totalFee', 'Total fee is required.')
        }
        if (membershipProductDTO.totalFee < 0) {
            validator.throwClientErrorException(id, 'totalFee', 'Total fee must be non negative.')
        }

        if (membershipProductDTO.taxId == null) {
            validator.throwClientErrorException(id, 'tax', 'Tax is required.')
        } else {
            Tax tax = taxDao.getById(context, membershipProductDTO.taxId.toLong())
            if (!tax) {
                validator.throwClientErrorException(id, 'tax', "Tax with id=$membershipProductDTO.taxId doesn't exist.")
            }
            Money adjustment = calculateTaxAdjustment(toMoneyValue(membershipProductDTO.totalFee), toMoneyValue(membershipProductDTO.feeExTax), tax.rate)
            if (Math.abs(adjustment.doubleValue()) > 0.01) {
                validator.throwClientErrorException(id, 'tax', "Incorrect money values for product price.")
            }
        }

        if (membershipProductDTO.status == null) {
            validator.throwClientErrorException(id, 'status', 'Status is required.')
        }

        if (isNotBlank(membershipProductDTO.description) && trimToNull(membershipProductDTO.description).length() > 8000) {
            validator.throwClientErrorException(id, 'description', 'Description cannot be more than 8000 chars.')
        }

        if (!membershipProductDTO.incomeAccountId) {
            validator.throwClientErrorException(id, 'incomeAccount', 'Id is required for account entity.')
        } else {
            Account account = accountDao.getById(context, membershipProductDTO.incomeAccountId.toLong())
            if (!account) {
                validator.throwClientErrorException(id, 'incomeAccount', "Account with id=$membershipProductDTO.incomeAccountId doesn't exist.")
            } else if (account.type != AccountType.INCOME) {
                validator.throwClientErrorException(id, 'incomeAccount', "Only accounts of income type can be assigned to voucher.")
            }
        }

        membershipProductDTO.corporatePasses.each {
            if (!it.id) {
                validator.throwClientErrorException(id, 'corporatePasses', 'Id is required for corporate pass entity.')
            } else if (!corporatePassDao.getById(context, it.id)) {
                validator.throwClientErrorException(id, 'corporatePasses', "CorporatePass with id=$it.id doesn't exist.")
            }
        }

        membershipProductDTO.membershipDiscounts.each {
            if (!it.discountId) {
                validator.throwClientErrorException(id, 'membershipDiscounts', 'Id is required for discount entity.')
            } else if (!discountDao.getById(context, it.discountId)) {
                validator.throwClientErrorException(id, 'membershipDiscounts', "Discount with id=$it.discountId doesn't exist.")
            }
            it.contactRelationTypes.each { crtId ->
                if (!contactRelationTypeDao.getById(context, crtId)) {
                    validator.throwClientErrorException(id, 'membershipDiscounts', "Relation type with id=$crtId doesn't exist.")
                }
            }
        }

        if (!membershipProductDTO.expiryType) {
            validator.throwClientErrorException(id, 'expiryType', 'Expiry type is required.')
        } else if (membershipProductDTO.expiryType == ExpiryTypeDTO.DAYS && membershipProductDTO.expiryDays == null) {
            validator.throwClientErrorException(id, 'expiryDays', 'Expiry days is required for this expiry type.')
        } else if (membershipProductDTO.expiryType == ExpiryTypeDTO.DAYS && membershipProductDTO.expiryDays < 0) {
            validator.throwClientErrorException(id, 'expiryDays', 'Expiry days must be non negative.')
        } else if (membershipProductDTO.expiryType != ExpiryTypeDTO.DAYS && membershipProductDTO.expiryDays) {
            validator.throwClientErrorException(id, 'expiryDays', 'Expiry days must be null for this expiry type.')
        }
    }

    @Override
    void validateModelBeforeRemove(MembershipProduct cayenneModel) {
        validator.throwClientErrorException(cayenneModel.id, 'id', 'Product cannot be deleted. Instead of it you can disable it.')
    }

    private void updateDiscountMemberships(MembershipProduct cayenneModel, List<MembershipDiscountDTO> discountMemberships) {
        List<Long> relationsToSave = discountMemberships*.discountId.findAll() as List<Long>
        cayenneModel.context.deleteObjects(cayenneModel.discountMemberships.findAll { !relationsToSave.contains(it.discount.id) })
        List<Long> alreadyExist = cayenneModel.discountMemberships.findAll { relationsToSave.contains(it.discount.id) }*.discount*.id.findAll() as List<Long>
        discountMemberships.each { dm ->
            DiscountMembership discountMembership
            if (alreadyExist.contains(dm.discountId)) {
                discountMembership = cayenneModel.discountMemberships.find { it.discount.id == dm.discountId }
            } else {
                discountMembership = discountMembershipDao.newObject(cayenneModel.context).with { dbDM ->
                    dbDM.discount = discountDao.getById(cayenneModel.context, dm.discountId)
                    dbDM.membershipProduct = cayenneModel
                    dbDM
                }
            }
            discountMembership.applyToMemberOnly = dm.contactRelationTypes.isEmpty()
            updateContactRelationTypes(discountMembership, dm.contactRelationTypes)
        }
    }

    private void updateContactRelationTypes(DiscountMembership discountMembership, List<Long> contactRelationTypeIds) {
        List<Long> relationsToSave = contactRelationTypeIds
        discountMembership.context.deleteObjects(discountMembership.discountMembershipRelationTypes.findAll { !relationsToSave.contains(it.contactRelationType.id) })
        contactRelationTypeIds.findAll { !discountMembership.discountMembershipRelationTypes*.contactRelationType*.id.contains(it) }.each {
            DiscountMembershipRelationType dmrt = discountMembershipRelationTypeDao.newObject(discountMembership.context)
            dmrt.discountMembership = discountMembership
            dmrt.contactRelationType = contactRelationTypeDao.getById(discountMembership.context, it)
            dmrt

        }
    }
}
