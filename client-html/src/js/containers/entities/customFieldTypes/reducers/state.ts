import { CustomFieldType } from "@api/model";

export interface CustomFieldTypesState {
  /**
   * key - entity name
   * value - retrieved items
   */
  types: { key?: string; value?: CustomFieldType[] };
  updating: boolean;
}
