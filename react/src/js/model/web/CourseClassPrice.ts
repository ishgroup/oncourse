import { Discount } from "./../web/Discount";

export class CourseClassPrice {

  /**
   * TODO:  fee
   */
  fee?: string;

  /**
   * TODO:  feeOverriden
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

