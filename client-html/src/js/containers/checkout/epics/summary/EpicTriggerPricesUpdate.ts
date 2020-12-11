/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ActionsObservable, Epic, ofType } from "redux-observable";
import { debounce, mergeMap } from "rxjs/operators";
import { interval } from "rxjs";
import { State } from "../../../../reducers/state";
import {
  CHECKOUT_ADD_CONTACT, CHECKOUT_ADD_ITEM, CHECKOUT_REMOVE_CONTACT, CHECKOUT_REMOVE_ITEM, CHECKOUT_UPDATE_RELATED_ITEMS
} from "../../actions";
import { CHECKOUT_UPDATE_CONTACT_FULFILLED } from "../../actions/checkoutContact";
import {
  CHECKOUT_TRIGGER_UPDATE_SUMMARY_PRICES,
  CHECKOUT_UPDATE_SUMMARY_PRICES
} from "../../actions/checkoutSummary";

export const EpicTriggerPricesUpdate: Epic<any, any, State> = (action$: ActionsObservable<any>, state$): any =>
  action$.pipe(
    ofType(
      CHECKOUT_ADD_ITEM,
      CHECKOUT_REMOVE_ITEM,
      CHECKOUT_ADD_CONTACT,
      CHECKOUT_UPDATE_CONTACT_FULFILLED,
      CHECKOUT_REMOVE_CONTACT,
      CHECKOUT_UPDATE_RELATED_ITEMS,
      CHECKOUT_TRIGGER_UPDATE_SUMMARY_PRICES
    ),
    debounce(() => interval(500)),
    mergeMap(() => {
      if (state$.value.checkout.contacts.length && state$.value.checkout.items.length) {
        return [{
          type: CHECKOUT_UPDATE_SUMMARY_PRICES
        }];
      }
      return [];
    })
  );

