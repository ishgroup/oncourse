
import {createStringEnum} from "common/utils/EnumUtils";

export const DataType = createStringEnum([
  'STRING',
  'BOOLEAN',
  'DATE',
  'DATETIME',
  'INTEGER',
  'COUNTRY',
  'LANGUAGE',
  'ENUM',
  'EMAIL',
  'SUBURB',
  'POSTCODE',
  'PHONE',
  'CHOICE',
]);

export type DataType = keyof typeof DataType;
