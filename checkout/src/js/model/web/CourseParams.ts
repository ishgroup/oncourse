import {ContactParams} from "./../web/ContactParams";
import {PromotionParams} from "./../web/PromotionParams";

export class CourseParams {

  /**
   * List of requested course classes
   */
  coursesIds?: string[];

  /**
   * List of applied promotions
   */
  promotions?: PromotionParams[];
  contact?: ContactParams;
}

