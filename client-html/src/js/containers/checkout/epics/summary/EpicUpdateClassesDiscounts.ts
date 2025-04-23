/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { decimalPlus } from 'ish-ui';
import { change } from 'redux-form';
import { Epic, ofType, StateObservable } from 'redux-observable';
import { debounceTime, mergeMap, Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import FetchErrorHandler from '../../../../common/api/fetch-errors-handlers/FetchErrorHandler';
import { State } from '../../../../reducers/state';
import {
  CHECKOUT_UPDATE_SUMMARY_CLASSES_DISCOUNTS,
  checkoutUpdateSummaryItems,
  checkoutUpdateSummaryPrices
} from '../../actions/checkoutSummary';
import { CHECKOUT_SUMMARY_FORM as summmaryForm } from '../../components/summary/CheckoutSummaryList';
import CheckoutService from '../../services/CheckoutService';

export const EpicUpdateClassesDiscounts: Epic<any, State> = (action$: Observable<any>, $state: StateObservable<State>): any => action$.pipe(
    ofType(
      CHECKOUT_UPDATE_SUMMARY_CLASSES_DISCOUNTS
    ),
    debounceTime(1000),
    mergeMap(async ({ payload: forcePricesUpdate }) => {
      const state = $state.value;

      const codes = state.checkout.summary.discounts.map(d => d.id).join(",");

      let totalEnrolmentsCount = 0;
      let totalAmountExDiscount = 0;
      const payerId = state.checkout.summary.list.find(l => l.payer)?.contact.id;
      const enrolments = [];

      state.checkout.summary.list.forEach((l, listIndex) => {
        l.items.forEach((i, itemIndex) => {
          if (i.checked) {
            const originalItem = state.checkout.items.find(s => s.id === i.id && s.type === i.type) as any;
            if (!originalItem) {
              return;
            }
            totalAmountExDiscount = decimalPlus(totalAmountExDiscount, parseFloat(originalItem.price));

            if (i.type === "course" && i.class) {
              totalEnrolmentsCount++;

              const courseItems = l.items.filter(item => item.checked && item.type === "course");

              enrolments.push({
                contactId: l.contact.id,
                classItem: i,
                payerId,
                courseIds: courseItems.map(item => item.courseId).toString(),
                productIds: l.items.filter(item => item.checked && item.type !== "course").map(item => item.id).toString(),
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

      console.log('!!!!!!', { enrolments, forcePricesUpdate, items });

      await Promise.all(
        enrolments.map(e =>
          CheckoutService
            .getContactDiscounts(
              e.contactId,
              e.classItem.class.id,
              e.courseIds,
              e.productIds,
              enrolments.map(enrolment => enrolment.classItem.class.id).toString(),
              codes,
              e.membershipIds,
              totalAmountExDiscount,
              e.payerId
            )
            .then(res => {
              const discounts = res.map(i => i.discount);

              const selectedDiscount = e.classItem && e.classItem.relationDiscount
                ? discounts.find(d => d.id === e.classItem.relationDiscount.id) || discounts[0]
                : discounts[0];

              if (!state.checkout.disableDiscounts) {
                actions.push(
                  change(summmaryForm, `${e.classItem.id}_${e.listIndex}_discount`, selectedDiscount),
                );
              }

              items.push({
                listIndex: e.listIndex,
                itemIndex: e.itemIndex,
                item: { ...e.classItem, discounts, discount: state.checkout.disableDiscounts ? null : selectedDiscount }
              });
            }))
      )
      .catch(res => {
        actions.push(...FetchErrorHandler(res, "Failed to get class discounts"));
      });

      if (items.length) {
        actions.splice(0, 0, checkoutUpdateSummaryItems(items));
      }

      if (forcePricesUpdate || items.length) {
        actions.push(checkoutUpdateSummaryPrices());
      }

      return actions;
    }),
    mergeMap(action$ => action$),
    catchError(res => FetchErrorHandler(res, "Failed to get class discounts"))
  );