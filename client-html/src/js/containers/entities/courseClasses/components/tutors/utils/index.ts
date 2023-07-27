/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { CourseClassTutor } from "@api/model";
import { format } from "date-fns";
import CourseClassTutorService from "../services/CourseClassTutorService";
import { D_MMM_YYYY } from  "ish-ui";
import { classCostInitial } from "../../../CourseClasses";
import { ClassCostExtended, CourseClassTutorExtended } from "../../../../../../model/entities/CourseClass";

export const validateTutorCreate = (tutor: CourseClassTutor) => CourseClassTutorService.validatePost(tutor);
export const validateTutorUpdate = (tutor: CourseClassTutor) => CourseClassTutorService.validatePut(tutor);

export const getTutorNameWarning = (value: any, latestSession: Date) => {
  let warning = null;

  const isBarred = ["BARRED", "INTERIM_BARRED"].includes(value["tutor.wwChildrenStatus"]);
  const expiredDate = value["tutor.dateFinished"] && new Date(value["tutor.dateFinished"]);
  const isExpired = expiredDate && latestSession ? expiredDate < latestSession : false;

  if (isBarred) {
    warning = "This tutor is barred from working with children";
  } else if (isExpired) {
    warning = `This tutor has a contract end date of ${format(
      expiredDate,
      D_MMM_YYYY
    )}. Please ensure you are correctly adding this tutor to this class`;
  }

  return warning;
};

export const getTutorPayInitial = (
  tutor: CourseClassTutorExtended,
  courseClassId: number,
  taxId: number,
  role: any,
  perUnitAmountExTax: number
): ClassCostExtended => {
  const perUnitAmount = isNaN(perUnitAmountExTax) ? 0 : perUnitAmountExTax;

  return {
    ...classCostInitial,
    courseClassid: courseClassId,
    taxId,
    isOverriden: perUnitAmount === 0,
    courseClassTutorId: tutor.id,
    temporaryTutorId: tutor.temporaryId,
    contactId: tutor.contactId,
    contactName: tutor.tutorName,
    repetitionType: (role && role["currentPayrate.type"]) || "Fixed",
    perUnitAmountExTax: perUnitAmount,
    flowType: "Wages"
  };
};

export const isTutorWageExist = (budget: ClassCostExtended[], tutor: CourseClassTutorExtended) =>
  budget.some(b => b.flowType === "Wages" && b.contactId === tutor.contactId);

