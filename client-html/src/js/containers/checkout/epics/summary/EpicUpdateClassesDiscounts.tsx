/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { change } from "redux-form";
import { Epic } from "redux-observable";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { decimalPlus } from "../../../../common/utils/numbers/decimalCalculation";
import { State } from "../../../../reducers/state";
import {
  CHECKOUT_UPDATE_SUMMARY_CLASSES_DISCOUNTS, checkoutUpdateSummaryItems, checkoutUpdateSummaryPrices
} from "../../actions/checkoutSummary";
import { CHECKOUT_SUMMARY_FORM as summmaryForm } from "../../components/summary/CheckoutSummaryList";
import CheckoutService from "../../services/CheckoutService";

const request: EpicUtils.Request<any, State, boolean> = {
  type: CHECKOUT_UPDATE_SUMMARY_CLASSES_DISCOUNTS,
  getData: (forcePricesUpdate, state) => new Promise(async resolve => {
    const codes = state.checkout.summary.discounts.map(d => d.id).join(",");

    let totalEnrolmentsCount = 0;
    let totalAmountExDiscount = 0;
    const enrolments = [];

    state.checkout.summary.list.forEach((l, listIndex) => {
      l.items.forEach((i, itemIndex) => {
        if (i.checked) {
          const originalPrice = state.checkout.items.find(s => s.id === i.id && s.type === i.type).price as any;
          totalAmountExDiscount = decimalPlus(totalAmountExDiscount, parseFloat(originalPrice));

          if (i.type === "course") {
            totalEnrolmentsCount++;

            enrolments.push({
              contactId: l.contact.id,
              classItem: i,
              listIndex,
              itemIndex,
              membershipIds: l.items.filter(i => i.type === "membership" && i.checked).map(i => i.id).join(",")
            });
          }
        }
      });
    });

    const actions = [];
    const items = [];

    await enrolments.map(e => () =>
      CheckoutService.getContactDiscounts(e.contactId, e.classItem.class.id, codes, e.membershipIds, totalEnrolmentsCount, totalAmountExDiscount)
        .then(res => {
          const discounts = res.map(i => i.discount);

          if (!state.checkout.disableDiscounts) {
            actions.push(
              change(summmaryForm, `${e.classItem.id}_${e.listIndex}_discount`, discounts[0]),
            );
          }

          items.push({
            listIndex: e.listIndex,
            itemIndex: e.itemIndex,
            item: { ...e.classItem, discounts, discount: state.checkout.disableDiscounts ? null : discounts[0] }
          });
        })
        .catch(res => {
          actions.push(...FetchErrorHandler(res, "Failed to get class discounts"));
        })).reduce(async (a, b) => {
      await a;
      await b();
    }, Promise.resolve());

    if (items.length) {
      actions.splice(0, 0, checkoutUpdateSummaryItems(items));
    }

    if (forcePricesUpdate || items.length) {
      actions.push(checkoutUpdateSummaryPrices());
    }

    resolve(actions);
  }),
  processData: actions => actions,
  processError: res => FetchErrorHandler(res, "Failed to get class discounts")
};

export const EpicUpdateClassesDiscounts: Epic<any, any> = EpicUtils.Create(request);

