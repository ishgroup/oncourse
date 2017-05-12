import {createStringEnum} from "common/utils/EnumUtils";

const Enum = createStringEnum([
      'string',
      'boolean',
      'date',
      'dateTime',
      'integer',
      'country',
      'language',
      'enum',
      'email'
]);

export type DataType = keyof typeof Enum;
