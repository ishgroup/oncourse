
import {createStringEnum} from "common/utils/EnumUtils";

export const ClassCondition = createStringEnum([
  'afterClassStarts',
  'beforeClassStarts',
  'afterClassEnds',
  'beforeClassEnds',
]);

export type ClassCondition = keyof typeof ClassCondition;
