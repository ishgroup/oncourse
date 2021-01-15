import {FieldHeading} from "./../field/FieldHeading";

export class WaitingList {
  contactId?: string;
  courseId?: string;
  warnings?: string[];
  errors?: string[];
  selected?: boolean;
  fieldHeadings?: FieldHeading[];
}

