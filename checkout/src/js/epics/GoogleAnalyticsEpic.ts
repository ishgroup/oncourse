import {ActionsObservable, Epic} from "redux-observable";
import {MiddlewareAPI} from "redux";
import {IshState} from "../services/IshState";
import {initGAEvent} from "../services/GoogleAnalyticsService";


export const GoogleAnalyticsEpic: Epic<any, IshState> = (action$: ActionsObservable<any>, store: MiddlewareAPI<IshState>): any => {
  return action$
    .filter(action => action.meta && action.meta.analytics)
    .mergeMap(action => {
      initGAEvent(action.meta.analytics);
      return [];
    })
}