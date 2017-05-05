import { ContactParams } from "./ContactParams";
import { PromotionParams } from "./PromotionParams";

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

