import {FieldHeading} from "./../field/FieldHeading";

export class WaitingList {
  contactId?: string;
  courseId?: string;
  studentsCount?: number;
  detail?: string;
  warnings?: string[];
  errors?: string[];
  selected?: boolean;
  fieldHeadings?: FieldHeading[];
}

