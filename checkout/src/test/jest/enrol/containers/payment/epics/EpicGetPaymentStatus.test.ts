import "rxjs";
import {ActionsObservable} from "redux-observable";

import {GET_PAYMENT_STATUS} from "../../../../../../js/enrol/containers/payment/actions/Actions";
import {Request} from "../../../../../../js/common/epics/EpicUtils";
import {Observable} from "rxjs/Observable";


test('test ProcessPayment epic', () => {

  let count: number = 0;
  const request: Request<string, any> = {
    type: GET_PAYMENT_STATUS,
    getData: () => {
      count++;
      if (count < 3)
        return Promise.reject("ERROR");
      else {
        return Promise.resolve("SUCCESS");
      }
    },
    processData: (data: string) => Observable.of(data),
    processError: (data: string) => Observable.of(data)
  };


  const action$: ActionsObservable<any> = new ActionsObservable(Observable.from([{type: GET_PAYMENT_STATUS}]));
  action$.ofType(request.type).mergeMap((action) => Observable
    .defer(() => request.getData(null, null)).retry(2)
    .flatMap((data: string) => request.processData(data, null))
    .catch((data) => request.processError(data))
  ).subscribe((data) => console.log(`next: ${data}`),
    (data) => console.log(`error: ${data}`),
    () => console.log(`completed`)
  )
});
