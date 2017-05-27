import {MiddlewareAPI} from "redux";
import {ActionsObservable, Epic} from "redux-observable";
import {Observable} from "rxjs";
import "rxjs";

import {OpenPayment} from "../containers/payment/actions/Actions";
import {changePhase} from "../actions/Actions";
import {Phase} from "../reducers/State";

/**
 * This epic process payment action of checkout application and define Phase of the application
 */
const OpenProceedToPaymentEpic: Epic<any, any> = (action$: ActionsObservable<any>, store: MiddlewareAPI<any>): Observable<any> => {
	return action$.ofType(OpenPayment).flatMap((action) => {
		return [changePhase(Phase.Payment)];
	});
}

export default OpenProceedToPaymentEpic;