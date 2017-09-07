import {Field} from "./../field/Field";
import {CourseClassPrice} from "./../web/CourseClassPrice";
import {Field} from "../field/Field";

export class Enrolment {
  contactId?: string;
  classId?: string;
  price?: CourseClassPrice;
  warnings?: string[];
  errors?: string[];
  selected?: boolean;
  fields?: Field[];
}

