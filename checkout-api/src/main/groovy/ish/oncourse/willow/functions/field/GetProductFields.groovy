package ish.oncourse.willow.functions.field

import ish.common.types.ProductType
import ish.common.types.TypesUtil
import ish.oncourse.common.field.FieldProperty
import ish.oncourse.model.FieldConfiguration
import ish.oncourse.model.Product
import ish.oncourse.willow.model.field.Field
import ish.oncourse.willow.model.field.FieldHeading

import static ish.common.types.ProductType.ARTICLE
import static ish.common.types.ProductType.MEMBERSHIP
import static ish.common.types.ProductType.VOUCHER

class GetProductFields {

    private Product product
    private ProductType productType
    private int quantity

    GetProductFields(Product product) {
        this.product = product
        this.productType = TypesUtil.getEnumForDatabaseValue(product.type, ProductType.class)
        this.quantity = 1
    }

    GetProductFields(Product product, ProductType productType) {
        this.product = product
        this.productType = productType
        this.quantity = 1
    }

    GetProductFields(Product product, ProductType productType, int quantity) {
        this.product = product
        this.productType = productType
        this.quantity = quantity
    }

    FieldConfiguration getConfiguration() {
        switch (productType) {
            case ARTICLE:
                return product.fieldConfigurationScheme?.articleFieldConfiguration
            case MEMBERSHIP:
                return product.fieldConfigurationScheme?.membershipFieldConfiguration
            case VOUCHER:
                return product.fieldConfigurationScheme?.voucherFieldConfiguration
        }
    }

    List<FieldHeading> get() {
        if (configuration) {
            Set<ish.oncourse.model.Field> customFields
            switch (productType) {
                case ARTICLE:
                    customFields = configuration.fields
                            .findAll { f -> FieldProperty.getByKey(f.property) == FieldProperty.CUSTOM_FIELD_ARTICLE }
                            .toSet()
                    break
                case MEMBERSHIP:
                    customFields = configuration.fields
                            .findAll { f -> FieldProperty.getByKey(f.property) == FieldProperty.CUSTOM_FIELD_MEMBERSHIP }
                            .toSet()
                    break
                case VOUCHER:
                    customFields = configuration.fields
                            .findAll { f -> FieldProperty.getByKey(f.property) == FieldProperty.CUSTOM_FIELD_VOUCHER }
                            .toSet()
                    break
            }
            List<FieldHeading> headingList = FieldHelper.valueOf(customFields).buildFieldHeadings()
            headingList.each {heading ->
                List<Field> fields = heading.fields.clone() as List<Field>
                for (int i = 1; i < quantity; i++) {
                    heading.fields.addAll(fields)
                }
            }
            return headingList
        }
        return null
    }
}
