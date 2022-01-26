/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import 'rxjs';
import { ActionsObservable, Epic } from 'redux-observable';
import { Observable } from 'rxjs/Observable';
import { MiddlewareAPI } from 'redux';
import * as Actions from '../containers/summary/actions/Actions';
import { ContactNode } from '../../model';
import { getCheckoutModelFromBackend } from '../actions/Actions';
import CheckoutServiceV2 from '../services/CheckoutServiceV2';
import { ContactNodeService } from '../services/ContactNodeService';
import { IshState } from '../../services/IshState';
import { ProcessError } from '../../common/epics/EpicUtils';

export const GetAllContactNodes: Epic<any, any> = (action$: ActionsObservable<any>, store: MiddlewareAPI<any>): Observable<any> => action$.ofType(Actions.GET_ALL_CONTACT_NODES_FROM_BACKEND)
  .mergeMap(() => {
    const state: IshState = store.getState();
    const { contacts } = state.checkout;

    return Observable.forkJoin(
      contacts.result.map((id) => CheckoutServiceV2.getContactNode(contacts.entities.contact[id], state.checkout.summary, state.cart, state.checkout.payerId)),
    )
      .flatMap((results: ContactNode[]) => {
        const relationsUpdateActions = ContactNodeService.getRelationsUpdateActions(results);
        return [
          ...results.map((node) => Actions.addContactNodeToState(node)),
          getCheckoutModelFromBackend(),
          ...relationsUpdateActions
        ];
      })
      .catch((e) => ProcessError(e));
  });
