import {MiddlewareAPI} from "redux";
import {ActionsObservable, Epic} from "redux-observable";
import {Observable} from "rxjs";
import "rxjs";

import {changePhase} from "../actions/Actions";
import {OpenSummaryRequest} from "../containers/summary/actions/Actions";
import {Phase} from "../reducers/State";

/**
 * This epic process Init action of checkout application and define Phase of the application
 */
const OpenSummaryEpic: Epic<any, any> = (action$: ActionsObservable<any>, store: MiddlewareAPI<any>): Observable<any> => {
  return action$.ofType(OpenSummaryRequest).flatMap((action) => {
    return [changePhase(Phase.Summary)];
  });
};

export default OpenSummaryEpic;