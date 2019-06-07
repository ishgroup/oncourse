import {Amount} from "./../checkout/Amount";
import {ContactNode} from "./../checkout/ContactNode";
import {CommonError} from "./../common/CommonError";
import {ValidationError} from "./../common/ValidationError";

export class CheckoutModel {
  error?: CommonError;
  validationErrors?: ValidationError;
  contactNodes?: ContactNode[];
  amount?: Amount;
  payerId?: string;
}

