import {Item} from "../model/../common/Item";
import {DataType} from "../model/../field/DataType";

export class Field {

  /**
   * Field id
   */
  id?: string;

  /**
   * Property key
   */
  key?: string;

  /**
   * Field name
   */
  name?: string;

  /**
   * Field description
   */
  description?: string;

  /**
   * Mandatory flag
   */
  mandatory?: boolean;

  /**
   * Data type of provaded value
   */
  dataType?: DataType;

  /**
   * Enumeration name which is type for field value
   */
  enumType?: string;

  /**
   * Value for corresponded property
   */
  value?: string;

  /**
   * Default value for corresponded property
   */
  defaultValue?: string;

  /**
   * Combobox choices for enumeration types
   */
  enumItems?: Item[];

  /**
   * order position
   */
  ordering?: number;
}

