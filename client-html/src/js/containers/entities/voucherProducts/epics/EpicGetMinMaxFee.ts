import { DataResponse } from "@api/model";
import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_VOUCHER_PRODUCT_MIN_MAX_FEE, GET_VOUCHER_PRODUCT_MIN_MAX_FEE_FULFILLED } from "../actions";
import EntityService from "../../../../common/services/EntityService";

const getFee = (ids: string, asc: boolean) =>
  EntityService.getPlainRecords(
    "CourseClass",
    "feeExGst",
    `course.id in (${ids}) and isCancelled == 0 and (endDateTime >= today or endDateTime == null)`,
    1,
    0,
    "feeExGst",
    asc
  );

const extractFee = (response: DataResponse) => (response.pageSize > 0 ? response.rows[0].values[0] : 0);

const request: EpicUtils.Request<any, string> = {
  type: GET_VOUCHER_PRODUCT_MIN_MAX_FEE,
  getData: ids => Promise.all([getFee(ids, true), getFee(ids, false)]),
  processData: (response: DataResponse[]) => {
    const actions = [
      {
        type: GET_VOUCHER_PRODUCT_MIN_MAX_FEE_FULFILLED,
        payload: {
          minFee: extractFee(response[0]),
          maxFee: extractFee(response[1])
        }
      }
    ];
    return actions;
  }
};

export const EpicGetMinMaxFee: Epic<any, any> = EpicUtils.Create(request);
