import {FieldHeading} from "../field/FieldHeading";

export class Application {
  contactId?: string;
  classId?: string;
  warnings?: string[];
  errors?: string[];
  selected?: boolean;
  fieldHeadings?: FieldHeading[];
}

