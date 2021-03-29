import {MiddlewareAPI} from "redux";
import {ActionsObservable, Epic} from "redux-observable";
import {Observable} from "rxjs";
import * as L from "lodash";
import {FINISH_CHECKOUT_PROCESS, resetCheckoutState} from "../actions/Actions";
import {IshState} from "../../services/IshState";
import {setResultDetailsContacts, setResultDetailsCorporatePass} from "../containers/summary/actions/Actions";
import {ContactPropsBy} from "../containers/summary/Summary";

/**
 * This epic process Init action of checkout application and define Phase of the application
 */
export const FinishCheckoutProcess: Epic<any, any> = (action$: ActionsObservable<any>, store: MiddlewareAPI<any, IshState>): Observable<any> => {
  return action$.ofType(FINISH_CHECKOUT_PROCESS).flatMap(() => {

    const state = store.getState();

    const contacts = state.checkout.summary.result.map(id => {
      return ContactPropsBy(id, state);
    });

    const corporatePass = state.checkout.summary.resultDetails &&
      state.checkout.summary.resultDetails.corporatePass;

    return [
      resetCheckoutState(),
      setResultDetailsContacts(contacts),
      setResultDetailsCorporatePass(L.cloneDeep(corporatePass))
    ];
  });
};
