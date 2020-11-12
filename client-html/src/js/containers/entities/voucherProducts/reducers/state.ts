import { Course, VoucherProduct } from "@api/model";

export interface VoucherProductState {
  foundCourses?: Course[];
  pendingCourses?: boolean;
  minFee?: number;
  maxFee?: number;
  items?: VoucherProduct[];
  search?: string;
  loading?: boolean;
  rowsCount?: number;
}
