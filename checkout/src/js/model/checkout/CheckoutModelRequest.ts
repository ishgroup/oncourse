import {ContactNode} from "./../checkout/ContactNode";

export class CheckoutModelRequest {
  contactNodes?: ContactNode[];
  promotionIds?: string[];
  redeemedVoucherIds?: string[];
  redeemedVoucherProductIds?: string[];
  payerId?: string;
  applyCredit?: boolean;

  /**
   * aclyal amount allocated for invoice/invoices (equalse subTotal if payment plan invoices not presented)
   */
  payNow?: number;
  corporatePassId?: string;
}

