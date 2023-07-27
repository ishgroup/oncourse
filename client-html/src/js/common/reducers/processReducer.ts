import { IAction } from "../actions/IshAction";
import { CLEAR_PROCESS, INTERRUPT_PROCESS_FULFILLED, UPDATE_PROCESS } from "../actions";
import { ProcessResult } from "@api/model";

export interface ProcessState extends ProcessResult {
  processId?: string;
}

export const processReducer = (state: ProcessState = {}, action: IAction<any>): any => {
  switch (action.type) {
    case INTERRUPT_PROCESS_FULFILLED:
    case UPDATE_PROCESS: {
      const {process, processId} = action.payload;

      return {
        ...state,
        ...process,
        ...{processId}
      };
    }

    case CLEAR_PROCESS: {
      return {};
    }

    default:
      return state;
  }
};
