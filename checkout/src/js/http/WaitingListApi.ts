import {HttpService} from "../common/services/HttpService";
import {WaitingListRequest} from "../model/checkout/waitinglist/WaitingListRequest";
import {CommonError} from "../model/common/CommonError";
import {ValidationError} from "../model/common/ValidationError";
import {FieldHeading} from "../model/field/FieldHeading";

export class WaitingListApi {
  constructor(private http: HttpService) {
  }

  submitWaitingList(request: WaitingListRequest): Promise<any> {
    return this.http.POST(`/submitWaitingList`, request);
  }
  waitingListFields(classId: string): Promise<FieldHeading[]> {
    return this.http.POST(`/waitingListFields/${classId}`);
  }
}
