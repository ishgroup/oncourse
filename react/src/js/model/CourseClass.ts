import { Course } from "./Course";
import { CourseClassPrice } from "./CourseClassPrice";

export class CourseClass {

  /**
   * Internal Unique identifier of class
   */
  id?: string;
  course?: Course;

  /**
   * Code of class
   */
  code?: string;

  /**
   * Start date of class
   */
  start?: string;

  /**
   * End date of class
   */
  end?: string;

  /**
   * Available places
   */
  hasAvailablePlaces?: boolean;

  /**
   * Number of free places
   */
  availableEnrolmentPlaces?: number;

  /**
   * Is class finished
   */
  isFinished?: boolean;

  /**
   * Is class cancelled
   */
  isCancelled?: boolean;

  /**
   * Allow by application
   */
  isAllowByApplication?: boolean;

  /**
   * Is payment gateway enabled
   */
  isPaymentGatewayEnabled?: boolean;

  /**
   * Prices attached to current course class
   */
  price?: CourseClassPrice;
}

