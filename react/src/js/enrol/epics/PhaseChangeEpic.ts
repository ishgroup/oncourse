import {MiddlewareAPI} from "redux";
import {ActionsObservable, Epic} from "redux-observable";
import {Observable} from "rxjs";
import "rxjs";
import {changePhase, PhaseChangeRequest} from "../actions/Actions";
import {Phase} from "../reducers/State";
import {OpenContactDetailsRequest} from "../containers/contact-edit/actions/Actions";
import {OpenSummaryRequest} from "../containers/summary/actions/Actions";
import {OpenPayment} from "../containers/payment/actions/Actions";

const PhaseChangeEpic: Epic<any, any> = (action$: ActionsObservable<any>, store: MiddlewareAPI<any>): Observable<any> => {
  return action$
    .ofType(PhaseChangeRequest).flatMap((action) => {
      switch (action.payload) {
        case Phase.EditContact:
          return [{type: OpenContactDetailsRequest}];
        case Phase.Summary:
          return [{type: OpenSummaryRequest}];
        case Phase.Payment:
          return [{type: OpenPayment}];
        default:
          return [changePhase(action.payload)];
      }
    });
};

export default PhaseChangeEpic;

