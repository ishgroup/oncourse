import {Discount} from "./../web/Discount";

export class CourseClassPrice {

  /**
   * Full class price
   */
  fee?: number;

  /**
   * Class price overriden by application
   */
  feeOverriden?: number;
  appliedDiscount?: Discount;

  /**
   * List discounts sorted by discounted fee
   */
  possibleDiscounts?: Discount[];

  /**
   * Has tax
   */
  hasTax?: boolean;
}

