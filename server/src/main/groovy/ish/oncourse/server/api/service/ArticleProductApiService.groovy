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
import ish.oncourse.server.api.dao.ArticleProductDao
import ish.oncourse.server.api.dao.CorporatePassDao
import ish.oncourse.server.api.dao.CorporatePassProductDao
import ish.oncourse.server.api.dao.EntityRelationDao
import ish.oncourse.server.api.dao.ProductDao
import ish.oncourse.server.api.dao.TaxDao
import ish.oncourse.server.cayenne.Product

import static ish.oncourse.server.api.function.MoneyFunctions.toMoneyValue
import static ish.oncourse.server.api.v1.function.EntityRelationFunctions.toRestFromEntityRelation
import static ish.oncourse.server.api.v1.function.EntityRelationFunctions.toRestToEntityRelation
import static ish.oncourse.server.api.v1.function.ProductFunctions.updateCorporatePassesByIds
import ish.oncourse.server.api.v1.model.ArticleProductCorporatePassDTO
import ish.oncourse.server.api.v1.model.ArticleProductDTO
import static ish.oncourse.server.api.v1.model.ProductStatusDTO.CAN_BE_PURCHASED_IN_OFFICE
import static ish.oncourse.server.api.v1.model.ProductStatusDTO.CAN_BE_PURCHASED_IN_OFFICE_ONLINE
import static ish.oncourse.server.api.v1.model.ProductStatusDTO.DISABLED
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.ArticleProduct
import ish.oncourse.server.cayenne.Tax
import static ish.util.MoneyUtil.calculateTaxAdjustment
import static ish.util.MoneyUtil.getPriceIncTax
import org.apache.cayenne.ObjectContext
import static org.apache.commons.lang3.StringUtils.isBlank
import static org.apache.commons.lang3.StringUtils.isNotBlank
import static org.apache.commons.lang3.StringUtils.trimToNull

import java.time.ZoneId

class ArticleProductApiService extends EntityApiService<ArticleProductDTO, ArticleProduct, ArticleProductDao> {

    @Inject
    private AccountDao accountDao

    @Inject
    private CorporatePassDao corporatePassDao

    @Inject
    private CorporatePassProductDao corporatePassProductDao

    @Inject
    private TaxDao taxDao

    @Inject
    private ProductDao productDao

    @Override
    Class<ArticleProduct> getPersistentClass() {
        return ArticleProduct
    }

    @Override
    ArticleProductDTO toRestModel(ArticleProduct articleProduct) {
        new ArticleProductDTO().with { articleProductDTO ->
            articleProductDTO.id = articleProduct.id
            articleProductDTO.name = articleProduct.name
            articleProductDTO.code = articleProduct.sku
            articleProductDTO.description = articleProduct.description
            articleProductDTO.feeExTax = articleProduct.priceExTax?.toBigDecimal()
            articleProductDTO.totalFee = getPriceIncTax(articleProduct.priceExTax, articleProduct.tax?.rate, articleProduct.taxAdjustment)?.toBigDecimal()
            articleProductDTO.taxId = articleProduct.tax?.id
            articleProductDTO.incomeAccountId = articleProduct.incomeAccount?.id
            articleProductDTO.status = articleProduct.isOnSale ? articleProduct.isWebVisible ? CAN_BE_PURCHASED_IN_OFFICE_ONLINE : CAN_BE_PURCHASED_IN_OFFICE : DISABLED
            articleProductDTO.corporatePasses = articleProduct.corporatePassProducts.collect { cp ->
                new ArticleProductCorporatePassDTO().with { ap ->
                    ap.id = cp.corporatePass.id
                    ap.contactFullName = cp.corporatePass.contact.fullName
                    ap
                }
            } as List<ArticleProductCorporatePassDTO>
            articleProductDTO.relatedSellables = (EntityRelationDao.getRelatedFrom(articleProduct.context, Product.simpleName, articleProduct.id).collect { toRestFromEntityRelation(it) } +
                                        EntityRelationDao.getRelatedTo(articleProduct.context, Product.simpleName, articleProduct.id).collect { toRestToEntityRelation(it) })
            articleProductDTO.createdOn = articleProduct.createdOn?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
            articleProductDTO.modifiedOn = articleProduct.modifiedOn?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
            articleProductDTO.customFields = articleProduct.customFields.collectEntries {[(it.customFieldType.key) : it.value] }
            articleProductDTO
        }
    }

    @Override
    ArticleProduct toCayenneModel(ArticleProductDTO articleProductDTO, ArticleProduct articleProduct) {
        articleProduct.name = trimToNull(articleProductDTO.name)
        articleProduct.sku = trimToNull(articleProductDTO.code)
        articleProduct.description = trimToNull(articleProductDTO.description)
        articleProduct.priceExTax = toMoneyValue(articleProductDTO.feeExTax)
        articleProduct.tax = taxDao.getById(articleProduct.context, articleProductDTO.taxId.toLong())
        articleProduct.incomeAccount = accountDao.getById(articleProduct.context, articleProductDTO.incomeAccountId.toLong())
        articleProduct.taxAdjustment = calculateTaxAdjustment(toMoneyValue(articleProductDTO.totalFee), articleProduct.priceExTax, articleProduct.tax.rate)
        articleProduct.isOnSale = articleProductDTO.status == CAN_BE_PURCHASED_IN_OFFICE_ONLINE || articleProductDTO.status == CAN_BE_PURCHASED_IN_OFFICE
        articleProduct.isWebVisible = articleProductDTO.status == CAN_BE_PURCHASED_IN_OFFICE_ONLINE
        updateCorporatePassesByIds(articleProduct, articleProductDTO.corporatePasses*.id.findAll(), corporatePassProductDao, corporatePassDao)
        articleProduct
    }

    @Override
    void validateModelBeforeSave(ArticleProductDTO articleProductDTO, ObjectContext context, Long id) {
        if (isBlank(articleProductDTO.name)) {
            validator.throwClientErrorException(id, 'name', 'Name is required.')
        } else if (trimToNull(articleProductDTO.name).length() > 500) {
            validator.throwClientErrorException(id, 'name', 'Name cannot be more than 500 chars.')
        }

        if (isBlank(articleProductDTO.code)) {
            validator.throwClientErrorException(id, 'code', 'Code is required.')
        } else if (trimToNull(articleProductDTO.code).length() > 10) {
            validator.throwClientErrorException(id, 'code', 'Code cannot be more than 10 chars.')
        } else if (!articleProductDTO.code.matches('^\\w+(\\.\\w+)*$')) {
            validator.throwClientErrorException(id, 'code', 'Code must start and end with letters or numbers and can contain full stops.')
        } else {
            Long articleId = productDao.getByCode(context, trimToNull(articleProductDTO.code))?.id
            if (articleId && articleId != id) {
                validator.throwClientErrorException(id, 'code', 'Code must be unique.')
            }
        }

        if (articleProductDTO.feeExTax == null) {
            validator.throwClientErrorException(id, 'feeExTax', 'Fee ex tax is required.')
        }
        if (articleProductDTO.totalFee == null) {
            validator.throwClientErrorException(id, 'totalFee', 'Total fee is required.')
        }

        if (articleProductDTO.taxId == null) {
            validator.throwClientErrorException(id, 'taxId', 'Tax is required.')
        } else {
            Tax tax = taxDao.getById(context, articleProductDTO.taxId)
            if (!tax) {
                validator.throwClientErrorException(id, 'tax', "Tax with id=${articleProductDTO.taxId} doesn't exist.")
            }
            Money adjustment = calculateTaxAdjustment(toMoneyValue(articleProductDTO.totalFee), toMoneyValue(articleProductDTO.feeExTax), tax.rate)
            if (adjustment.toBigDecimal().abs() > 0.01) {
                validator.throwClientErrorException(id, 'tax', "Incorrect money values for product price.")
            }
        }

        if (articleProductDTO.status == null) {
            validator.throwClientErrorException(id, 'status', 'Status is required.')
        }

        if (isNotBlank(articleProductDTO.description) && trimToNull(articleProductDTO.description).length() > 8000) {
            validator.throwClientErrorException(id, 'description', 'Description cannot be more than 8000 chars.')
        }

        if (articleProductDTO.incomeAccountId == null) {
            validator.throwClientErrorException(id, 'incomeAccountId', 'IncomeAccount is required for account entity.')
        } else {
            Account account = accountDao.getById(context, articleProductDTO.incomeAccountId.toLong())
            if (!account) {
                validator.throwClientErrorException(id, 'incomeAccount', "Account with id=${articleProductDTO.incomeAccountId} doesn't exist.")
            } else if (account.type != AccountType.INCOME) {
                validator.throwClientErrorException(id, 'incomeAccount', "Only accounts of income type can be assigned to voucher.")
            }
        }

        articleProductDTO.corporatePasses.each {
            if (!it.id) {
                validator.throwClientErrorException(id, 'corporatePasses', 'Id is required for corporate pass entity.')
            } else if (!corporatePassDao.getById(context, it.id)) {
                validator.throwClientErrorException(id, 'corporatePasses', "CorporatePass with id=$it.id doesn't exist.")
            }
        }
    }

    @Override
    void validateModelBeforeRemove(ArticleProduct articleProduct) {
        validator.throwClientErrorException(articleProduct.id, 'id', 'Product cannot be deleted. Instead of it you can disable it.')
    }
}
