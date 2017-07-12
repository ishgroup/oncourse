import {Amount} from "./Amount";
import {ContactNode} from "./ContactNode";
import {CommonError} from "../common/CommonError";

export class CheckoutModel {
  error?: CommonError;
  contactNodes?: ContactNode[];
  amount?: Amount;
  payerId?: string;
}

