import {Discount} from "./../web/Discount";

export class CourseClassPrice {

  /**
   * Full class price
   */
  fee?: string;

  /**
   * Class price overriden by application
   */
  feeOverriden?: string;
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

