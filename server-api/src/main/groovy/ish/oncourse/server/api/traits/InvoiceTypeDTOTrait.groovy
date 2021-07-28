package ish.oncourse.server.api.traits

import ish.common.types.InvoiceType
import ish.oncourse.server.api.v1.model.InvoiceTypeDTO

trait InvoiceTypeDTOTrait {

    InvoiceType getDbType() {
        switch (this as InvoiceTypeDTO) {
            case InvoiceTypeDTO.INVOICE:
                return InvoiceType.INVOICE
            case InvoiceTypeDTO.QUOTE:
                return InvoiceType.QUOTE
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    InvoiceTypeDTO fromDbType(InvoiceType invoiceType) {
        if(!invoiceType) {
            return null
        }
        switch (invoiceType) {
            case InvoiceType.INVOICE:
                return InvoiceTypeDTO.INVOICE
            case InvoiceType.QUOTE:
                return InvoiceTypeDTO.QUOTE
            default:
                throw new IllegalArgumentException("Illegal invoice type is $invoiceType.displayName")
        }
    }
}