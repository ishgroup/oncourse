import {IAction} from "../../../../actions/IshAction";
import {CANCEL_CHECKOUT_PROCESS, TRY_ANOTHER_CARD} from "../actions/Actions";
import {ActionsObservable, combineEpics, Epic} from "redux-observable";
import {MiddlewareAPI} from "redux";
import {IshState} from "../../../../services/IshState";
import {Observable} from "rxjs/Observable";
import {changePhase, resetCheckoutState, sendInitRequest} from "../../../actions/Actions";
import {Phase} from "../../../reducers/State";
import {resetPaymentState} from "../../payment/actions/Actions";


const TryAnotherCard: Epic<any, IshState> = (action$: ActionsObservable<any>, store: MiddlewareAPI<IshState>): Observable<any> => {
  return action$.ofType(TRY_ANOTHER_CARD).flatMap((action: IAction<any>) => {
    return [changePhase(Phase.Payment), resetPaymentState()];
  });
};


const CancelCheckoutProcess: Epic<any, IshState> = (action$: ActionsObservable<any>, store: MiddlewareAPI<IshState>): Observable<any> => {
  return action$.ofType(CANCEL_CHECKOUT_PROCESS).flatMap((action: IAction<any>) => {
    return [resetCheckoutState(), changePhase(Phase.Init), resetPaymentState(), sendInitRequest()];
  });
};

export const EpicResult = combineEpics(TryAnotherCard, CancelCheckoutProcess);