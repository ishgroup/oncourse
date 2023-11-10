import { CustomFieldType } from "@api/model";

export interface CustomFieldTypesState {
  /**
   * key - entity name
   * value - retrieved items
   */
  types: Record<string, CustomFieldType[]>;
  updating: boolean;
}
