import { Amount } from "./../checkout/Amount";
import { PurchaseItems } from "./../checkout/PurchaseItems";
import { CommonError } from "./../common/CommonError";

export class CheckoutModel {
  error?: CommonError;
  purchaseItemsList?: PurchaseItems[];
  amount?: Amount;
  promotionIds?: string[];
  payerId?: string;
}

