import { ContactParams } from "./../web/ContactParams";
import { PromotionParams } from "./../web/PromotionParams";

export class CourseClassesParams {

  /**
   * List of requested course classes
   */
  courseClassesIds?: string[];

  /**
   * List of applied promotions
   */
  promotions?: PromotionParams[];
  contact?: ContactParams;
}

