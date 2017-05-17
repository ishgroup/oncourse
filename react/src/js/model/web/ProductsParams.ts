import { ContactParams } from "./../web/ContactParams";
import { PromotionParams } from "./../web/PromotionParams";

export class ProductsParams {

  /**
   * List of requested products
   */
  productsIds?: string[];

  /**
   * List of applied promotions
   */
  promotions?: PromotionParams[];
  contact?: ContactParams;
}

