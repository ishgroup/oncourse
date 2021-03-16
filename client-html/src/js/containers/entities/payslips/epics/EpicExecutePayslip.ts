import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { POST_PAYSLIP_EXECUTE, POST_PAYSLIP_EXECUTE_FULFILLED } from "../actions";
import PayslipService from "../services/PayslipService";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { Epic } from "redux-observable";
import { PayslipStatus } from "@api/model";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import { FETCH_SUCCESS } from "../../../../common/actions";

const request: EpicUtils.Request<any, { ids: number[]; status: PayslipStatus }> = {
  type: POST_PAYSLIP_EXECUTE,
  getData: ({ ids, status }) => PayslipService.executePayslip(ids, status),
  processData: (v, s, { ids }) => {
    return [
      {
        type: POST_PAYSLIP_EXECUTE_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Payslip status updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Payslip", listUpdate: true, savedID: ids[0] }
      }
    ];
  },
  processError: response => FetchErrorHandler(response, "Payslip execution failed.")
};

export const EpicExecutePayslip: Epic<any, any> = EpicUtils.Create(request);
