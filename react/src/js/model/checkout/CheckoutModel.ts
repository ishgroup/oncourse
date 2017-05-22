import { Amount } from "./../checkout/Amount";
import { PurchaseItems } from "./../checkout/PurchaseItems";

export class CheckoutModel {
  purchaseItems?: PurchaseItems[];
  amount?: Amount;
  promotionIds?: string[];
}

