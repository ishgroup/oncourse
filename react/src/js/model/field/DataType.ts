
import {createStringEnum} from "common/utils/EnumUtils";

export const DataType = createStringEnum([
      'string',
      'boolean',
      'date',
      'dateTime',
      'integer',
      'country',
      'language',
      'enum',
      'email',
      'suburb',
      'postcode',
      'phone'
]);

export type DataType = keyof typeof DataType;


