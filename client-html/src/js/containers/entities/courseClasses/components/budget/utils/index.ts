/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ClassCost, CourseClassPaymentPlan, Tax } from "@api/model";
import { differenceInMinutes, format, subMinutes } from "date-fns";
import {
  decimalDivide,
  decimalMul,
  decimalPlus
} from "../../../../../../common/utils/numbers/decimalCalculation";
import { TimetableSession } from "../../../../../../model/timetable";
import { ClassCostExtended } from "../../../../../../model/entities/CourseClass";
import { getCurrentTax } from "../../../../taxes/utils";

export const discountsSort = (a, b) => (a.description > b.description ? 1 : -1);

export const getFeeWithTaxAmount = (exTaxFee: number, currentTax: Tax) => decimalMul(exTaxFee, decimalPlus(currentTax.rate, 1));

export const getPaymentPlansTotal = (paymentPlans: CourseClassPaymentPlan[]) =>
  paymentPlans.reduce((p: number, c) => (c.dayOffset === null ? p : decimalPlus(p, c.amount)), 0);

export const excludeOnEnrolPaymentPlan = (item: ClassCostExtended, currentTax: Tax) => {
  const result = { ...item };

  if (result.flowType === "Income" && result.invoiceToStudent && result.paymentPlan.length) {
    result.perUnitAmountIncTax = result.paymentPlan.find(p => p.dayOffset === null).amount;
    result.perUnitAmountExTax = decimalDivide(result.perUnitAmountIncTax, decimalPlus(currentTax.rate, 1));
    result.paymentPlan = result.paymentPlan.filter(p => p.dayOffset !== null);
  }
  
  return result;
};

export const includeOnEnrolPaymentPlan = (item: ClassCostExtended, taxes: Tax[]) => {
  const currentTax = getCurrentTax(taxes, item.taxId);
  const result = { ...item };

  if (result.flowType === "Income" && result.invoiceToStudent && result.paymentPlan.length) {
    result.paymentPlan = [
      { dayOffset: null, amount: result.perUnitAmountIncTax },
      ...result.paymentPlan
    ];

    result.perUnitAmountIncTax = result.paymentPlan.reduce((p: number, c) => decimalPlus(p, c.amount), 0);

    result.perUnitAmountExTax = decimalDivide(result.perUnitAmountIncTax, decimalPlus(currentTax.rate, 1));
  }

  return result;
};

const getSessionPayable = (
  item: ClassCostExtended,
  s: TimetableSession,
  p: number,
) => {
  const attendance = s.tutorAttendances?.find(a => a.attendanceType !== "Rejected for payroll"
    && ((a.courseClassTutorId && a.courseClassTutorId === item.courseClassTutorId) || (a.temporaryTutorId && a.temporaryTutorId === item.temporaryTutorId)));

  if (item.flowType === "Wages") {
    let isApplied = true;

    if (attendance) {
      isApplied = true;
      if (typeof attendance.actualPayableDurationMinutes === "number") {
        return decimalPlus(decimalDivide(attendance.actualPayableDurationMinutes, 60), p);
      }
    }

    if (item.courseClassTutorId && !s.tutorAttendances.some(ta => ta.courseClassTutorId === item.courseClassTutorId)) {
      isApplied = false;
    }

    if (item.temporaryTutorId && !s.tutorAttendances.some(ta => ta.temporaryTutorId === item.temporaryTutorId)) {
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
      const end = subMinutes(new Date(s.end), attendance?.actualPayableDurationMinutes || 0);

      start.setSeconds(0, 0);
      end.setSeconds(0, 0);

      return decimalPlus(decimalDivide(differenceInMinutes(end, start), 60), p);
    }
  }
};

export const getClassCostFee = (
  item: ClassCostExtended,
  maximumPlaces: number,
  projectedPlaces: number,
  actualPlaces: number,
  sessions: TimetableSession[],
) => {
  let feeAmount = item.perUnitAmountExTax;
  let isMultiplyable = true;

  switch (item.repetitionType) {
    case "Fixed": {
      isMultiplyable = false;
      break;
    }
    case "Per session": {
      isMultiplyable = false;

      const sessionsLength = item.flowType === "Wages"
        ? sessions.reduce((p, s) => getSessionPayable(item, s, p), 0)
        : sessions.length;

      feeAmount = decimalMul(feeAmount, sessionsLength);
      break;
    }
    case "Per enrolment": {
      isMultiplyable = true;
      break;
    }
    case "Per unit": {
      isMultiplyable = false;
      feeAmount = decimalMul(feeAmount, item.unitCount);
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
      const duration = sessions.reduce((p, s) => getSessionPayable(item, s, p), 0);
      isMultiplyable = item.repetitionType === "Per student contact hour";
      feeAmount = decimalMul(feeAmount, duration);
    }
  }

  return {
    max: isMultiplyable ? decimalMul(feeAmount, maximumPlaces) : feeAmount,
    projected: isMultiplyable ? decimalMul(feeAmount, projectedPlaces) : feeAmount,
    actual: item.actualAmount || 0
  };
};

export const getClassFeeTotal = (costs: ClassCost[], taxes: Tax[]) => {
  const studentFee = costs.find(c => c.invoiceToStudent);
  return studentFee ? getFeeWithTaxAmount(studentFee.perUnitAmountExTax, getCurrentTax(taxes, studentFee.taxId)) : 0;
};

export const dateForCompare = (date: string, customFormat: string) => new Date(format(new Date(date), customFormat));
