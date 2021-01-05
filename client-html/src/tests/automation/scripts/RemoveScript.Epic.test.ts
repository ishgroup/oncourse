import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  DELETE_SCRIPT_ENTITY_FULFILLED,
  deleteScriptItem,
  GET_SCRIPTS_LIST
} from "../../../js/containers/automation/containers/scripts/actions";
import { EpicDeleteScriptItem } from "../../../js/containers/automation/containers/scripts/epics/EpicDeleteScriptItem";

describe("Remove script epic tests", () => {
  it("EpicDeleteScriptItem should returns correct values", () => DefaultEpic({
    action: deleteScriptItem(1),
    epic: EpicDeleteScriptItem,
    processData: () => [
      {
        type: GET_SCRIPTS_LIST,
        payload: { selectFirst: true }
      },
      {
        type: DELETE_SCRIPT_ENTITY_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Script was deleted" }
      }
    ]
  }));
});
