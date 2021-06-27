export interface PlainEntityState<T> {
  items?: T[];
  search?: string;
  loading?: boolean;
  rowsCount?: number;
}

export interface CommonPlainSearchEntity {
  items?: any[];
  search?: string;
  loading?: boolean;
  rowsCount?: number;
}

export const availableEntities = [
  "Assessment",
  "Account",
  "ArticleProduct",
  "Discount",
  "Qualification",
  "Module",
  "MembershipProduct",
  "Contact",
  "Site",
  "Room",
  "Course",
  "CourseClass",
  "Enrolment",
  "ConcessionType",
  "CorporatePass",
  "ContactRelationType",
  "VoucherProduct"
] as const;

export type PlainSearchEntity = typeof availableEntities[number];

export type CommonPlainRecordSearchState = {
  [key in PlainSearchEntity]?: CommonPlainSearchEntity;
};
