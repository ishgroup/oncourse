
export class Version {

  /**
   * unique id of version
   */
  id?: number;

  /**
   * has version published
   */
  published?: boolean;

  /**
   * First and last name of the user who published version
   */
  author?: string;

  /**
   * count of changes from previous version
   */
  changes?: number;

  /**
   * date and time when version was published
   */
  datetime?: string;
}

