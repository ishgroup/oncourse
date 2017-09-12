import {Amount} from "./Amount";
import {ContactNode} from "./ContactNode";
import {CommonError} from "../common/CommonError";
import {ValidationError} from "../common/ValidationError";

export class CheckoutModel {
  error?: CommonError;
  validationErrors?: ValidationError;
  contactNodes?: ContactNode[];
  amount?: Amount;
  payerId?: string;
}

