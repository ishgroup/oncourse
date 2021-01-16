import {MiddlewareAPI} from "redux";
import {ActionsObservable, Epic} from "redux-observable";
import {Observable} from "rxjs";
import "rxjs";
import {FINISH_CHECKOUT_PROCESS, resetCheckoutState} from "../actions/Actions";
import {IshState} from "../../services/IshState";
import {setResultDetails} from "../containers/summary/actions/Actions";
import {ContactPropsBy} from "../containers/summary/Summary";

/**
 * This epic process Init action of checkout application and define Phase of the application
 */
export const FinishCheckoutProcess: Epic<any, any> = (action$: ActionsObservable<any>, store: MiddlewareAPI<IshState>): Observable<any> => {
  return action$.ofType(FINISH_CHECKOUT_PROCESS).flatMap(action => {

    const state = store.getState();

    const details = state.checkout.summary.result.map(id => {
      return ContactPropsBy(id, state);
    });

    return [
      resetCheckoutState(),
      setResultDetails(details)
    ];
  });
};
