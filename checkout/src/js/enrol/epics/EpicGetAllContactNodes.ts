import "rxjs";
import * as Actions from "../containers/summary/actions/Actions";
import CheckoutService from "../services/CheckoutService";
import {ContactNode} from "../../model";
import {ActionsObservable, Epic} from "redux-observable";
import {getCheckoutModelFromBackend} from "../actions/Actions";
import {Observable} from "rxjs/Observable";
import {MiddlewareAPI} from "redux";

export const GetAllContactNodes: Epic<any, any> = (action$: ActionsObservable<any>, store: MiddlewareAPI<any>): Observable<any> => {
  return action$.ofType(Actions.GET_ALL_CONTACT_NODES_FROM_BACKEND)
    .mergeMap(action => {
      const state = store.getState();
      const contacts = state.checkout.contacts;

      return Observable.forkJoin(
        contacts.result.map(id => CheckoutService.getContactNode(contacts.entities.contact[id], state.cart)),
      )
      .flatMap((results: ContactNode[]) => {
        return [
          ...results.map(node => Actions.addContactNodeToState(node)),
          getCheckoutModelFromBackend(),
        ];
      });
    });
};
