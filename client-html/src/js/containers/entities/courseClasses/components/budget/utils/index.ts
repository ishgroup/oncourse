/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ClassCost, CourseClassPaymentPlan, Tax } from "@api/model";
import { differenceInMinutes, format, subMinutes } from "date-fns";
import { decimalDivide, decimalMul, decimalPlus } from "../../../../../../common/utils/numbers/decimalCalculation";
import { TimetableSession } from "../../../../../../model/timetable";
import { ClassCostExtended, TutorAttendanceExtended } from "../../../../../../model/entities/CourseClass";
import { getCurrentTax } from "../../../../taxes/utils";

export const discountsSort = (a, b) => (a.description > b.description ? 1 : -1);

export const getFeeWithTaxAmount = (exTaxFee: number, currentTax: Tax) => decimalMul(exTaxFee, decimalPlus(currentTax.rate, 1));

export const getPaymentPlansTotal = (paymentPlans: CourseClassPaymentPlan[]) =>
  paymentPlans.reduce((p: number, c) => (c.dayOffset === null ? p : decimalPlus(p, c.amount)), 0);

const getSessionPayable = (
  item: ClassCostExtended,
  s: TimetableSession,
  p: number,
  tutotAttendance: TutorAttendanceExtended[]
) => {
  if (item.flowType === "Wages") {
    let isApplied = true;

    if (tutotAttendance) {
      isApplied = false;

      const attendance = tutotAttendance.find(a => a.attendanceType === "Confirmed for payroll"
        && a.sessionId === s.id
        && a.courseClassTutorId === item.courseClassTutorId
      );

      if (attendance) {
        isApplied = true;
        if (typeof attendance.durationMinutes === "number") {
          return decimalPlus(decimalDivide(attendance.durationMinutes, 60), p);
        }
      }
    }

    if (item.courseClassTutorId && !s.courseClassTutorIds.includes(item.courseClassTutorId)) {
      isApplied = false;
    }

    if (item.temporaryTutorId && !s.temporaryTutorIds.includes(item.temporaryTutorId)) {
      isApplied = false;
    }

    if (!isApplied) {
      return decimalPlus(0, p);
    }
  }

  switch (item.repetitionType) {
    case "Per session":
      return decimalPlus(1, p);
    default: {
      const start = new Date(s.start);
      const end = subMinutes(new Date(s.end), s.payAdjustment || 0);

      start.setSeconds(0, 0);
      end.setSeconds(0, 0);

      return decimalPlus(decimalDivide(differenceInMinutes(end, start), 60), p);
    }
  }

}

export const getClassCostFee = (
  item: ClassCostExtended,
  maximumPlaces: number,
  projectedPlaces: number,
  actualPlaces: number,
  sessions: TimetableSession[],
  tutotAttendance: TutorAttendanceExtended[]
) => {
  let feeAmount = item.perUnitAmountExTax;
  let actualFeeAmount = item.perUnitAmountExTax;
  let isMultiplyable = true;

  switch (item.repetitionType) {
    case "Fixed": {
      isMultiplyable = false;
      break;
    }
    case "Per session": {
      isMultiplyable = false;

      const sessionsLength = item.flowType === "Wages"
        ? sessions.reduce((p, s) => getSessionPayable(item, s, p, null), 0)
        : sessions.length;

      feeAmount = decimalMul(feeAmount, sessionsLength);
      actualFeeAmount = feeAmount;
      break;
    }
    case "Per enrolment": {
      isMultiplyable = true;
      break;
    }
    case "Per unit": {
      isMultiplyable = false;
      feeAmount = decimalMul(feeAmount, item.unitCount);
      actualFeeAmount = feeAmount;
      break;
    }
    case "Discount": {
      return {
        max: decimalMul(
          item.perUnitAmountExTax,
          maximumPlaces,
          typeof item.courseClassDiscount.forecast === "number"
            ? item.courseClassDiscount.forecast
            : item.courseClassDiscount.discount.predictedStudentsPercentage
        ),
        projected: decimalMul(
          item.perUnitAmountExTax,
          projectedPlaces,
          typeof item.courseClassDiscount.forecast === "number"
            ? item.courseClassDiscount.forecast
            : item.courseClassDiscount.discount.predictedStudentsPercentage
        ),
        actual: item.actualAmount
      };
    }
    case "Per timetabled hour":
    case "Per student contact hour": {
      const duration = sessions.reduce((p, s) => getSessionPayable(item, s, p, null), 0);
      const actualDuration = sessions.reduce((p, s) => getSessionPayable(item, s, p, tutotAttendance), 0);

      isMultiplyable = item.repetitionType === "Per student contact hour";
      feeAmount = decimalMul(feeAmount, duration);
      actualFeeAmount = decimalMul(actualFeeAmount, actualDuration);
    }
  }

  return {
    max: isMultiplyable ? decimalMul(feeAmount, maximumPlaces) : feeAmount,
    projected: isMultiplyable ? decimalMul(feeAmount, projectedPlaces) : feeAmount,
    actual: isMultiplyable ? decimalMul(actualFeeAmount, actualPlaces) : actualFeeAmount
  };
};

export const getClassFeeTotal = (costs: ClassCost[], taxes: Tax[]) => {
  const studentFee = costs.find(c => c.invoiceToStudent);
  return studentFee ? getFeeWithTaxAmount(studentFee.perUnitAmountExTax, getCurrentTax(taxes, studentFee.taxId)) : 0;
};

export const dateForCompare = (date: string, customFormat: string) => new Date(format(new Date(date), customFormat));
