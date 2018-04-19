import {Epic} from "redux-observable";
import "rxjs";
import {IshState} from "../../../../services/IshState";
import * as EpicUtils from "../../../epics/EpicUtils";
import {GET_CONCESSION_TYPES_REQUEST} from "../actions/Actions";
import CheckoutService from "../../../services/CheckoutService";
import {ConcessionType as ConcessionTypeModel} from "../../../../model";
import {FULFILLED} from "../../../../common/actions/ActionUtils";

//creates Epic that gets available ConcessionTypes and updates app state with them
const request: EpicUtils.Request<any, IshState> = {
  type: GET_CONCESSION_TYPES_REQUEST,
  getData: () => CheckoutService.getConcessionTypes(),
  processData: (value: ConcessionTypeModel[], state: IshState) => {

    return [{
      type: FULFILLED(GET_CONCESSION_TYPES_REQUEST),
      payload: value,
    }];
  },
};

export const EpicGetConcessionTypes: Epic<any, IshState> = EpicUtils.Create(request);
