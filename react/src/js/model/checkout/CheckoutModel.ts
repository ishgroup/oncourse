import {Amount} from "../model/../checkout/Amount";
import {ContactNode} from "../model/../checkout/ContactNode";
import {CommonError} from "../model/../common/CommonError";

export class CheckoutModel {
  error?: CommonError;
  contactNodes?: ContactNode[];
  amount?: Amount;
  payerId?: string;
}

