import {ContactParams} from "./ContactParams";
import {PromotionParams} from "./PromotionParams";

export class Suggestion {

  /**
   * Internal Unique identifier of suggestion
   */
  id?: string;

  /**
   * Code of suggestion
   */
  code?: string;

  /**
   * Name of suggestion
   */
  name?: string;

  /**
   * Description of suggestion
   */
  description?: string;

  /**
   * Is payment gateway enabled
   */
  isPaymentGatewayEnabled?: boolean;

  /**
   * Is on sale and is Web visible
   */
  canBuy?: boolean;
  allowRemove?: boolean;
  relatedClassId?: string;
  price?: number;
}

export class SuggestionsParams {
  /**
   * List of requested suggestions
   */
  productsIds?: string[];

  /**
   * List of applied promotions
   */
  promotions?: PromotionParams[];
  contact?: ContactParams;
}
