import {createStringEnum} from "common/utils/EnumUtils";

const Enum = createStringEnum([
      'enrolment',
      'waitinglist',
      'application',
      'mailinglist'
]);

export type FieldSet = keyof typeof Enum;
