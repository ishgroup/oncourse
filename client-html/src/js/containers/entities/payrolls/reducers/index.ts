import { IAction } from "../../../../common/actions/IshAction";
import { CLEAR_PAYROLL_PREPARED_WAGES, PAYROLL_PROCESS_FINISHED, PREPARE_PAYROLL_FULFILLED } from "../actions/index";
import { PayrollsState } from "./state";

export const payrollsReducer = (state: PayrollsState = { generated: false }, action: IAction<any>): any => {
  switch (action.type) {
    case PREPARE_PAYROLL_FULFILLED: {
      return {
        ...state,
        ...action.payload,
        generated: false
      };
    }

    case CLEAR_PAYROLL_PREPARED_WAGES: {
      return {
        ...state,
        ...{ preparedWages: null, generated: false }
      };
    }

    case PAYROLL_PROCESS_FINISHED: {
      return {
        ...state,
        ...{ generated: true }
      };
    }

    default:
      return state;
  }
};
