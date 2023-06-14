import { AnyArgFunction } from "./CommonFunctions";

export type NestedTableColumnsTypes = "checkbox" | "link" | "currency" | "date" | "date-time" | "delete";

export interface NestedTableColumn {
  name: string;
  title?: string;
  type?: NestedTableColumnsTypes;
  linkPath?: string;
  linkEntity?: string;
  onChangeHandler?: any;
  disabledHandler?: any;
  width?: number;
  disableSort?: boolean;
  defaultSort?: boolean;
  cellClass?: string;
  customSort?: AnyArgFunction;
}
