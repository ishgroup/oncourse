
import {createStringEnum} from "common/utils/EnumUtils";

export const ClassEnrolmentCondition = createStringEnum([
  'afterClassStarts',
  'beforeClassStarts',
  'beforeClassEnds',
]);

export type ClassEnrolmentCondition = keyof typeof ClassEnrolmentCondition;
