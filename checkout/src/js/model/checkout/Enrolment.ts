import {FieldHeading} from "./../field/FieldHeading";
import {CourseClassPrice} from "./../web/CourseClassPrice";

export class Enrolment {
  contactId?: string;
  classId?: string;
  courseId?: string;
  price?: CourseClassPrice;
  warnings?: string[];
  errors?: string[];
  selected?: boolean;
  fieldHeadings?: FieldHeading[];
  allowRemove?: boolean;
  relatedClassId?: string;
  relatedProductId?: string;
}

