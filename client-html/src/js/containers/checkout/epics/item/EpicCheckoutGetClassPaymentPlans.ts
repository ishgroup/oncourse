/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { addDays, isBefore } from "date-fns";
import { Epic } from "redux-observable";
import { ClassCost } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { decimalPlus } from "../../../../common/utils/numbers/decimalCalculation";
import { CheckoutCourse } from "../../../../model/checkout";
import ClassCostService from "../../../entities/courseClasses/components/budget/services/ClassCostService";
import { updateClassItem } from "../../actions";
import { checkoutChangeSummaryItemField } from "../../actions/checkoutSummary";
import { CHECKOUT_GET_CLASS_PAYMENT_PLANS } from "../../actions/chekoutItem";

const request: EpicUtils.Request<ClassCost[], { item: CheckoutCourse }> = {
  type: CHECKOUT_GET_CLASS_PAYMENT_PLANS,
  getData: ({ item }) => ClassCostService.getCourseClassCosts(item.class.id),
  processData: (costs, s, { item }) => {
    const studentFee = costs.find(c => c.invoiceToStudent);

    let paymentPlans = [];

    const toomorrow = addDays(new Date(), 1);
    toomorrow.setHours(0, 0, 0, 0);

    if (studentFee && studentFee.paymentPlan.length) {
      const classStart = item.class.startDateTime ? new Date(item.class.startDateTime) : new Date();

      classStart.setHours(0, 0, 0, 0);

      let passedPlansPrice = 0;

      paymentPlans = studentFee.paymentPlan
        .map(({ amount, dayOffset }) => {
          const date = typeof dayOffset === "number" ? addDays(classStart, dayOffset) : null;

          if (date && isBefore(date, toomorrow)) {
            passedPlansPrice = decimalPlus(amount, passedPlansPrice);
            return null;
          }

          return {
            amount,
            date
          };
        })
        .filter(p => p);

      paymentPlans.sort((a, b) => (a.date > b.date ? 1 : -1));
      paymentPlans[0].amount = decimalPlus(paymentPlans[0].amount, passedPlansPrice);
    }

    const targetItem = s.checkout.items.find(i => i.id === item.id && item.type === i.type);

    const summaryActions = [];

    s.checkout.summary.list.forEach((l, listIndex) => {
      l.items.forEach((li, itemIndex) => {
        if (li.id === item.id && item.type === li.type) {
          summaryActions.push(checkoutChangeSummaryItemField(
            listIndex,
            itemIndex,
            paymentPlans,
            "paymentPlans"
          ));
        }
      });
    });

    return [
      updateClassItem({ ...targetItem, paymentPlans }),
      ...summaryActions
    ];
  },
  processError: response => FetchErrorHandler(response, "Failed to get class payment plans")
};

export const EpicCheckoutGetClassPaymentPlans: Epic<any, any> = EpicUtils.Create(request);
