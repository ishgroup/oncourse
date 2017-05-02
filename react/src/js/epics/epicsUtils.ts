import {FULFILLED, REJECTED} from "../constants/IshActions";
import {Observable} from "rxjs";

export function mapPayload(actionType: string) {
  return function (payload: any) {
    return {
      type: FULFILLED(actionType),
      payload
    };
  }
}

export function mapError(actionType: string) {
  return function (payload: any) {
    return Observable.of({
      type: REJECTED(actionType),
      payload,
      error: true
    });
  }
}
