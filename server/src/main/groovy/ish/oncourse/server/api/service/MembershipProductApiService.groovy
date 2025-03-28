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
import ish.common.types.ExpiryType
import ish.math.Money
import ish.oncourse.server.api.dao.*
import ish.oncourse.server.api.v1.function.MembershipFunctions
import ish.oncourse.server.api.v1.model.*
import ish.oncourse.server.cayenne.*
import ish.oncourse.server.document.DocumentService
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext

import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static ish.oncourse.server.api.v1.function.CustomFieldFunctions.updateCustomFields
import static ish.oncourse.server.api.v1.function.DocumentFunctions.toRestDocument
import static ish.oncourse.server.api.v1.function.DocumentFunctions.updateDocuments
import static ish.oncourse.server.api.v1.function.EntityRelationFunctions.toRestFromEntityRelation
import static ish.oncourse.server.api.v1.function.EntityRelationFunctions.toRestToEntityRelation
import static ish.oncourse.server.api.v1.function.MembershipProductFunctions.calculateNextDate
import static ish.oncourse.server.api.v1.function.MembershipProductFunctions.toRestMembershipDiscount
import static ish.oncourse.server.api.v1.function.ProductFunctions.expiryTypeMap
import static ish.oncourse.server.api.v1.function.ProductFunctions.updateCorporatePasses
import static ish.oncourse.server.api.v1.function.TagFunctions.updateTags
import static ish.oncourse.server.api.v1.model.ProductStatusDTO.*
import static ish.util.MoneyUtil.calculateTaxAdjustment
import static ish.util.MoneyUtil.getPriceIncTax
import static org.apache.commons.lang3.StringUtils.*

class MembershipProductApiService extends TaggableApiService<MembershipProductDTO, MembershipProduct, MembershipProductDao> {

    @Inject
    private AccountDao accountDao

    @Inject
    private DocumentService documentService

    @Inject
    private ContactRelationTypeDao contactRelationTypeDao

    @Inject
    private CorporatePassDao corporatePassDao

    @Inject
    private CorporatePassProductDao corporatePassProductDao

    @Inject
    private FieldConfigurationSchemeDao fieldConfigurationSchemeDao

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
            membershipProductDTO.membershipDiscounts = membershipProduct.discountMemberships.collect { toRestMembershipDiscount(it) }
            membershipProductDTO.relatedSellables = (EntityRelationDao.getRelatedFrom(membershipProduct.context, Product.simpleName, membershipProduct.id).collect { toRestFromEntityRelation(it) } +
                    EntityRelationDao.getRelatedTo(membershipProduct.context, Product.simpleName, membershipProduct.id).collect { toRestToEntityRelation(it) })
            membershipProductDTO.createdOn = LocalDateUtils.dateToTimeValue(membershipProduct.createdOn)
            membershipProductDTO.modifiedOn = LocalDateUtils.dateToTimeValue(membershipProduct.modifiedOn)
            membershipProductDTO.dataCollectionRuleId = membershipProduct.fieldConfigurationScheme?.id
            membershipProductDTO.documents = membershipProduct.activeAttachments.collect { toRestDocument(it.document, documentService) }
            membershipProductDTO.tags = membershipProduct.allTags.collect { it.id }
            membershipProductDTO.customFields = membershipProduct.customFields.collectEntries { [(it.customFieldType.key): it.value] }
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
        membershipProduct.priceExTax = Money.exactOf(membershipProductDTO.feeExTax)
        membershipProduct.tax = taxDao.getById(context, membershipProductDTO.taxId.toLong())
        membershipProduct.taxAdjustment = calculateTaxAdjustment(Money.exactOf(membershipProductDTO.totalFee), membershipProduct.priceExTax, membershipProduct.tax.rate)
        membershipProduct.expiryType = expiryTypeMap.getByValue(membershipProductDTO.expiryType)
        membershipProduct.expiryDays = membershipProductDTO.expiryDays
        membershipProduct.incomeAccount = accountDao.getById(context, membershipProductDTO.incomeAccountId.toLong())
        membershipProduct.isOnSale = membershipProductDTO.status == CAN_BE_PURCHASED_IN_OFFICE_ONLINE || membershipProductDTO.status == CAN_BE_PURCHASED_IN_OFFICE
        membershipProduct.isWebVisible = membershipProductDTO.status == CAN_BE_PURCHASED_IN_OFFICE_ONLINE
        membershipProduct.fieldConfigurationScheme = membershipProductDTO.dataCollectionRuleId ?
                fieldConfigurationSchemeDao.getById(membershipProduct.context, membershipProductDTO.dataCollectionRuleId) :
                null as FieldConfigurationScheme
        updateCorporatePasses(membershipProduct, membershipProductDTO.corporatePasses, corporatePassProductDao, corporatePassDao)
        updateDocuments(membershipProduct, membershipProduct.attachmentRelations, membershipProductDTO.documents, MembershipProductAttachmentRelation, context)
        updateTags(membershipProduct, membershipProduct.taggingRelations, membershipProductDTO.tags, MembershipProductTagRelation, context)
        updateCustomFields(membershipProduct.context, membershipProduct, membershipProductDTO.customFields, membershipProduct.customFieldClass)
        updateDiscountMemberships(membershipProduct, membershipProductDTO.membershipDiscounts)
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
            Money adjustment = calculateTaxAdjustment(Money.exactOf(membershipProductDTO.totalFee), Money.exactOf(membershipProductDTO.feeExTax), tax.rate)
            if (Math.abs(adjustment.toDouble()) > 0.01) {
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
                validator.throwClientErrorException(id, 'incomeAccount', "Only accounts of income type can be assigned to membership.")
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

    @Override
    Closure getAction(String key, String value) {
        Closure action = super.getAction(key, value)
        if (!action) {
            validator.throwClientErrorException(key, "Unsupported attribute")
        }
        action
    }

    CheckoutMembershipProductDTO getCheckoutModel(Long id, Long contactId) {
        ObjectContext context = cayenneService.newContext
        MembershipProduct membership = getRecordById(context, MembershipProduct.class, id)

        return new CheckoutMembershipProductDTO().with { dto ->
            dto.id = membership.id
            dto.name = membership.name
            dto.code = membership.sku
            dto.description = membership.description
            dto.feeExTax = membership.priceExTax?.toBigDecimal()
            dto.totalFee = getPriceIncTax(membership.priceExTax, membership.tax?.rate, membership.taxAdjustment)?.toBigDecimal()

            Date expiryDate = null
            switch (membership.expiryType) {
                case ExpiryType.DAYS:
                    expiryDate = new Date().plus(membership.expiryDays != null ? membership.expiryDays : 0)
                    break
                case ExpiryType.FIRST_JANUARY:
                    expiryDate = calculateNextDate(1, Calendar.JANUARY)
                    break
                case ExpiryType.FIRST_JULY:
                    expiryDate = calculateNextDate(1, Calendar.JULY)
                    break
            }
            def membershipItem = new Membership().with {
                it.expiryDate = expiryDate
                it.product = membership
                it
            }
            if (contactId != null) {
                Contact contact = getRecordById(context, Contact.class, contactId, Contact.PRODUCT_ITEMS.joint())
                Date renewalDate = MembershipFunctions.getRenwevalExpiryDate(contact, membershipItem)
                dto.expiresOn = LocalDateUtils.dateToValue(renewalDate ?: expiryDate)
            }
            dto
        }
    }
}
