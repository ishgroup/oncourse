import {ContactNode} from "../model/../checkout/ContactNode";

export class CheckoutModelRequest {
  contactNodes?: ContactNode[];
  promotionIds?: string[];
  payerId?: string;
}

