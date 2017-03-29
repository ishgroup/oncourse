import { ContactParams } from "./ContactParams";
import { PromotionParams } from "./PromotionParams";

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

