import { Amount } from "./../checkout/Amount";
import { ContactNode } from "./../checkout/ContactNode";
import { CommonError } from "./../common/CommonError";

export class CheckoutModel {
  error?: CommonError;
  contactNodes?: ContactNode[];
  amount?: Amount;
  payerId?: string;
}

