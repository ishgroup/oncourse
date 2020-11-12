import { Assessment } from "@api/model";

export interface AssessmentsState {
  items?: Assessment[];
  search?: string;
  loading?: boolean;
  rowsCount?: number;
}
