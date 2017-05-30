import {Epic} from "redux-observable";
import "rxjs";
import {ItemSelect, ItemSelectRequest} from "../containers/summary/actions/Actions";
import CheckoutService from "../services/CheckoutService";
import {IshState} from "../../services/IshState";
import * as EpicUtils from "./EpicUtils";
import {Enrolment} from "../../model/checkout/Enrolment";
import {convertFromEnrolment} from "../containers/summary/reducers/State";
import {updateAmountRequest} from "../actions/Actions";

const request: EpicUtils.Request<Enrolment, IshState> = {
  type: ItemSelectRequest,
  getData: CheckoutService.updateEnrolment,
  processData: (value: Enrolment, state: IshState) => {
    return [{type: ItemSelect, payload: convertFromEnrolment(value)}, updateAmountRequest()];
  }
};

const Epic: Epic<any, any> = EpicUtils.Create(request);

export default Epic;