import { createStringEnum } from '@EnumUtils';

export const FieldSet = createStringEnum([
  'ENROLMENT',
  'WAITINGLIST',
]);

export type FieldSet = keyof typeof FieldSet;
