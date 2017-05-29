import { ContactNode } from "./../checkout/ContactNode";

export class CheckoutModelRequest {
  contactNodes?: ContactNode[];
  promotionIds?: string[];
  payerId?: string;
}

