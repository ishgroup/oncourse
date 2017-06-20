import {ContactParams} from "../model/../web/ContactParams";
import {PromotionParams} from "../model/../web/PromotionParams";

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

