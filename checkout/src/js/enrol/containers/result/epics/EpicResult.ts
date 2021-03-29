import {TRY_ANOTHER_CARD} from "../actions/Actions";
import {ActionsObservable, combineEpics, Epic} from "redux-observable";
import {MiddlewareAPI} from "redux";
import {IshState} from "../../../../services/IshState";
import {Observable} from "rxjs/Observable";
import {changePhase} from "../../../actions/Actions";
import {Phase} from "../../../reducers/State";
import {processPaymentV2, resetPaymentState} from "../../payment/actions/Actions";


const TryAnotherCard: Epic<any, IshState> = (action$: ActionsObservable<any>, store: MiddlewareAPI<any, IshState>): Observable<any> => {
  return action$.ofType(TRY_ANOTHER_CARD).flatMap(() => {
    return [
      changePhase(Phase.Payment),
      resetPaymentState(),
      processPaymentV2(
        true
      ),
    ];
  });
};

export const EpicResult = combineEpics(TryAnotherCard);
