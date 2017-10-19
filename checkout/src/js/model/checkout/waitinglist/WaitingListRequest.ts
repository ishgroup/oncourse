import {FieldHeading} from "./../../field/FieldHeading";

export class WaitingListRequest {
  contactId?: string;
  classId?: string;
  comments?: string;
  numberOfStudents?: number;
  fieldHeadings?: FieldHeading[];
}

