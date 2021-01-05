import { stopSubmit } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import {
  OPEN_RUN_SCRIPT_PDF_FULFILLED,
  openRunScriptPdf
} from "../../../js/containers/automation/containers/scripts/actions";
import { EpicOpenRunScriptPdf } from "../../../js/containers/automation/containers/scripts/epics/EpicOpenRunScriptPdf";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { CONTEXT } from "../../../js/common/api/Constants";

describe("Open run script pdf epic tests", () => {
  it("EpicOpenRunScriptPdf should returns correct values", () => DefaultEpic({
    action: openRunScriptPdf("1"),
    epic: EpicOpenRunScriptPdf,
    beforeProcess: () => {
      window.open = jest.fn();
      window.open(`${CONTEXT}v1/list/entity/script/execute/pdf/1`);
    },
    processData: () => [
      stopSubmit("ExecuteScriptForm"),
      { type: OPEN_RUN_SCRIPT_PDF_FULFILLED },
      { type: FETCH_SUCCESS, payload: { message: "Script execution has finished" } }
    ]
  }));
});
