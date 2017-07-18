import {ContactNode} from "./../checkout/ContactNode";

export class CheckoutModelRequest {
  contactNodes?: ContactNode[];
  promotionIds?: string[];
  redeemedVoucherIds?: string[];
  payerId?: string;
  corporatePassId?: string;
}

