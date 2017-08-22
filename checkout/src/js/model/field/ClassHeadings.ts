import {FieldHeading} from "./../field/FieldHeading";

export class ClassHeadings {

  /**
   * Reffered class id
   */
  classId?: string;

  /**
   * Related field headings
   */
  headings?: FieldHeading[];

  /**
   * Dummy headin filled by fields that not linked to any real Field Heading
   */
  dummyHeading?: FieldHeading;
}

