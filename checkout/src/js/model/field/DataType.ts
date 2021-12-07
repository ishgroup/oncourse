
import {createStringEnum} from "@EnumUtils";

export const DataType = createStringEnum([
  'STRING',
  'LONG_STRING',
  'PATTERN_TEXT',
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
  'TAGGROUP_S',
  'TAGGROUP_M',
  'MAILINGLIST',
  'MONEY',
  'URL',
]);

export type DataType = keyof typeof DataType;
