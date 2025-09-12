export const INVOICE_LINE_DISCOUNT_COLUMNS = 'name,discountType,discountDollar,discountPercent,rounding';

export const INVOICE_LINE_DISCOUNT_AQL = '((validTo >= today) or (validTo == null)) and ((validFrom <= today) or (validFrom == null)) ';