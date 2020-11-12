import { Course, CourseEnrolmentType } from "@api/model";

export interface CoursesState {
  items?: Course[];
  search?: string;
  enrolmentTypeSearch?: CourseEnrolmentType;
  loading?: boolean;
  rowsCount?: number;
}
