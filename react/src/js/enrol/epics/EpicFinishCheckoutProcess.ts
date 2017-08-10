import {MiddlewareAPI} from "redux";
import {ActionsObservable, Epic} from "redux-observable";
import {Observable} from "rxjs";
import "rxjs";
import {FINISH_CHECKOUT_PROCESS, resetCheckoutState} from "../actions/Actions";
import {IshState} from "../../services/IshState";

/**
 * This epic process Init action of checkout application and define Phase of the application
 */
export const FinishCheckoutProcess: Epic<any, any> = (action$: ActionsObservable<any>, store: MiddlewareAPI<IshState>): Observable<any> => {
  return action$.ofType(FINISH_CHECKOUT_PROCESS).flatMap(action => {
    // TODO need to remove purchased items from card
    return [
      resetCheckoutState(),
    ];
  });
};
