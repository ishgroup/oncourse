import { stopSubmit } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import {
  getRunScriptResult,
  openRunScriptPdf,
  runScript,
  POST_SCRIPT_RUN_REQUEST_FULFILLED
} from "../../../js/containers/automation/containers/scripts/actions";
import { EpicRunScriptItem } from "../../../js/containers/automation/containers/scripts/epics/EpicRunScriptItem";
import { START_PROCESS, UPDATE_PROCESS } from "../../../js/common/actions";

describe("Run script item epic test", () => {
  it("EpicRunScriptItem should returns correct values", () => {
    const processId: string = "4n5pxq24kriob12ogd";
    const outputType = "pdf";
    const name = "script 1";
    return DefaultEpic({
      action: runScript({
          scriptId: 1,
          variables: null,
          searchQuery: null
        },
        outputType,
        name),
      epic: EpicRunScriptItem,
      processData: () => [
        { type: POST_SCRIPT_RUN_REQUEST_FULFILLED },
        {
          type: UPDATE_PROCESS,
          payload: { processId }
        },
        {
          type: START_PROCESS,
          payload: {
            processId,
            actionsOnFail: [stopSubmit("ExecuteScriptForm")],
            actions: [
              outputType === "pdf"
                ? openRunScriptPdf(processId)
                : getRunScriptResult(processId, outputType, name)
            ]
          }
        }
      ]
    });
  });
});
