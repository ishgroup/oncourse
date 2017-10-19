import {HttpService} from "../common/services/HttpService";
import {WaitingListRequest} from "../model/checkout/waitinglist/WaitingListRequest";
import {CommonError} from "../model/common/CommonError";
import {ValidationError} from "../model/common/ValidationError";

export class WaitingListApi {
  constructor(private http: HttpService) {
  }

  submitWaitingList(request: WaitingListRequest): Promise<any> {
    return this.http.POST(`/submitWaitingList`, request);
  }
}
