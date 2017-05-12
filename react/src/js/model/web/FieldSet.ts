import {createStringEnum} from "../../common/utils/EnumUtils";

export const FieldSet = createStringEnum([
      'enrolment',
      'waitinglist',
      'application',
      'mailinglist'
]);

export type FieldSet = keyof typeof FieldSet;


