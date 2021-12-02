
import {createStringEnum} from "@EnumUtils";

export const Type = createStringEnum([
  'ARTICLE',
  'MEMBERSHIP',
  'VOUCHER',
]);

export type Type = keyof typeof Type;
