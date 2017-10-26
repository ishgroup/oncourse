
export class Course {

  /**
   * Internal Unique identifier of course
   */
  id?: string;

  /**
   * Code of course
   */
  code?: string;

  /**
   * Name of course
   */
  name?: string;

  /**
   * Description of course
   */
  description?: string;

  /**
   * Has enrollable classes
   */
  hasCurrentClasses?: boolean;

  /**
   * Has more available places
   */
  hasMoreAvailablePlaces?: boolean;
}

