import { CourseClass } from "@api/model";

export interface CoruseClassesSate {
  items?: CourseClass[];
  search?: string;
  loading?: boolean;
  rowsCount?: number;
  checkEmpty?: boolean;
}

export interface CourseClassBulkSession {
  selection?: any[];
  modalOpened?: boolean;
  onUpdate?: () => void;
  tutors?: any[];
}
