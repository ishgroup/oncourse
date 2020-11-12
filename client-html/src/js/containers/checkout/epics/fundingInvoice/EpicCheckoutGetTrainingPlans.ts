/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { format } from "date-fns";
import { getFormValues, initialize } from "redux-form";
import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { D_MMM_YYYY, YYYY_MM_DD_MINUSED } from "../../../../common/utils/dates/format";
import { CheckoutFundingInvoiceItem } from "../../../../model/checkout/fundingInvoice";
import CourseClassAssessmentService from "../../../entities/courseClasses/components/assessments/services/CourseClassAssessmentService";
import CourseClassAttendanceService from "../../../entities/courseClasses/components/attendance/services/CourseClassAttendanceService";
import CourseClassTimetableService from "../../../entities/courseClasses/components/timetable/services/CourseClassTimetableService";
import {
  CHECKOUT_FUNDING_INVOICE_GET_TRAINING_PLANS
} from "../../actions/checkoutFundingInvoice";
import { CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM } from "../../components/fundingInvoice/CheckoutFundingInvoiceSummaryList";

const request: EpicUtils.Request = {
  type: CHECKOUT_FUNDING_INVOICE_GET_TRAINING_PLANS,
  getData: ({ classId }) =>
    CourseClassAttendanceService.getTrainingPlans(classId).then(plans =>
    CourseClassTimetableService.findTimetableSessionsForCourseClasses(classId).then(sessions => ({
      plans,
      sessions
    }))),
  processData: (response, state) => {
    const { plans, sessions } = response;

    const trainingPlansBase = [];
    const trainingPlans = [];
    const formValues = getFormValues(CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM)(state) as CheckoutFundingInvoiceItem;

    if (state.checkout.fundingInvoice.item && plans.length) {
      const units = sessions.map(({ id, start }) => ({
        id,
        start
      }));

      units.sort((a, b) => (new Date(a.start) > new Date(b.start) ? 1 : -1));

      const checkedIds: any = {};
      const commencedIds = [];
      const completedIds = [];

      units.forEach(({ id, start }, index) => {
        const sDate = new Date(start);
        const sDateFormatted = format(sDate, D_MMM_YYYY);

        plans.forEach(plan => {
          if (!checkedIds[plan.moduleId]) {
            checkedIds[plan.moduleId] = 0;
          }

          plan.sessionIds.forEach(psid => {
            const checkCompleted = () => {
              if (checkedIds[plan.moduleId] > 0
                && checkedIds[plan.moduleId] === plan.sessionIds.length
                && !completedIds.includes(plan.moduleId)) {
                completedIds.push(plan.moduleId);
              }
            };

            if (index !== units.length - 1) {
              checkCompleted();
            }

           if (psid === id) {
             if (!commencedIds.includes(plan.moduleId)) {
               commencedIds.push(plan.moduleId);
             }
             checkedIds[plan.moduleId]++;
           }

            if (index === units.length - 1) {
              checkCompleted();
            }
          });
        });

        trainingPlansBase.push({
          date: sDateFormatted,
          unitsCommenced: commencedIds.length,
          unitsCompleted: completedIds.length
        });
      });

      trainingPlansBase.sort((a, b) => (new Date(a.date) > new Date(b.date) ? 1 : -1));

      trainingPlansBase.forEach((tp, index, arr) => {
        const prev = arr[index - 1];

        if (!prev || !(prev.unitsCommenced === tp.unitsCommenced && prev.unitsCompleted === tp.unitsCompleted)) {
          trainingPlans.push(tp);
        }
      });
    }

    let paymentPlans = formValues.paymentPlans;

    if (trainingPlans.length) {
      paymentPlans = [
        paymentPlans[0],
      ].concat(trainingPlans.map(({ date }) => ({
        amount: 0,
        date: format(new Date(date), YYYY_MM_DD_MINUSED),
        entityName: "InvoiceDueDate",
        id: null,
        successful: false,
        type: "Payment due"
      })));
      paymentPlans[paymentPlans.length - 1].amount = formValues.total;
    }

    return [
      initialize(CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM,
        {
          ...formValues,
          trainingPlans,
          paymentPlans
        })
    ];
  }
};

export const EpicCheckoutGetTrainingPlans: Epic<any, any> = EpicUtils.Create(request);
