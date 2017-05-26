import {MiddlewareAPI} from "redux";
import {ActionsObservable, Epic} from "redux-observable";
import {Observable} from "rxjs";
import "rxjs";
import {ItemsLoad, OpenSummaryRequest} from "../containers/summary/actions/Actions";
import CheckoutService from "../services/CheckoutService";
import {PurchaseItems} from "../../model/checkout/PurchaseItems";
import {IshState} from "../../services/IshState";
import {changePhase, MessagesShow} from "../actions/Actions";
import {Phase} from "../reducers/State";
import {toValidationError} from "../../common/utils/ErrorUtils";


interface Request<V, S> {
  type: string,
  getData: (state: S) => Promise<V>
  processData: (value: V, state: S) => { type: string; payload: any }[]
  catchError: (data: any) => { type: string, payload: any }
}

const processPurchaseItems = (items: PurchaseItems, state: IshState): { type: string; payload: any }[] => {
  if (state.checkout.purchaseItems.length == 0) {
    return [{type: ItemsLoad, payload: items}, changePhase(Phase.Summary)];
  } else {
    return [changePhase(Phase.Summary)]
  }
};

const catchError = (data: any): { type: string, payload: any } => {
  return {type: MessagesShow, payload: toValidationError(data)}
};

/**
 * This epic loads PurchaseItems for payer and change phase to Summary
 */
const OpenSummaryEpic: Epic<any, any> = (action$: ActionsObservable<any>, store: MiddlewareAPI<IshState>): Observable<any> => {
  const request: Request<PurchaseItems, IshState> = {
    type: OpenSummaryRequest,
    getData: CheckoutService.getPurchaseItems,
    processData: processPurchaseItems,
    catchError: catchError,
  };

  return action$
    .ofType(request.type)
    .mergeMap(action => Observable.fromPromise(request.getData(store.getState())))
    .flatMap(data => request.processData(data, store.getState()))
    .catch((data) => {
      return Observable.of(request.catchError(data))
    });
};


export default OpenSummaryEpic;