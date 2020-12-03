/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ActionsObservable, Epic } from "redux-observable";
import { mergeMap } from "rxjs/operators";
import { State } from "../../../../reducers/state";
import {
  CHECKOUT_ADD_CONTACT, CHECKOUT_ADD_ITEM, CHECKOUT_REMOVE_CONTACT, CHECKOUT_REMOVE_ITEM, CHECKOUT_UPDATE_RELATED_ITEMS
} from "../../actions";
import { CHECKOUT_UPDATE_CONTACT_FULFILLED } from "../../actions/checkoutContact";
import { checkoutUpdateSummaryPrices } from "../../actions/checkoutSummary";

export const EpicTriggerPricesUpdate: Epic<any, any, State> = (action$: ActionsObservable<any>, state$): any =>
  action$.ofType(
    CHECKOUT_ADD_ITEM,
    CHECKOUT_REMOVE_ITEM,
    CHECKOUT_ADD_CONTACT,
    CHECKOUT_UPDATE_CONTACT_FULFILLED,
    CHECKOUT_REMOVE_CONTACT,
    CHECKOUT_UPDATE_RELATED_ITEMS
    ).pipe(
    mergeMap(() => (
      state$.value.checkout.contacts.length && state$.value.checkout.items.length ? [checkoutUpdateSummaryPrices()] : []))
  );

