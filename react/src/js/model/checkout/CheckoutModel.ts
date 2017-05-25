import { Amount } from "./../checkout/Amount";
import { PurchaseItems } from "./../checkout/PurchaseItems";

export class CheckoutModel {
  purchaseItemsList?: PurchaseItems[];
  amount?: Amount;
  promotionIds?: string[];
}

