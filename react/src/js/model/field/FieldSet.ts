
import {createStringEnum} from "common/utils/EnumUtils";

export const FieldSet = createStringEnum([
      'enrolment',
      'waitinglist',
      'mailinglist'
]);

export type FieldSet = keyof typeof FieldSet;


