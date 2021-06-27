import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  createScriptItem,
  getScriptsList,
  POST_SCRIPT_ENTITY_FULFILLED
} from "../../../js/containers/automation/containers/scripts/actions";
import { EpicCreateScriptItem } from "../../../js/containers/automation/containers/scripts/epics/EpicCreateScriptItem";
import { SCRIPT_EDIT_VIEW_FORM_NAME } from "../../../js/containers/automation/containers/scripts/constants";

describe("Create script epic tests", () => {
  it("EpicCreateScriptItem should returns correct values", () => {
    const script = {
      name: "script 21",
      enabled: true,
      nextRun: new Date().toISOString()
    };
    return DefaultEpic({
      action: createScriptItem(script, "Code"),
      epic: EpicCreateScriptItem,
      processData: () => [
        {
          type: POST_SCRIPT_ENTITY_FULFILLED
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "New Script created" }
        },
        getScriptsList(script.name),
        initialize(SCRIPT_EDIT_VIEW_FORM_NAME, script)
      ]
    });
  });
});
