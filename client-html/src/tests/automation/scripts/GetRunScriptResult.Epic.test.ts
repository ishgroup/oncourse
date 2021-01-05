import { stopSubmit } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_RUN_SCRIPT_RESULT,
  GET_RUN_SCRIPT_RESULT_FULFILLED,
} from "../../../js/containers/automation/containers/scripts/actions";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { EpicGetRunScriptResult } from "../../../js/containers/automation/containers/scripts/epics/EpicGetRunScriptResult";

describe("Get run script result epic tests", () => {
  it("EpicGetRunScriptResult should returns correct values", () => DefaultEpic({
    action: {
      type: GET_RUN_SCRIPT_RESULT,
      payload: { processId: 1 }
    },
    epic: EpicGetRunScriptResult,
    processData: () => [
      stopSubmit("ExecuteScriptForm"),
      { type: GET_RUN_SCRIPT_RESULT_FULFILLED },
      { type: FETCH_SUCCESS, payload: { message: "Script execution has finished" } }
    ]
  }));
});
