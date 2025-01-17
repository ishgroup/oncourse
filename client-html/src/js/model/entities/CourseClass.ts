/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

import {
  AssessmentClass,
  ClassCost,
  CourseClass,
  CourseClassTutor,
  createStringEnum,
  Note,
  StudentAttendance,
  Tax,
  TrainingPlan,
  TutorAttendanceType
} from "@api/model";
import { TimetableSession } from "../timetable";
import { AccountExtended } from "./Account";

export interface CourseClassRoom {
  id: number;
  name: string;
  seatedCapacity: string;
}

export interface ClassCostExtended extends ClassCost {
  index?: number;
  temporaryId?: string;
  temporaryTutorId?: string;
}

export interface StudentAttendanceExtended extends StudentAttendance {
  index: number;
}

export interface TrainingPlanExtended extends TrainingPlan {
  index: number;
}

export interface AssessmentClassExtended extends AssessmentClass {
  index?: number;
  temporaryId?: string;
}

export interface CourseClassTutorExtended extends CourseClassTutor {
  temporaryId?: string;
}

export interface CourseClassExtended extends CourseClass {
  sessions?: TimetableSession[];
  tutors?: CourseClassTutorExtended[];
  budget?: ClassCostExtended[];
  studentAttendance?: StudentAttendanceExtended[];
  trainingPlan?: TrainingPlanExtended[];
  notes?: Note[];
  assessments?: AssessmentClassExtended[];
  openedSession?: TimetableSession;
}

export interface BudgetCostModalContentProps {
  values?: ClassCostExtended;
  costLabel?: string;
  hasMinMaxFields?: boolean;
  hasCountField?: boolean;
  currentTax?: Tax;
  classValues?: CourseClassExtended;
  taxes?: Tax[];
  accounts?: AccountExtended[];
  dispatch?: any;
  classes?: any;
  currencySymbol?: string;
}

export type CourseClassStatus = "Current" | "Future" | "Self Paced" | "Unscheduled" | "Finished" | "Cancelled";

export interface ClassCostItem {
  value: ClassCostExtended;
  max: number;
  projected: number;
  actual: number;
}

export interface ClassCostType {
  max: number;
  projected: number;
  actual: number;
  items: ClassCostItem[];
  percentage?: number;
}

export interface ClassCostTypes {
  income: ClassCostType;
  customInvoices: ClassCostType;
  discount: ClassCostType;
  cost: ClassCostType;
}

export const SessionRepeatTypes = createStringEnum([
  "hour",
  "day (excluding weekends)",
  "day (including weekends)",
  "week",
  "month",
  "year"
]);

export type SessionRepeatTypes = keyof typeof SessionRepeatTypes;

export interface ContactAttendanceItem {
  name: string;
  title?: string;
  contactId: number;
  attendances: (StudentAttendanceExtended & TrainingPlanExtended)[];
}

export type AttandanceStepItem = Partial<TimetableSession> & Partial<AssessmentClassExtended>;

export interface AttandanceDay {
  day: Date;
  items: AttandanceStepItem[];
}

export interface AttandanceMonth {
  month: Date;
  days: AttandanceDay[];
}

export type AttandanceChangeType =
  | "singleStudent"
  | "singleTutor"
  | "byStudent"
  | "byTutor"
  | "allStudents"
  | "allTutors"
  | "allTrainingPlans"
  | "bySession";

export type AttendanceGridType = "Student" | "Training plan";

export const tutorStatusRoles: TutorAttendanceType[] = [
  "Confirmed for payroll",
  "Rejected for payroll",
  "Not confirmed for payroll"
];
