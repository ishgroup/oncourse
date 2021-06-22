import { createStringEnum } from '../../common/utils/EnumUtils';


export const FieldSet = createStringEnum([
  'ENROLMENT',
  'WAITINGLIST',
  'MAILINGLIST',
]);

export type FieldSet = keyof typeof FieldSet;
