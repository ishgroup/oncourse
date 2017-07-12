import {CourseClassPrice} from "../web/CourseClassPrice";

export class Enrolment {
  contactId?: string;
  classId?: string;
  price?: CourseClassPrice;
  warnings?: string[];
  errors?: string[];
  selected?: boolean;
}

