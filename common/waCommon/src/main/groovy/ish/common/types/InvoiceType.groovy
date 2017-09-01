package ish.common.types

import ish.common.util.DisplayableExtendedEnumeration
import ish.oncourse.API

@API
enum InvoiceType implements DisplayableExtendedEnumeration<Integer> {

    
    /**
     * Database value: 1
     */
    @API
    INVOICE(1, "Invoice"),


    /**
     * Database value: 2
     */
    @API
    SALE_ORDER(2, "Sale order")
    
    
    private String displayName
    private int value

    private InvoiceType(int value, String displayName) {
        this.value = value
        this.displayName = displayName
    }
    
    @Override
    Integer getDatabaseValue() {
        return this.value
    }

    @Override
    String getDisplayName() {
        return this.displayName
    }

    @Override
    String toString() {
        return getDisplayName()
    } 
}