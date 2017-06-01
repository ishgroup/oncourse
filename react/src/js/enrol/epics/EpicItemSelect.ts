import {Epic} from "redux-observable";
import "rxjs";
import {SELECT_ITEM, SELECT_ITEM_REQUEST} from "../containers/summary/actions/Actions";
import CheckoutService from "../services/CheckoutService";
import {IshState} from "../../services/IshState";
import * as EpicUtils from "./EpicUtils";
import {Enrolment} from "../../model/checkout/Enrolment";
import {EnrolmentToState} from "../containers/summary/reducers/State";
import {updateAmountRequest} from "../actions/Actions";

const request: EpicUtils.Request<Enrolment, IshState> = {
  type: SELECT_ITEM_REQUEST,
  getData: CheckoutService.updateEnrolment,
  processData: (value: Enrolment, state: IshState) => {
    return [{type: SELECT_ITEM, payload: EnrolmentToState(value)}, updateAmountRequest()];
  }
};

export const EpicItemSelect: Epic<any, any> = EpicUtils.Create(request);
