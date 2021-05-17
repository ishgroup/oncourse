import "rxjs";
import * as Actions from "../containers/summary/actions/Actions";
import {ContactNode} from "../../model";
import {ActionsObservable, Epic} from "redux-observable";
import {getCheckoutModelFromBackend} from "../actions/Actions";
import {Observable} from "rxjs/Observable";
import {MiddlewareAPI} from "redux";
import CheckoutServiceV2 from "../services/CheckoutServiceV2";
import {ContactNodeService} from "../services/ContactNodeService";
import {IshState} from "../../services/IshState";
import {ProcessError} from "../../common/epics/EpicUtils";

export const GetAllContactNodes: Epic<any, any> = (action$: ActionsObservable<any>, store: MiddlewareAPI<any>): Observable<any> => {
  return action$.ofType(Actions.GET_ALL_CONTACT_NODES_FROM_BACKEND)
    .mergeMap(() => {
      const state: IshState = store.getState();
      const contacts = state.checkout.contacts;

      return Observable.forkJoin(
        contacts.result.map(id => CheckoutServiceV2.getContactNode(contacts.entities.contact[id], state.checkout.summary, state.cart, state.checkout.payerId)),
      )
      .flatMap((results: ContactNode[]) => {
        const relationsUpdateActions = ContactNodeService.getRelationsUpdateActions(results);
        return [
          ...results.map(node => Actions.addContactNodeToState(node)),
          getCheckoutModelFromBackend(),
          ...relationsUpdateActions
        ];
      })
      .catch(e => ProcessError(e))
    });
};
