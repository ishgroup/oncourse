import { FieldHeading } from '../field/FieldHeading';

export class WaitingList {
  studentsCount: number;

  detail: string;

  contactId: string;

  courseId: string;

  warnings: string[];

  errors: string[];

  selected: boolean;

  fieldHeadings: FieldHeading[];
}
